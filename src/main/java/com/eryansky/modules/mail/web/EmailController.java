/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.mail.web;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.BaseController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresRoles;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.service.DiskManager;
import com.eryansky.modules.disk.service.FileManager;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.mail._enum.*;
import com.eryansky.modules.mail.entity.ContactGroup;
import com.eryansky.modules.mail.entity.Email;
import com.eryansky.modules.mail.entity.Inbox;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.entity.MailContact;
import com.eryansky.modules.mail.entity.Outbox;
import com.eryansky.modules.mail.entity.SendInfo;
import com.eryansky.modules.mail.service.ContactGroupManager;
import com.eryansky.modules.mail.service.EmailManager;
import com.eryansky.modules.mail.service.InboxManager;
import com.eryansky.modules.mail.service.MailAccountManager;
import com.eryansky.modules.mail.service.MailContactManager;
import com.eryansky.modules.mail.service.ReceiveInfoManager;
import com.eryansky.modules.mail.service.OutboxManager;
import com.eryansky.modules.mail.service.RecycleBinManager;
import com.eryansky.modules.mail.service.SendInfoManager;
import com.eryansky.modules.mail.task.MailAsyncTaskService;
import com.eryansky.modules.mail.utils.EmailUtils;
import com.eryansky.modules.mail.vo.EmailQueryVo;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.SelectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 邮件管理 Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/mail/email")
public class EmailController extends BaseController<Email, String> {

	@Autowired
	private EmailManager emailManager;
	@Autowired
	private OutboxManager outboxManager;
	@Autowired
	private RecycleBinManager recycleBinManager;
	@Autowired
	private InboxManager inboxManager;
    @Autowired
	private ReceiveInfoManager receiveInfoManager;
    @Autowired
	private SendInfoManager senderInfoManager;
    @Autowired
	private MailContactManager mailContactManager;
    @Autowired
	private ContactGroupManager contactGroupManager;
    @Autowired
	private MailAccountManager mailAccountManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private DiskManager diskManager;
	@Autowired
	private FileManager fileManager;
    @Autowired
	private MailAsyncTaskService mailAsyncTaskService;

    /**
     * 箱类型
     */
    public enum BoxType{
        UnreadInbox,Inbox, Outbox,Draftbox,RecycleBin,MailAccount//收件箱（未读）、收件箱、发件箱、草稿箱、回收站
    }

    /**
     * 操作类型
     */
    public enum OperateType{
        Send,SaveDraft,Repeat,Reply,QuickWriteMail//发送邮件、保存草稿 转发、回复 快速写邮件
    }

	@Override
	public EntityManager<Email, String> getEntityManager() {
		return emailManager;
	}

    @RequestMapping(value = { "" })
    public ModelAndView list(String emailId) {
        ModelAndView modelAndView = new ModelAndView("modules/mail/email");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        List<MailAccount> mailAccounts = mailAccountManager.findUserMailAcoounts(sessionInfo.getUserId(),AccountActivite.ACTIVITE.getValue());
        modelAndView.addObject("emailId", emailId);
        modelAndView.addObject("mailAccounts", mailAccounts);
        return modelAndView;
    }

    /**
     * 邮件数据列表
     * @param emailQueryVo 查询条件
     * @param boxType {@link com.eryansky.modules.mail.web.EmailController.BoxType}
     * @return
     */
	@RequestMapping(value = { "{boxType}Datagrid" })
	@ResponseBody
	public String userEmailDatagrid(EmailQueryVo emailQueryVo, @PathVariable BoxType boxType) {
		Page page = new Page(SpringMVCHolder.getRequest());// 分页对象
		SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        emailQueryVo.syncEndTime();

        if(emailQueryVo != null ){
            if(StringUtils.isBlank(emailQueryVo.getMailAccountId())){
                emailQueryVo.setMailAccountId(null);//全部邮件
            }if("1".equals(emailQueryVo.getMailAccountId())){
                emailQueryVo.setMailAccountId("");//站内邮件
            }

        }

        if (BoxType.Outbox.equals(boxType)) {// 发件箱
            page = outboxManager.findOutboxPageForUser(page, sessionInfo.getUserId(), emailQueryVo, false);
        } else if (BoxType.Draftbox.equals(boxType)) {//草稿箱
            page = outboxManager.findOutboxPageForUser(page, sessionInfo.getUserId(), emailQueryVo,true);
        } else if (BoxType.RecycleBin.equals(boxType)) {//回收站
            page = recycleBinManager.findRecycleBinPageForUser(page,
                    sessionInfo.getUserId(), emailQueryVo);
        } else if (BoxType.Inbox.equals(boxType) || BoxType.UnreadInbox.equals(boxType)) {//收件箱
            if (BoxType.UnreadInbox.equals(boxType)) {//未读
                emailQueryVo.setInboxReadStatus(EmailReadStatus.unreaded.getValue());
            }
            page = inboxManager.findInboxPageForUser(page, sessionInfo.getUserId(), emailQueryVo);
            Datagrid dg = new Datagrid(page.getTotalCount(),page.getResult());
            String json = JsonMapper.getInstance().toJson(dg, Inbox.class,
                    new String[]{"id", "emailId", "title", "summary","senderName", "sendTime", "priority", "priorityView", "isRead", "isReadView","receiveTime"});
            return json;
        } else if(BoxType.MailAccount.equals(boxType)){
            page = mailAccountManager.findPageUserMailAcoounts(sessionInfo.getUserId(), page);
        }
        Datagrid dg = new Datagrid(page.getTotalCount(),page.getResult());
        String json = JsonMapper.getInstance().toJson(dg);
		return json;
	}

    /**
     * 邮件阅读情况页面
     * @param id 邮件ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "readInfo/{id}" })
    public ModelAndView emailReadInfo(@PathVariable String id)
            throws Exception {
        ModelAndView modelAndView = new ModelAndView("modules/mail/email-readInfo");
        modelAndView.addObject("emailId", id);
        return modelAndView;
    }

    /**
     * 邮件阅读情况
     * @param id 邮件ID
     * @return
     */
    @RequestMapping(value = {"readInfoDatagrid/{id}"})
    @ResponseBody
    public String emailReadInfoDatagrid(@PathVariable String id) {
        Page<Inbox> page = new Page<Inbox>(SpringMVCHolder.getRequest());// 分页对象
        page = inboxManager.findPageByEmailId(page, id);
        Datagrid<Inbox> dg = new Datagrid<Inbox>(page.getTotalCount(), page.getResult());
        String json = JsonMapper.getInstance().toJson(dg,Inbox.class,new String[]{"userName","isReadView","readTime"});
        return json;
    }


    private void withPrefix(Integer receiveObjectType, String receiveObjectId, List<String> resultIds) {
        String _id = receiveObjectId;
        if (ReceiveObjectType.User.getValue().equals(receiveObjectType)) {
            _id = MailContactController.PREFIX_USER + receiveObjectId;
        }else if (ReceiveObjectType.UserGroup.getValue().equals(receiveObjectType)) {
            _id = MailContactController.PREFIX_USER_GROUP + receiveObjectId;
        }else if (ReceiveObjectType.Organ.getValue().equals(receiveObjectType)) {
            _id = MailContactController.PREFIX_ORGAN + receiveObjectId;
        }else if (ReceiveObjectType.Contact.getValue().equals(receiveObjectType)) {
            _id = MailContactController.PREFIX_MAILCONTACT + receiveObjectId;
        }else if (ReceiveObjectType.ContactGroup.getValue().equals(receiveObjectType)) {
            _id = MailContactController.PREFIX_CONTACT_GROUP + receiveObjectId;
        }
        resultIds.add(_id);
    }

    private List<String> withPrefix(List<SendInfo> sendInfos) {
        List<String> result = null;
        if (Collections3.isNotEmpty(sendInfos)) {
            result = new ArrayList<String>(sendInfos.size());
            for (SendInfo sendInfo : sendInfos) {
                withPrefix(sendInfo.getReceiveObjectType(), sendInfo.getReceiveObjectId(), result);
            }

        }
        return result;
    }

    /**
     * @param id 邮件ID
     * @param mailAccountId 发件账号Id 站内邮件为""
     * @param operateType {@link com.eryansky.modules.mail.web.EmailController.OperateType}
     * @param receiveObjectId 接收对象类型
     * @param receiveObjectId 接收对象
     * @return
     * @throws Exception
     *
     */
    @Mobile
    @RequestMapping(value = { "input" })
    public ModelAndView input(String id,String mailAccountId, OperateType operateType,Integer receiveObjectType,String receiveObjectId){
        ModelAndView modelAndView = new ModelAndView("modules/mail/email-input");
        SessionInfo sessionInfo  = SecurityUtils.getCurrentSessionInfo();
        List<File> files = null; // 挂接的附件
        List<String> fileIds = null;
        Email model = null;
        if (StringUtils.isNotBlank(id)){
            model = emailManager.loadById(id);
        }else{
            model = new Email();
        }
        if(model != null){
            fileIds = model.getFileIds();
            if (Collections3.isNotEmpty(fileIds)) {
                files = diskManager.findFilesByIds(fileIds);
            }
        }

        String toIds = JsonMapper.getInstance().toJson(" ");//接收人
        String ccIds = JsonMapper.getInstance().toJson(" ");//抄送人

        if(operateType == null && StringUtils.isNotBlank(id)){//编辑草稿
            Outbox outbox = model.getOutbox();
            mailAccountId = outbox.getMailAccountId();//忽略前台参数

            List<SendInfo> toSendInfos = senderInfoManager.findOutboxSendInfos(outbox.getId(), ReceiveType.TO.getValue());
            toIds = JsonMapper.getInstance().toJson(withPrefix(toSendInfos));

            List<SendInfo> ccSendInfos = senderInfoManager.findOutboxSendInfos(outbox.getId(), ReceiveType.CC.getValue());
            ccIds = JsonMapper.getInstance().toJson(withPrefix(ccSendInfos));

            //TODO
        }else{
            if (OperateType.Reply.equals(operateType)) {// 回复
                String sender = model.getSender();//发件人
                List<String> senderIds = new ArrayList<String>(1);
                senderIds.add(sender);
                Integer _receiveObjectType = null;
                if(MailType.System.getValue().equals(model.getMailType())){
                    _receiveObjectType = ReceiveObjectType.User.getValue();
                }else if(MailType.Mail.getValue().equals(model.getMailType())){
                    _receiveObjectType = ReceiveObjectType.Contact.getValue();
                }
                List<String> _toIds = new ArrayList<String>(1);
                withPrefix(_receiveObjectType,sender,_toIds);
                toIds = JsonMapper.getInstance().toJson(_toIds);

                model = model.reply();
                if(StringUtils.isBlank(mailAccountId)){//使用收件对应的账号回复邮件
                    mailAccountId = mailAccountManager.getUserReceiveMailAccountIdByEmailId(sessionInfo.getUserId(),id);
                }
                files = null;
            } else if (OperateType.Repeat.equals(operateType)) {// 转发
                String loginUserId = sessionInfo.getUserId();
                List<File> newFiles = null;// 新附件集合
                if (Collections3.isNotEmpty(fileIds)) {// 文件拷贝生成新的文件对象
                    List<File> sourceFiles = diskManager.findFilesByIds(fileIds);
                    newFiles = new ArrayList<File>(sourceFiles.size());
                    for (File sourceFile : sourceFiles) {
                        File newFile = sourceFile.copy();
                        newFile.setFolder(DiskUtils.getUserEmaliFolder(loginUserId));
                        newFile.setUserId(loginUserId);
                        newFile.setStatus(StatusState.LOCK.getValue());
                        newFiles.add(newFile);
                        diskManager.saveFile(newFile);
                    }
                }
                files = newFiles;
                model = model.repeat();
            }else if (OperateType.QuickWriteMail.equals(operateType)) {// 快速写邮件
                model = new Email();
                if(StringUtils.isBlank(mailAccountId)){
                    List<MailAccount> mailAccounts = mailAccountManager.findUserMailAcoounts(sessionInfo.getUserId(), AccountActivite.ACTIVITE.getValue());
                    if(Collections3.isNotEmpty(mailAccounts)){//取第一个邮件账号 TODO 指定账号
                        mailAccountId = mailAccounts.get(0).getId();
                    }
                }
                String result = emailManager.copyCheckAndSave(sessionInfo.getUserId(), receiveObjectType, receiveObjectId);//导入联系人
                List<String> _toIds = new ArrayList<String>(1);
                withPrefix(receiveObjectType,result,_toIds);
                toIds = JsonMapper.getInstance().toJson(_toIds);
            }
        }
        modelAndView.addObject("files", files);
        if (Collections3.isNotEmpty(files)) {
            modelAndView.addObject("fileIds", ConvertUtils.convertElementPropertyToString(files, "id", ","));
        }
        modelAndView.addObject("toIds",toIds);
        modelAndView.addObject("ccIds",ccIds);
        modelAndView.addObject("model", model);
        modelAndView.addObject("mailAccountId", mailAccountId);
        modelAndView.addObject("prioritys", EmailPriority.values());
        modelAndView.addObject("PREFIX_USER", MailContactController.PREFIX_USER);
        return modelAndView;
    }


    /**
	 * @param email
	 * @param operateType {@link com.eryansky.modules.mail.web.EmailController.OperateType}
	 * @param mailAccountId 邮箱帐号ID
	 * @param toIds 接收人
	 * @param ccIds 抄送人
	 * @return
	 */
	@RequestMapping(value = { "_save" })
	@ResponseBody
	public Result save(
			@ModelAttribute("model") Email email,OperateType operateType,
            String mailAccountId,
			@RequestParam(value = "toIds", required = false) List<String> toIds,
			@RequestParam(value = "ccIds", required = false) List<String> ccIds,
			@RequestParam(value = "_fileIds", required = false) List<String> fileIds) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		Result result = null;

        Outbox outbox = email.getOutbox();
        outbox.setUserId(sessionInfo.getUserId());
        outbox.setMailAccountId(mailAccountId);

        // 更新文件为有效状态 上传的时候为lock状态
        if (Collections3.isNotEmpty(fileIds)) {
            List<File> noticeFiles = diskManager.findFilesByIds(fileIds);
            for (File noticeFile : noticeFiles) {
                noticeFile.setStatus(StatusState.NORMAL.getValue());
                diskManager.updateFile(noticeFile);
            }
        }

        List<String> oldFileIds = null;// 原有文件的ID
        if (StringUtils.isNotBlank(email.getId())) {
            oldFileIds = emailManager.getFileIds(email.getId());// 原有文件的ID
        }
        List<String> newFileIds = fileIds;// 当前文件的ID
        List<String> removeFileIds = Lists.newArrayList();// 删除的文件ID
        if (Collections3.isEmpty(newFileIds)) {
            removeFileIds = oldFileIds;
        } else {
            if (Collections3.isNotEmpty(oldFileIds)) {
                for (String oldFileId : oldFileIds) {
                    if (!newFileIds.contains(oldFileId)) {
                        removeFileIds.add(oldFileId);
                    }
                }
            }

        }
        // 组件上移除文件
        if (Collections3.isNotEmpty(removeFileIds)) {
            fileManager.deleteFolderFiles(removeFileIds);
        }

        email.setMailType(MailType.System.getValue());
        email.setFileIds(newFileIds);
        email.setSender(sessionInfo.getUserId());
        emailManager.saveEmailAndOutbox(email, outbox);

        if(StringUtils.isNotBlank(outbox.getId())){
            List<SendInfo> sendInfoList = senderInfoManager.findOutboxSendInfos(outbox.getId(),null);
            senderInfoManager.deleteAll(sendInfoList);
        }

        saveSendInfos(toIds, outbox, email, ReceiveType.TO.getValue());
        saveSendInfos(ccIds, outbox, email, ReceiveType.CC.getValue());


        //发送邮件
        if (OperateType.Send.equals(operateType)) {
            outbox.setOutboxMode(OutboxMode.Sending.getValue());
            outboxManager.update(outbox);
            mailAsyncTaskService.sendEmail(email.getId());
        }

        result = Result.successResult();
		return result.setObj(email);
	}

    /**
     * 保持站内邮件发送信息
     * @param ids
     * @param outbox
     * @param email
     * @param receiveType
     */
    private void saveSendInfos(List<String> ids, Outbox outbox, Email email, Integer receiveType){
        if(Collections3.isNotEmpty(ids)) {
            for(String id : ids){
                String receieveObjectId = null;
                Integer receieveObjectType = null;
                if(StringUtils.startsWith(id,MailContactController.PREFIX_USER)){
                    receieveObjectType = ReceiveObjectType.User.getValue();
                    receieveObjectId = StringUtils.substringAfter(id,MailContactController.PREFIX_USER);
                }else if(StringUtils.startsWith(id,MailContactController.PREFIX_USER_GROUP)){
                    receieveObjectType = ReceiveObjectType.UserGroup.getValue();
                    receieveObjectId = StringUtils.substringAfter(id,MailContactController.PREFIX_USER_GROUP);
                }else if(StringUtils.startsWith(id,MailContactController.PREFIX_ORGAN)){
                    receieveObjectType = ReceiveObjectType.Organ.getValue();
                    receieveObjectId = StringUtils.substringAfter(id,MailContactController.PREFIX_ORGAN);
                }else if(StringUtils.startsWith(id,MailContactController.PREFIX_MAILCONTACT)){
                    receieveObjectType = ReceiveObjectType.Contact.getValue();
                    receieveObjectId = StringUtils.substringAfter(id,MailContactController.PREFIX_MAILCONTACT);
                }else if(StringUtils.startsWith(id,MailContactController.PREFIX_CONTACT_GROUP)){
                    receieveObjectType = ReceiveObjectType.ContactGroup.getValue();
                    receieveObjectId = StringUtils.substringAfter(id,MailContactController.PREFIX_CONTACT_GROUP);
                }
                SendInfo sendInfo = new SendInfo();
                sendInfo.setReceiveObjectType(receieveObjectType);
                sendInfo.setOutboxId(outbox.getId());
                sendInfo.setReceiveType(receiveType);
                sendInfo.setReceiveObjectId(receieveObjectId);
                senderInfoManager.save(sendInfo);
            }
        }

    }

    /**
     * 发送邮件
     * @param id 邮件ID
     * @return
     */
    @RequestMapping(value = { "sendEmail/{id}" })
    @ResponseBody
    public Result sendEmail(@PathVariable String id) {
        emailManager.sendEmail(id);
        return Result.successResult();
    }

    /**
     * 邮件删除
     * @param ids 收件箱ID集合、发件箱ID集合
     * @param boxType {@link com.eryansky.modules.mail.web.EmailController.BoxType}
     * @return
     */
    @RequestMapping(value = { "_remove" })
    @ResponseBody
    public Result _remove(
            @RequestParam(value = "ids", required = false) List<String> ids,BoxType boxType) {
        Result result = null;
        if (BoxType.Outbox.equals(boxType) || BoxType.Draftbox.equals(boxType)) {//发件箱(包含草稿箱)
            outboxManager.deleteToRecycleBin(ids);
        }else if (BoxType.Inbox.equals(boxType)) {//收件箱
            inboxManager.deleteToRecycleBin(ids);
        }
        result = Result.successResult();
        return result;
    }

    /**
     * 撤销邮件发送
     * @param id 邮件ID
     * @return
     */
    @RequestMapping(value = { "revoke/{id}" }, method = RequestMethod.POST)
    @ResponseBody
    public Result revoke(@PathVariable String id) {
        Result result = null;
        emailManager.revokeEmail(id);
        result = Result.successResult();
        return result;
    }



    /**
     * 标记为已读
     * @param inboxIds 收件箱ID集合
     * @return
     */
	@RequestMapping(value = { "markReaded" })
	@ResponseBody
	public Result markReaded(
			@RequestParam(value = "inboxIds", required = false) List<String> inboxIds) {
		Result result = null;
		inboxManager.markEmailReaded(inboxIds);
		result = Result.successResult();
		return result;
	}

    /**
     * 邮件查看
     * @param id
     * @param mailAccountId
     * @param boxType
     * @return
     */
    @Mobile
	@RequestMapping(value = { "view/{id}" })
	public ModelAndView view(@PathVariable String id,String mailAccountId,BoxType boxType,String recycleBinId,String outboxId,String inboxId) {
        ModelAndView modelAndView = new ModelAndView("modules/mail/email-view");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Email model = emailManager.loadById(id);
		List<File> files = null; // 挂接的附件

		if (Collections3.isNotEmpty(model.getFileIds())) {
			files = diskManager.findFilesByIds(model.getFileIds());
		}
        String userId = sessionInfo.getUserId();
          EmailUtils.setUserEmailRead(userId, id);

        modelAndView.addObject("model", model);
        modelAndView.addObject("files", files);
        modelAndView.addObject("mailAccountId", mailAccountId);
        modelAndView.addObject("boxType",boxType);
        modelAndView.addObject("recycleBinId",recycleBinId);
        modelAndView.addObject("outboxId",outboxId);
        modelAndView.addObject("inboxId",inboxId);
		return modelAndView;
	}


    /**
     * 回收站删除
     * @param recycleBinIds
     * @return
     */
	@RequestMapping(value = { "clearRecycleBin" })
	@ResponseBody
	public Result remove(
			@RequestParam(value = "recycleBinIds", required = true) List<String> recycleBinIds) {
		Result result = null;
        if(Collections3.isNotEmpty(recycleBinIds)){
            for(String recycleBinId:recycleBinIds){
                recycleBinManager.clearById(recycleBinId);
            }
        }
		result = Result.successResult();
		return result;
	}

    /**
     * 回收站恢复
     * @param recycleBinIds
     * @return
     */
	@RequestMapping(value = { "reduce" })
	@ResponseBody
	public Result reduce(
			@RequestParam(value = "recycleBinIds", required = true) List<String> recycleBinIds) {
		Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		recycleBinManager.recoveryByIds(sessionInfo.getUserId(),recycleBinIds);
		result = Result.successResult();
		return result;
	}

	/**
     * 邮件重要性 下拉列表
	 * @param selectType
	 *            {@link com.eryansky.utils.SelectType}
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "mailPriorityCombobox" })
	@ResponseBody
	public List<Combobox> mailPriorityCombobox(String selectType) throws Exception {
		List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
		EmailPriority[] _emums = EmailPriority.values();
		for (EmailPriority column : _emums) {
			Combobox combobox = new Combobox(column.getValue().toString(),
					column.getDescription());
			cList.add(combobox);
		}

		return cList;
	}




    /**
     * 邮件状态下拉列表
     *
     * @throws Exception
     */
    @RequestMapping(value = {"emailReadStatusCombobox"})
    @ResponseBody
    public List<Combobox> emailReadStatusCombobox(String selectType) {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        EmailReadStatus[] _enums = EmailReadStatus.values();
        for (int i = 0; i < _enums.length; i++) {
            if (!_enums[i].getValue().equals(EmailReadStatus.Deteled.getValue())) {
                Combobox combobox = new Combobox(_enums[i].getValue().toString(), _enums[i].getDescription());
                cList.add(combobox);
            }
        }
        return cList;
    }



	/**
	 * 邮件附件上传
	 * 
	 */
	@RequestMapping(value = { "upload" })
	@ResponseBody
	public Result upload(
			@RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile) {
		Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		try {
			if (uploadFile != null) {
				com.eryansky.modules.disk.entity.File file = DiskUtils
						.saveEmailFile(sessionInfo, uploadFile);
				file.setStatus(StatusState.LOCK.getValue());
				fileManager.update(file);
				result = Result.successResult().setObj(file.getId());
			} else {
				result = Result.errorResult().setMsg("文件路径丢失,上传失败！");
			}
		} catch (Exception e) {
			result = Result.errorResult().setMsg(
					"文件上传失败！" + ":" + e.getMessage());
		}
		return result;
	}

    /**
     * 删除邮件附件
     * @param email 邮件
     * @param fileId
     * @return
     * @throws Exception
     */
	@RequestMapping(value = { "delUpload" })
	@ResponseBody
	public Result delUpload(@ModelAttribute("model") Email email,
			@RequestParam(value = "fileId", required = false) String fileId)
			throws Exception {
		Result result = null;
        email.getFileIds().remove(fileId);
        getEntityManager().saveEntity(email);
        DiskUtils.deleteFile(fileId);
        result = Result.successResult();
		return result;
	}


    /**
     * 邮件管理页面 管理员
     * @return
     */
    @RequestMapping(value = { "manager" })
    public String emailManager() {
        return "modules/mail/email-manager";
    }


    /**
     * 邮件列表
     * @param emailQueryVo 查询条件
     * @return
     */
    @RequestMapping(value = { "emailDatagrid" })
    @ResponseBody
    public Datagrid<Email> emailDatagrid(EmailQueryVo emailQueryVo) {
        Page<Email> page = new Page<Email>(SpringMVCHolder.getRequest());// 分页对象
        emailQueryVo.syncEndTime();
        page = emailManager.findPage(page, emailQueryVo);
        Datagrid<Email> dg = new Datagrid<Email>(page.getTotalCount(),page.getResult());
        return dg;
    }

    /**
     * 删除邮件
     * @param ids 邮件ID集合
     * @return
     */
    @RequiresRoles(value = {AppConstants.ROLE_EMAIL_MANAGER})
    @RequestMapping(value = { "removeEmail" })
    @ResponseBody
    public Result removeEmail(@RequestParam(value = "ids", required = false) List<String> ids){
        Result result = null;
        for (String id : ids) {
            emailManager.removeEmailWithAll(id);
        }
        result = Result.successResult();
        return result;
    }

    /**
     * 未读邮件数量
     * @return
     */
    @RequestMapping(value = { "myMessage"})
    @ResponseBody
    public Result myMessage(HttpServletResponse response,String mailAccountId){
        WebUtils.setNoCacheHeader(response);
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(StringUtils.isBlank(mailAccountId)){
            mailAccountId = null;//全部邮件
        }else if("1".equals(mailAccountId)){
            mailAccountId = "";//站内邮件
        }

        long inboxs = inboxManager.getUserUnreadEmailNum(mailAccountId,sessionInfo.getUserId());
        Map<String,Long> map = Maps.newHashMap();
        map.put("inboxs", inboxs);
        result = Result.successResult().setObj(map);
        return result;
    }


    /**
     * 接收邮件
     * @param mailAccountId
     * @return
     */
    @RequestMapping(value = { "receiveMail"})
    @ResponseBody
    public Result receiveMail(String mailAccountId){
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
//        List<String> mailAccountIds;
//        if(StringUtils.isNotBlank(mailAccountId)){
//            mailAccountIds = new ArrayList<String>(1);
//            mailAccountIds.add(mailAccountId);
//
//        }else{
//            mailAccountIds = mailAccountManager.getUserMailAcoountIds(sessionInfo.getUserId(), AccountActivite.ACTIVITE.getValue());
//        }
        mailAsyncTaskService.receiveMail(sessionInfo.getUserId(), mailAccountId);

        return Result.successResult().setMsg("任务已提交，请稍后刷新页面!");
    }

    /**
     * 用户/联系人/联系人组 查看
     * @param receiveObjectId
     * @param receiveObjectType
     * @return
     */
    @RequestMapping(value = { "receiveObject"})
    public ModelAndView receiveObject(String receiveObjectId,Integer receiveObjectType){
        ModelAndView modelAndView = new ModelAndView("modules/mail/email-receiveObject");
        Object obj = null;
        if(ReceiveObjectType.User.getValue().equals(receiveObjectType)){
            User user = UserUtils.getUser(receiveObjectId);
            modelAndView.addObject("user", user);
        }else if(ReceiveObjectType.UserGroup.getValue().equals(receiveObjectType)){
            ContactGroup contactGroup = EmailUtils.getContactGroup(receiveObjectId);
            modelAndView.addObject("contactGroup",contactGroup);
        }else if(ReceiveObjectType.Organ.getValue().equals(receiveObjectType)){
            Organ organ = OrganUtils.getOrgan(receiveObjectId);
            modelAndView.addObject("organ",organ);
        }else if(ReceiveObjectType.Contact.getValue().equals(receiveObjectType)){
            MailContact mailContact = EmailUtils.getMailContact(receiveObjectId);
            modelAndView.addObject("mailContact",mailContact);
        }if(ReceiveObjectType.ContactGroup.getValue().equals(receiveObjectType)){
            ContactGroup contactGroup = EmailUtils.getContactGroup(receiveObjectId);
            modelAndView.addObject("contactGroup",contactGroup);
        }

        modelAndView.addObject("receiveObjectId",receiveObjectId);
        modelAndView.addObject("receiveObjectType",receiveObjectType);
        return modelAndView;
    }

    /**
     * 快速写邮件
     * @param receiveObjectId
     * @param receiveObjectType
     * @param mailAccountId 发件账号
     * @return
     */
    @Mobile
    @RequestMapping(value = { "_input" })
    public ModelAndView _input(String receiveObjectId,Integer receiveObjectType,String mailAccountId){
        ModelAndView modelAndView = new ModelAndView("modules/mail/email-input");
        SessionInfo sessionInfo  = SecurityUtils.getCurrentSessionInfo();
        List<File> files = null; // 挂接的附件
        List<String> fileIds = null;
        Email model = new Email();
        String toIds = JsonMapper.getInstance().toJson(" ");//接收人
        String ccIds = JsonMapper.getInstance().toJson(" ");//抄送人
        modelAndView.addObject("toIds",toIds);
        modelAndView.addObject("ccIds",ccIds);
        modelAndView.addObject("model", model);
        modelAndView.addObject("receiveObjectId", receiveObjectId);
        modelAndView.addObject("receiveObjectType", receiveObjectType);
        modelAndView.addObject("mailAccountId", mailAccountId);
        modelAndView.addObject("prioritys", EmailPriority.values());
        return modelAndView;
    }

}
