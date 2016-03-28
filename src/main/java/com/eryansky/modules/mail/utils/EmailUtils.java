/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.mail.utils;

import com.eryansky.common.mail.receiver.Receiver;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.disk.service.FileManager;
import com.eryansky.modules.mail._enum.EmailReadStatus;
import com.eryansky.modules.mail._enum.MailType;
import com.eryansky.modules.mail._enum.ReceiveObjectType;
import com.eryansky.modules.mail._enum.ReceiveType;
import com.eryansky.modules.mail.entity.*;
import com.eryansky.modules.mail.service.*;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.YesOrNo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Email操作简化工具类
 */
public class EmailUtils {

    protected static Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    private static EmailManager emailManager = SpringContextHolder.getBean(EmailManager.class);
    private static InboxManager inboxManager = SpringContextHolder.getBean(InboxManager.class);
    private static OutboxManager outboxManager = SpringContextHolder.getBean(OutboxManager.class);
    private static MailContactManager mailContactManager = SpringContextHolder.getBean(MailContactManager.class);
    private static ContactGroupManager contactGroupManager = SpringContextHolder.getBean(ContactGroupManager.class);
    private static MailAccountManager mailAccountManager = SpringContextHolder.getBean(MailAccountManager.class);
    private static ReceiveInfoManager receiveInfoManager = SpringContextHolder.getBean(ReceiveInfoManager.class);
    private static SendInfoManager senderInfoManager = SpringContextHolder.getBean(SendInfoManager.class);
    private static FileManager fileManager = SpringContextHolder.getBean(FileManager.class);
    private static OrganManager organManager = SpringContextHolder.getBean(OrganManager.class);
    private static UserManager userManager = SpringContextHolder.getBean(UserManager.class);

    public static final String MSG_REPLY = "回复：";
    public static final String MSG_REPEAT = "转发：";
    public static final String SEND_USER_UNKNOW = "匿名";

    private EmailUtils(){
    }

    /**
     * 外部邮件转入 收件箱 默认状态 未读(0) 已读(2)
     * @return
     */
    public static Integer getReceiveDefaultReadStatus() {
        String value =  AppConstants.getConfigValue("email.receiveDefaultReadStatus", EmailReadStatus.unreaded.getValue() + "");
        return Integer.valueOf(value);
    }

    /**
     *
     * @param emailId 邮件ID
     * @return
     */
    public static boolean isRead(String emailId) {
        return inboxManager.isRead(SecurityUtils.getCurrentSessionInfo().getUserId(), emailId);
    }

    /**
     * 判断是否有接收人读取了邮件
     * @param emailId 邮件ID
     * @return
     */
    public static boolean isReceivesRead(String emailId) {
        return emailManager.isReceivesRead(emailId);
    }

    public synchronized static void setUserEmailRead(String userId, String emailId){
        inboxManager.setUserEmailRead(userId,emailId);
    }

    public synchronized static void setRead(String inboxId){
        inboxManager.setRead(inboxId);
    }




    /**
     * 获取邮件
     * @param emailId 邮件ID
     * @return
     */
    public static Email getEmail(String emailId) {
        return emailManager.loadById(emailId);
    }

    /**
     * 获取邮件
     * @param mailContactId 联系人ID
     * @return
     */
    public static MailContact getMailContact(String mailContactId) {
        return mailContactManager.loadById(mailContactId);
    }

    /**
     * 联系人名称
     * @param mailContactId
     * @return
     */
    public static String getMailContactName(String mailContactId) {
        if(StringUtils.isBlank(mailContactId)){
            return null;
        }
        MailContact mailContact = getMailContact(mailContactId);
        if(mailContact != null){
//            return mailContact.getName() + "&lt;"+mailContact.getEmail()+"&gt;";
            return mailContact.getName();
        }
        return mailContactId;

    }

    /**
     * 获取邮件联系人分组
     * @param contactGroupId 联系人组ID
     * @return
     */
    public static ContactGroup getContactGroup(String contactGroupId) {
        return contactGroupManager.loadById(contactGroupId);
    }

    /**
     * 获取邮件联系人分组名称
     * @param contactGroupId
     * @return
     */
    public static String getContactGroupName(String contactGroupId) {
        if(StringUtils.isBlank(contactGroupId)){
            return null;
        }
        ContactGroup contactGroup = getContactGroup(contactGroupId);
        if(contactGroup != null){
            return contactGroup.getName();
        }
        return contactGroupId;

    }

    /**
     * 获取联系人信息
     * @param emailId
     * @param receiveType {@link ReceiveType}
     * @return
     */
    public static List<IContact> getEmailContacts(String emailId, Integer receiveType){
        Email email = getEmail(emailId);
        List list = null;
        if(MailType.System.getValue().equals(email.getMailType())){
            list = senderInfoManager.findMailSendInfos(emailId,receiveType);//List<SendInfo>
        }else if(MailType.Mail.getValue().equals(email.getMailType())){
            list = receiveInfoManager.findReceiveInfos(emailId,receiveType);//List<ReceiveInfo>
        }
        return list;
    }



    /**
     * 收件人名称
     * @param emailId
     * @return
     */
    public static String getToContactNames(String emailId){
        List<IContact> list = getEmailContacts(emailId, ReceiveType.TO.getValue());
        return ConvertUtils.convertElementPropertyToString(list, "nameView", ", ");
    }


    /**
     * 抄送人名称
     * @param emailId
     * @return
     */
    public static String getCcContactNames(String emailId){
        List<IContact> list = getEmailContacts(emailId, ReceiveType.CC.getValue());
        return ConvertUtils.convertElementPropertyToString(list, "nameView", ", ");
    }

    /**
     * 密送人名称
     * @param emailId
     * @return
     */
    public static String getBccContactNames(String emailId){
        List<IContact> list = getEmailContacts(emailId, ReceiveType.BCC.getValue());
        return ConvertUtils.convertElementPropertyToString(list, "nameView", ", ");
    }

    /**
     * 邮件发件人
     * @param emailId 邮件ID
     * @return
     */
    public static String getSenderName(String emailId) {
        Email email = emailManager.loadById(emailId);
        if(YesOrNo.YES.getValue().equals(email.getIsAnonymous())){
            return SEND_USER_UNKNOW;
        }
        String name = null;
        if(MailType.Mail.getValue().equals(email.getMailType())){
            name = getMailContactName(email.getSender());
        }else{
            name = UserUtils.getUserName(email.getSender());
        }
        return name;
    }

    /**
     * 得到邮件顶部信息
     * @param email
     * @return
     */
    public static String getEmail_TopInfo(Email email) {
        StringBuffer mailContent = new StringBuffer();
        Date sendTime = email.getCreateTime();
        String senderName = "";
        String toNames = "";
        StringBuffer toNamesSB = new StringBuffer("");
        Outbox outbox = outboxManager.getOutboxByEmailId(email.getId());
        if (outbox != null) {
            sendTime = email.getSendTime();
            senderName = email.getSenderName();
        }
        List<Inbox> inboxes = inboxManager.getInboxsByEmailId(email.getId());
        for (Inbox inbox : inboxes) {
            toNamesSB.append(inbox.getUserName()).append(
                    ";");
        }
        if (toNamesSB.toString().endsWith(";")) {
            toNames = toNamesSB.substring(0,
                    toNamesSB.length() - 1);
        }
        String emailContent;
        if (email.getContent().contains("'")) {
            emailContent = email.getContent().replace("'", "\\'");
        } else {
            emailContent = email.getContent();
        }
        mailContent
                .append("<br><br>")
                .append("<hr><div style=\"background: #EFEFEF;padding: 10px 10px 10px 10px;\">")
                .append("&nbsp;&nbsp;<strong>发件人：</strong>")
                .append(senderName).append("<br>")
                .append("&nbsp;&nbsp;<strong>发送时间：</strong>")
                .append(DateUtils.format(sendTime, DateUtils.DATE_TIME_FORMAT))
                .append("<br>").append("&nbsp;&nbsp;<strong>收件人：</strong>")
                .append(toNames).append("<br>")
                .append("&nbsp;&nbsp;<strong>主题：</strong>")
                .append(email.getTitle()).append("<br>").append("</div>")
                .append("<br>").append(emailContent);
        return mailContent.toString();
    }

    /**
     * 同步未读邮件到收件箱
     * @param userId
     * @param mailAccountId
     */
    public static synchronized void syncToInbox(String userId,String mailAccountId){
        Receiver receiver = null;
        try {
            MailAccount mailAccount = mailAccountManager.getById(mailAccountId);
            receiver = Receiver.make(mailAccount.toAccount());
            boolean open = receiver.open();
            Message[] messages = receiver.search(Receiver.SearchType.UNREAD);
            if(messages != null && messages.length >0){
                String[] uids = new String[messages.length];
                logger.info("mail[{}] count {} ",new Object[]{mailAccountId,messages.length});
                for (int i = 0; i < messages.length; i++) {
                    MimeMessage mimeMessage = (MimeMessage) messages[i];
                    uids[i] = receiver.getUID(mimeMessage);
                    try {
                        saveMessage(userId,mailAccountId,uids[i],messages[i]);
                    } catch (Exception e) {
                        logger.error("邮件["+uids[i]+"]解析异常：", e);
                    }

                }
//                saveMessages(userId,mailAccountId,uids,messages);
            }


        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        } finally {
            try {
                receiver.close();
            } catch (Exception e) {
            }
        }

    }

    /**
     * 保存邮件(批量)
     * @param userId
     * @param mailAccountId
     * @param uids
     * @param messages
     * @throws MessagingException
     */
    public static void saveMessages(String userId, String mailAccountId,String[] uids, Message[] messages) throws IOException, MessagingException {
        emailManager.saveMessages(userId, mailAccountId, uids, messages);
    }

    /**
     * 保存邮件
     * @param userId
     * @param mailAccountId
     * @param uid
     * @param message
     * @throws MessagingException
     */
    public static void saveMessage(String userId, String mailAccountId,String uid, Message message) throws IOException, MessagingException {
        saveMessage(userId, mailAccountId, uid, message, null);
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
    public static void saveMessage(String userId, String mailAccountId,String uid, Message message,Integer readStatus) throws IOException, MessagingException {
        emailManager.saveMessage(userId, mailAccountId, uid, message, readStatus);
    }

    /**
     * 发送邮件
     * @param mailAccountId 邮件账号ID
     * @param email 邮件
     * @param receiveInfos 接收人信息
     */
    public static void sendMail(String mailAccountId,Email email,List<ReceiveInfo> receiveInfos){
        emailManager.sendMail(mailAccountId, email, receiveInfos);
    }

    /**
     *  查找分组下的 用户
     * @param contactGroupId
     * @return
     */
    public static  List<User> findContactGroupUsers(String contactGroupId){
        return  contactGroupManager.findContactGroupUsers(contactGroupId);
    }

    /**
     * 查找分组下的邮件联系人
     * @param contactGroupId
     * @return
     */
    public static List<MailContact> findContactGroupMailContacts(String contactGroupId){
        return contactGroupManager.findContactGroupMailContacts(contactGroupId);
    }


    /**
     * 发送站内邮件
     *
     * @param title          邮件标题
     * @param content        邮件内容
     * @param sendUserId     发件人
     * @param receiveUserIds 收件人
     * @param ccUserIds      抄送人
     * @param bccUserIds     密送人
     */
    public static void sendEmail(String title,String content,
                                 String sendUserId,
                                 List<String> receiveUserIds,List<String> ccUserIds,List<String> bccUserIds) {
        Email email = new Email();
        email.setTitle(title);
        email.setContent(content);
        if(sendUserId == null){
            email.setIsAnonymous(YesOrNo.YES.getValue());
            email.setSendTime(Calendar.getInstance().getTime());
            emailManager.save(email);

        }else{
            email.setSender(sendUserId);
            Outbox outbox = email.getOutbox();
            outbox.setUserId(sendUserId);
            emailManager.saveEmailAndOutbox(email, outbox);
            SendInfo sendInfo = new SendInfo();
            sendInfo.setReceiveType(ReceiveType.TO.getValue());
            sendInfo.setReceiveObjectId(sendUserId);
            sendInfo.setOutboxId(outbox.getId());
            senderInfoManager.save(sendInfo);
        }


        Date nowTime = Calendar.getInstance().getTime();
        if(Collections3.isNotEmpty(receiveUserIds)){
            for(String receiveUserId:receiveUserIds){
                ReceiveInfo receiveInfo = new ReceiveInfo();
                receiveInfo.setReceiveObjectType(ReceiveObjectType.User.getValue());
                receiveInfo.setReceiveObjectId(receiveUserId);
                receiveInfo.setEmailId(email.getId());
                receiveInfo.setReceiveType(ReceiveType.TO.getValue());
                receiveInfoManager.save(receiveInfo);
                Inbox inbox = new Inbox();
                inbox.setEmailId(email.getId());
                inbox.setUserId(receiveUserId);
                inbox.setReceiveTime(nowTime);
                inbox.setReceiveType(ReceiveType.TO.getValue());
                inbox.setIsRead(EmailReadStatus.unreaded.getValue());
                inboxManager.save(inbox);
            }
        }

        if(Collections3.isNotEmpty(ccUserIds)){
            for(String ccUserId:ccUserIds){
                ReceiveInfo receiveInfo = new ReceiveInfo();
                receiveInfo.setReceiveObjectType(ReceiveObjectType.User.getValue());
                receiveInfo.setReceiveObjectId(ccUserId);
                receiveInfo.setEmailId(email.getId());
                receiveInfo.setReceiveType(ReceiveType.TO.getValue());
                receiveInfoManager.save(receiveInfo);
                Inbox inbox = new Inbox();
                inbox.setEmailId(email.getId());
                inbox.setUserId(ccUserId);
                inbox.setReceiveTime(nowTime);
                inbox.setReceiveType(ReceiveType.CC.getValue());
                inbox.setIsRead(EmailReadStatus.unreaded.getValue());
                inboxManager.save(inbox);
            }
        }

        if(Collections3.isNotEmpty(bccUserIds)){
            for(String bccUserId:bccUserIds){
                ReceiveInfo receiveInfo = new ReceiveInfo();
                receiveInfo.setReceiveObjectType(ReceiveObjectType.User.getValue());
                receiveInfo.setReceiveObjectId(bccUserId);
                receiveInfo.setEmailId(email.getId());
                receiveInfo.setReceiveType(ReceiveType.BCC.getValue());
                receiveInfoManager.save(receiveInfo);
                Inbox inbox = new Inbox();
                inbox.setEmailId(email.getId());
                inbox.setUserId(bccUserId);
                inbox.setReceiveTime(nowTime);
                inbox.setReceiveType(ReceiveType.BCC.getValue());
                inbox.setIsRead(EmailReadStatus.unreaded.getValue());
                inboxManager.save(inbox);
            }
        }
    }

    /**
     * 发送系统邮件
     *
     * @param title          邮件标题
     * @param content        邮件内容
     * @param receiveUserIds 收件人
     * @param ccUserIds      抄送人
     * @param bccUserIds     密送人
     */
    public static void sendSystemEmail(String title,String content,
                                       List<String> receiveUserIds,List<String> ccUserIds,List<String> bccUserIds) {
        User superUser = userManager.getSuperUser();//发件人为 系统管理员
        sendEmail(title, content, superUser.getId(), receiveUserIds, ccUserIds, bccUserIds);
    }

    /**
     * 发送系统邮件
     *
     * @param title          邮件标题
     * @param content        邮件内容
     * @param receiveUserIds 收件人
     */
    public static void sendSystemEmail(String title,String content,List<String> receiveUserIds) {
        User superUser = userManager.getSuperUser();//发件人为 系统管理员
        sendEmail(title, content, superUser.getId(), receiveUserIds, null, null);
    }


    /**
     * 发送系统邮件
     *
     * @param title         邮件标题
     * @param content       邮件内容
     * @param receiveUserId 收件人
     */
    public static void sendSystemEmail(String title,String content,String receiveUserId) {
        User superUser = userManager.getSuperUser();//发件人为 系统管理员
        List<String> receiveUserIds = new ArrayList<String>(0);
        receiveUserIds.add(receiveUserId);
        sendEmail(title, content, superUser.getId(), receiveUserIds, null, null);
    }

    /**
     * 发送匿名邮件
     *
     * @param title         邮件标题
     * @param content       邮件内容
     * @param receiveUserId 收件人
     */
    public static void sendAnonymousEmail(String title, String content, String receiveUserId) {
        List<String> receiveUserIds = new ArrayList<String>(0);
        receiveUserIds.add(receiveUserId);
        sendAnonymousEmail(title, content, receiveUserIds);
    }

    /**
     * 发送匿名邮件
     * @param title 邮件标题
     * @param content 邮件内容
     * @param receiveUserIds 收件人
     */
    public static void sendAnonymousEmail(String title,String content,List<String> receiveUserIds) {
        sendEmail(title, content, null, receiveUserIds, null, null);
    }
}
