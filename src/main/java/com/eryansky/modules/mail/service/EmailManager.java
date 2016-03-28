/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.mail.service;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.mail.config.ServerConfig;
import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.receiver.Receiver;
import com.eryansky.common.mail.sender.Sender;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Lists;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.service.FileManager;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.mail._enum.*;
import com.eryansky.modules.mail.entity.*;
import com.eryansky.modules.mail.utils.EmailUtils;
import com.eryansky.modules.mail.utils.MailUtils;
import com.eryansky.modules.mail.vo.EmailQueryVo;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.UserManager;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * service
 * 
 * @author xush
 */
@Service
public class EmailManager extends EntityManager<Email, String> {
	private static final Logger logger = LoggerFactory
			.getLogger(EmailManager.class);

	private HibernateDao<Email, String> emailDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private OrganManager organManager;
	@Autowired
	private InboxManager inboxManager;
	@Autowired
	private OutboxManager outboxManager;
    @Autowired
    private RecycleBinManager recycleBinManager;
	@Autowired
	private MailAccountManager mailAccountManager;
	@Autowired
	private ContactGroupManager contactGroupManager;
	@Autowired
	private ReceiveInfoManager receiveInfoManager;
	@Autowired
	private SendInfoManager senderInfoManager;
	@Autowired
	private MailContactManager mailContactManager;
	@Autowired
	private FileManager fileManager;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		emailDao = new HibernateDao<Email, String>(sessionFactory, Email.class);
	}

	@Override
	protected HibernateDao<Email, String> getEntityDao() {
		return emailDao;
	}

	/**
	 * 检察邮件是否存在
	 * @param uid
	 * @return
	 */
	public String checkEmailExist(String uid) {
		Validate.notNull(uid, "参数[uid]不能为null.");
		Parameter parameter = new Parameter(uid, StatusState.NORMAL.getValue());
		StringBuffer hql = new StringBuffer();
		hql.append("select e.id from Email e where e.uid  = :p1 and e.status = :p2");//MySQL 忽略了大小写
//		hql.append("select e.id from Email e where e.uid like :p1 and e.status = :p2");
		List<String> list = getEntityDao().find(hql.toString(), parameter);
		return list.isEmpty() ? null:list.get(0);
	}

	/**
	 * 检查用户收件箱是否存在某个邮件
	 * @param userId
	 * @param mailAccountId
	 * @param uid
	 * @return
	 */
	public boolean checkUserInboxEmailExist(String userId, String mailAccountId,String uid) {
		Validate.notNull(uid, "参数[uid]不能为null.");
		boolean flag = false;
		Parameter parameter = new Parameter(uid,mailAccountId,userId);
		StringBuffer hql = new StringBuffer();
		hql.append("select e from Email e where e.uid = :p1 and e.id in (")
			.append("select i.emailId from Inbox i where i.mailAccountId = :p2 and i.userId = :p3")
			.append(")");
		List<Email> list = getEntityDao().find(hql.toString(),parameter);
		flag = !list.isEmpty();
		return flag;
	}



	/**
	 * 判断邮件是否有人读了
	 * 
	 * @param emailId 邮件ID
	 * @return
	 */
	public boolean isReceivesRead(String emailId) {
		Validate.notNull(emailId, "参数[emailId]不能为null.");
		Parameter parameter = new Parameter(emailId, EmailReadStatus.readed.getValue());
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) from Inbox i where i.emailId = :p1 and i.isRead = :p2");
		List<Object> list = getEntityDao().find(hql.toString(), parameter);
        Long count = (Long) list.get(0);
		if (count > 0L) {
            return true;
		}
		return false;
	}

	/**
	 * 撤销邮件
	 * 
	 * @param emailId
	 *            邮件ID
	 */
    @Logging(value = "撤销邮件[{0}]")
	public void revokeEmail(String emailId) {
		Validate.notNull(emailId, "参数[emailId]不能为null.");
		if (this.isReceivesRead(emailId)) {
			throw new ServiceException("邮件已查看，不能执行撤销操作！");
		} else {
			List<Inbox> inboxList = inboxManager.getInboxsByEmailId(emailId);
			inboxManager.deleteAll(inboxList);
			receiveInfoManager.deleteReceiveInfos(emailId,null);
			outboxManager.setEmailStatus(emailId, OutboxMode.Draft.getValue());
		}

	}

	/**
	 * 邮件保存
	 * 
	 * @param email
	 * @param outbox
	 * @throws Exception
	 */
	public void saveEmailAndOutbox(Email email, Outbox outbox) {
        this.saveEntity(email);
        outbox.setEmailId(email.getEmailId());
        outboxManager.saveOrUpdate(outbox);
	}

	/**
	 * 去除重复 emailId、receiveObjectType、receiveObjectId 同一邮件
	 * @param receiveInfos
	 * @param receiveInfo
	 */
	private void checkReceiveInfoAdd(List<ReceiveInfo> receiveInfos,ReceiveInfo receiveInfo){
		boolean flag = false;
		for(ReceiveInfo r:receiveInfos){
			if(r.getReceiveObjectType().equals(receiveInfo.getReceiveObjectType())
					&& r.getReceiveObjectId().equals(receiveInfo.getReceiveObjectId())){
				flag = true;
				break;
			}

		}
		if(!flag){
			receiveInfos.add(receiveInfo);
		}

	}

	/**
	 * 发送邮件
	 * @param emailId
	 */
    @Logging(value = "发送邮件[{0}]")
	public void sendEmail(String emailId) {
		Date nowTime = Calendar.getInstance().getTime();
		Email email = getById(emailId);
		email.setSendTime(nowTime);
        Outbox outbox = outboxManager.getOutboxByEmailId(emailId);
		//根据发送配置信息生成接收信息
		List<SendInfo> sendInfos = senderInfoManager.findOutboxSendInfos(outbox.getId(),null);
		List<ReceiveInfo>  receiveInfos = Lists.newArrayList();
		for(SendInfo sendInfo : sendInfos){
			Integer receiveObjectType = null;
			List<String> receieveObjectIds = Lists.newArrayList();
			if(ReceiveObjectType.User.getValue().equals(sendInfo.getReceiveObjectType())){
				receieveObjectIds.add(sendInfo.getReceiveObjectId());
				receiveObjectType = sendInfo.getReceiveObjectType();
			}else if(ReceiveObjectType.Organ.getValue().equals(sendInfo.getReceiveObjectType())){
				List<String> userIds = organManager.findOrganUserIds(sendInfo.getReceiveObjectId());
				receiveObjectType = ReceiveObjectType.User.getValue();
				receieveObjectIds = userIds;
			}else if(ReceiveObjectType.UserGroup.getValue().equals(sendInfo.getReceiveObjectType())){
				ContactGroup contactGroup = contactGroupManager.loadById(sendInfo.getReceiveObjectId());
				receiveObjectType = ReceiveObjectType.User.getValue();
				receieveObjectIds = contactGroup.getObjectIds();
			}else if(ReceiveObjectType.Contact.getValue().equals(sendInfo.getReceiveObjectType())){
				receieveObjectIds.add(sendInfo.getReceiveObjectId());
				receiveObjectType = sendInfo.getReceiveObjectType();
			}else if(ReceiveObjectType.ContactGroup.getValue().equals(sendInfo.getReceiveObjectType())){
				ContactGroup contactGroup = contactGroupManager.loadById(sendInfo.getReceiveObjectId());
				receiveObjectType = ReceiveObjectType.Contact.getValue();
				receieveObjectIds = contactGroup.getObjectIds();
			}
			for(String receieveObjectId:receieveObjectIds){
				ReceiveInfo receiveInfo = new ReceiveInfo();
				receiveInfo.setReceiveObjectType(receiveObjectType);
				receiveInfo.setReceiveObjectId(receieveObjectId);
				receiveInfo.setEmailId(emailId);
				receiveInfo.setReceiveType(sendInfo.getReceiveType());
				checkReceiveInfoAdd(receiveInfos, receiveInfo);

			}

		}

		receiveInfoManager.saveOrUpdate(receiveInfos);

		//根据发送信息 发送邮件（内部邮件）
		List<ReceiveInfo>  mailReceiveInfos = Lists.newArrayList();
		for(ReceiveInfo receiveInfo :receiveInfos){
			if(ReceiveObjectType.User.getValue().equals(receiveInfo.getReceiveObjectType())){
				Inbox inbox = new Inbox();
				inbox.setEmailId(emailId);
				inbox.setUserId(receiveInfo.getReceiveObjectId());
				inbox.setReceiveTime(nowTime);
				inbox.setReceiveType(receiveInfo.getReceiveType());
				inbox.setIsRead(EmailReadStatus.unreaded.getValue());
				inboxManager.save(inbox);
			}else{
				//TODO 调用邮件系统发送
				mailReceiveInfos.add(receiveInfo);
			}

		}

		//发送邮件（调用第三方）
		if(Collections3.isNotEmpty(mailReceiveInfos)){
			EmailUtils.sendMail(email.getOutbox().getMailAccountId(), email, mailReceiveInfos);
		}


        outbox.setOutboxMode(OutboxMode.Sent.getValue());
		outboxManager.update(outbox);
	}

	/**
	 * 查找附件ID
	 * 
	 * @param emailId
	 * @return
	 */
	public List<String> getFileIds(String emailId) {
		List<Email> list = getEntityDao().find(
				"select e from Email e where e.id = :p1",
				new Parameter(emailId));
		Email email = list.isEmpty() ? null : list.get(0);
		if (email != null) {
			return email.getFileIds();
		}
		return null;
	}

    /**
     * 邮件管理查询 来自于发件箱
     * @param page
     * @param emailQueryVo
     * @return
     */
    public Page<Email> findPage(Page<Email> page, EmailQueryVo emailQueryVo) {
        StringBuilder hql = new StringBuilder();
        Parameter parameter = new Parameter(StatusState.DELETE.getValue());
        hql.append("select e from Email e where e.status <> :p1");
        if(emailQueryVo != null){
            if(StringUtils.isNotBlank(emailQueryVo.getTitle())){
                hql.append(" and e.title like :title");
                parameter.put("title","%"+emailQueryVo.getTitle()+"%");
            }
            if(StringUtils.isNotBlank(emailQueryVo.getContent())){
                hql.append(" and e.content like :content");
                parameter.put("content","%" + emailQueryVo.getContent() + "%");
            }

			if(Collections3.isNotEmpty(emailQueryVo.getSendObjectIds())){
				hql.append(" and e.sender in (:senderIds)");
				parameter.put("senderIds",emailQueryVo.getSendObjectIds());
			}

            if (emailQueryVo.getStartTime() != null && emailQueryVo.getEndTime() != null) {
                hql.append(" and  (e.sendTime between :startTime and :endTime)");
                parameter.put("startTime", emailQueryVo.getStartTime());
                parameter.put("endTime", emailQueryVo.getEndTime());
            }
        }

        return getEntityDao().findPage(page, hql.toString(), parameter);

    }

	/**
	 * 删除邮件 级联清空 回收站、收件箱发件箱（包括草稿）、收件箱、邮件附件、邮件
	 * 
	 * @param emailIds　邮件ID集合
	 */
	public void removeEmailWithAll(List<String> emailIds) {
		if (Collections3.isNotEmpty(emailIds)) {
			for (String emailId : emailIds) {
                this.removeEmailWithAll(emailId);
			}
		}
	}

    /**
     * 删除邮件 级联清空  回收站、收件箱发件箱（包括草稿）、收件箱、邮件附件、邮件
     *
     * @param emailId 邮件ID
     */
    @Logging(value = "删除邮件[{0}]")
    public void removeEmailWithAll(String emailId) {
        Email email = this.loadById(emailId);
        recycleBinManager.deleteByEmailId(emailId);
		inboxManager.deleteByEmailId(emailId);
		senderInfoManager.deleteByEmailId(emailId);
		receiveInfoManager.deleteByEmailId(emailId);
		outboxManager.deleteByEmailId(emailId);

        List<String> fileIds = email.getFileIds();
        if (Collections3.isNotEmpty(fileIds)) {
            for (String fileId : fileIds) {
                DiskUtils.deleteFile(fileId);
            }
        }
        this.delete(email);
    }

    /**
     * 删除邮件 仅考虑 邮件本身以及邮件附件
     * @param emailId
     */
    public void removeEmail(String emailId) {
        Email email = this.loadById(emailId);
        List<String> fileIds = email.getFileIds();
        if (Collections3.isNotEmpty(fileIds)) {
            for (String fileId : fileIds) {
                DiskUtils.deleteFile(fileId);
            }
        }
        this.delete(email);
    }


	/**
	 * 同步未读邮件到收件箱
	 * @param userId
	 * @param mailAccountId
	 */
	public void syncToInbox(String userId,String mailAccountId){
		Receiver receiver = null;
		MailAccount mailAccount = mailAccountManager.getById(mailAccountId);

		try {
			receiver = Receiver.make(mailAccount.toAccount());
			boolean open = receiver.open();
			logger.info(mailAccount.getMailAddress()+" open:"+open);
			Message[] list = receiver.search(Receiver.SearchType.UNREAD);
			logger.info("mail count "+list.length);
			String[] uuids = new String[list.length];
			for(int i=0;i<list.length;i++){
				uuids[i] = receiver.getUID((MimeMessage) list[i]);

			}
			saveMessages(userId,mailAccountId,uuids,list);
		} catch (Exception e) {
			logger.error("邮件读取异常：",e);
			throw new SystemException(e);
		} finally {
			receiver.close();
		}

	}

	/**
	 * 保存邮件(批量)
	 * @param userId
	 * @param mailAccountId
	 * @param uids
	 * @param messages
	 * @throws IOException
	 * @throws MessagingException
	 */
	public void saveMessages(String userId, String mailAccountId, String[] uids, Message[] messages) throws IOException, MessagingException {
		for(int i=0;i<messages.length;i++){
			saveMessage(userId,mailAccountId, uids[i],messages[i],null);
		}
	}

	/**
	 * 保存邮件
	 * @param userId
	 * @param mailAccountId
	 * @param uid
	 * @param message
	 * @param readStatus
	 * @throws IOException
	 * @throws MessagingException
	 */
	public void saveMessage(String userId, String mailAccountId, String uid, Message message,Integer readStatus) throws IOException, MessagingException {
		boolean exist = this.checkUserInboxEmailExist(userId,mailAccountId, uid);
		if(exist){
			return;
		}

		Email email = new Email();
		MimeMessage mimeMessage = (MimeMessage)message;
		String title = MailUtils.getSubject(mimeMessage);
		email.setTitle(title);
		StringBuffer content = new StringBuffer(30);
		MailUtils.getMailTextContent(mimeMessage, content);
		email.setContent(content.toString());
//				email.setContent(EncodeUtils.htmlEscape(content.toString()));
		email.setMailType(MailType.Mail.getValue());
		email.setEmailSize(Long.valueOf(mimeMessage.getSize()));
		email.setUid(uid);
		List<String> files = Lists.newArrayList();
		if(MailUtils.isContainAttachment(mimeMessage)){
			MailUtils.saveAttachment(userId, mimeMessage, files);
		}
		email.setFileIds(files);

		InternetAddress sender = MailUtils.getFrom(mimeMessage);

		MailContact mailContact = checkAndSaveContact(userId,sender);
		email.setSendTime(mimeMessage.getSentDate());
		email.setSender(mailContact.getId());
		this.save(email);

		List<InternetAddress> rAddress = MailUtils.getRecipientAddress(mimeMessage, Message.RecipientType.TO);
		receive(ReceiveType.TO.getValue(), userId, email.getId(), rAddress);
		rAddress = MailUtils.getRecipientAddress(mimeMessage, Message.RecipientType.CC);
		receive(ReceiveType.CC.getValue(), userId, email.getId(), rAddress);
		rAddress = MailUtils.getRecipientAddress(mimeMessage, Message.RecipientType.BCC);
		receive(ReceiveType.BCC.getValue(), userId, email.getId(), rAddress);

		Inbox inbox = new Inbox();
		inbox.setMailAccountId(mailAccountId);
		inbox.setUserId(userId);
		inbox.setEmailId(email.getId());
		Integer _readStatus = readStatus;
		if(_readStatus == null){
			_readStatus = EmailUtils.getReceiveDefaultReadStatus();
		}
		inbox.setIsRead(_readStatus);
		inbox.setReceiveType(ReceiveType.TO.getValue());
//				inbox.setReceiveTime(message.getReceivedDate() == null ? Calendar.getInstance().getTime() : message.getReceivedDate());
		inbox.setReceiveTime(mimeMessage.getReceivedDate() == null ? mimeMessage.getSentDate() : mimeMessage.getReceivedDate());
		inboxManager.save(inbox);
	}

	private MailContact checkAndSaveContact(String userId,InternetAddress address){
		String name = address.getPersonal();
		String addres = MailUtils.decode(address.getAddress());
		MailContact mailContact = mailContactManager.checkUserMailContactExist(userId, addres);
		if(mailContact == null){
			mailContact = new MailContact();
			mailContact.setEmail(addres);
			mailContact.setUserId(userId);
			if(StringUtils.isBlank(name)){
				name = StringUtils.substringBefore(addres,"@");
			}
			mailContact.setName(name);
			mailContactManager.save(mailContact);
			ContactGroup contactGroup = contactGroupManager.saveDefaultMailContactGroupIfNotExist(userId);
			if(!contactGroup.getObjectIds().contains(mailContact.getId())){
				contactGroup.getObjectIds().add(mailContact.getId());
				contactGroupManager.update(contactGroup);
			}
		}
		return mailContact;
	}
	private void receive(Integer receiveType, String userId, String emailId, List<InternetAddress> rAddress){
		if(Collections3.isNotEmpty(rAddress)){
			for(InternetAddress addresjoin:rAddress){
				MailContact mailContact = checkAndSaveContact(userId, addresjoin);

				ReceiveInfo receiveInfo = receiveInfoManager.checkExistReceiveObjectId(emailId, mailContact.getId(), receiveType);
				if(receiveInfo == null){
					receiveInfo = new ReceiveInfo();
					receiveInfo.setReceiveObjectId(mailContact.getId());
					receiveInfo.setEmailId(emailId);
					receiveInfo.setReceiveType(receiveType);
					receiveInfo.setReceiveObjectType(ReceiveObjectType.Contact.getValue());
					receiveInfoManager.save(receiveInfo);
				}
			}
		}
	}



	public void sendMail(String mailAccountId,Email email,List<ReceiveInfo> receiveInfos){
		Sender sender = null;
		try {
			MailAccount mailAccount = mailAccountManager.loadById(mailAccountId);
			Account account = mailAccount.toAccount();
			sender = Sender.make(account);
			sender.open();
			MimeMessage message = new MimeMessage(sender.getSession());
			// 设置优先级
			message.addHeader("X-Priority", String.valueOf(email.getPriority()));
			if (account.getSenderServer().getContentType().equals(ServerConfig.ContentType.HTML)) {
				message.addHeader("Content-type", "text/html");
			} else {
				message.addHeader("Content-type", "text/plain");
			}

			message.setSubject(email.getTitle(), account.getSenderServer().getCharset());
			Multipart mp = new MimeMultipart();
			MimeBodyPart contentPart = new MimeBodyPart();
			if (account.getSenderServer().getContentType().equals(ServerConfig.ContentType.HTML)) {
				contentPart.setContent(email.getContent(), "text/html;charset="
						+ account.getSenderServer().getCharset());
			} else {
				contentPart.setText(email.getContent(), account.getSenderServer().getCharset());
			}
			mp.addBodyPart(contentPart);
			message.setContent(mp);


			if(Collections3.isNotEmpty(email.getFileIds())){
				MimeBodyPart attachPart;
				for (String fileId : email.getFileIds()) {
					attachPart = new MimeBodyPart();
					File file = fileManager.getById(fileId);
					java.io.File diskFile = file.getDiskFile();
					FileDataSource fds = new FileDataSource(diskFile);
					attachPart.setDataHandler(new DataHandler(fds));
					if (file.getName().contains("$")) {
						attachPart.setFileName(MimeUtility.encodeWord(file.getName().substring(
								file.getName().indexOf("$") + 1,
								file.getName().length())));
					} else {
						attachPart.setFileName(MimeUtility.encodeWord(file.getName()));
					}
					mp.addBodyPart(attachPart);
				}
			}


			message.setFrom(new InternetAddress(mailAccountManager.loadById(mailAccountId).getMailAddress(), email.getSenderName()));

			//设置接收、抄送、密送人
			for(ReceiveInfo receiveInfo:receiveInfos){
				if(ReceiveType.TO.getValue().equals(receiveInfo.getReceiveType())){
					message.addRecipients(Message.RecipientType.TO, receiveInfo.getContactEmail());
				}else if(ReceiveType.CC.getValue().equals(receiveInfo.getReceiveType())){
					message.addRecipients(Message.RecipientType.CC, receiveInfo.getContactEmail());
				}else if(ReceiveType.BCC.getValue().equals(receiveInfo.getReceiveType())){
					message.addRecipients(Message.RecipientType.BCC, receiveInfo.getContactEmail());
				}

			}

			message.setSentDate(Calendar.getInstance().getTime());
			sender.send(message);

		} catch (Exception e) {
			throw new SystemException("发送邮件["+email.getTitle()+"]失败",e);
		} finally {
			if (sender != null) {
				sender.close();
			}
		}
	}

	/**
	 *
	 * @param userId
	 * @param receiveObjectType {@link ReceiveObjectType}
	 * @param receiveObjectId
	 */
	public String copyCheckAndSave(String userId, Integer receiveObjectType, String receiveObjectId){
		String result = receiveObjectId;
		if (ReceiveObjectType.User.getValue().equals(receiveObjectType)) {

		}else if (ReceiveObjectType.UserGroup.getValue().equals(receiveObjectType)) {
			ContactGroup contactGroup = contactGroupManager.loadById(receiveObjectId);
			ContactGroup userContactGroup = contactGroupManager.checkExist(userId, ContactGroupType.Mail.getValue(), contactGroup.getName(), null);
			if(userContactGroup == null){
				userContactGroup = contactGroup.copy(userId);
				contactGroupManager.save(userContactGroup);
			}else{
				logger.warn("已存在用户组[{}]", new Object[]{contactGroup.getName()});
			}
			result = userContactGroup.getId();
		}else if (ReceiveObjectType.Organ.getValue().equals(receiveObjectType)) {
		}else if (ReceiveObjectType.Contact.getValue().equals(receiveObjectType)) {
			MailContact mailContact = mailContactManager.loadById(receiveObjectId);
			MailContact userMailContact = mailContactManager.checkUserMailContactExist(userId, mailContact.getEmail());
			if(userMailContact == null){
				ContactGroup contactGroup = contactGroupManager.saveDefaultMailContactGroupIfNotExist(userId);
				userMailContact = mailContact.copy(contactGroup.getId());
				mailContactManager.save(userMailContact);
			}else{
				logger.warn("已存在联系人[{}:{}]",new Object[]{mailContact.getName(),mailContact.getEmail()});
			}
			result =  userMailContact.getId();
		}else if (ReceiveObjectType.ContactGroup.getValue().equals(receiveObjectType)) {
			ContactGroup contactGroup = contactGroupManager.loadById(receiveObjectId);
			ContactGroup userContactGroup = contactGroupManager.checkExist(userId, ContactGroupType.System.getValue(), contactGroup.getName(), null);
			if(userContactGroup == null){
				userContactGroup = contactGroup.copy(userId);
				contactGroupManager.save(userContactGroup);
				List<MailContact> mailContacts = mailContactManager.findGroupMailContacts(contactGroup.getId());
				for(MailContact mailContact:mailContacts){
					MailContact m = mailContact.copy(contactGroup.getId());
					mailContactManager.save(m);
				}
			}else{
				logger.warn("已存在用户组[{}]", new Object[]{contactGroup.getName()});
			}
			result =  userContactGroup.getId();

		}
		return result;
	}
}
