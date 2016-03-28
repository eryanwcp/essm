/**
 * Copyright (c) 2013-2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.entity;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.PrettyMemoryUtils;
import com.eryansky.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.eryansky.modules.disk.utils.FileUtils;
import com.eryansky.modules.mail._enum.EmailPriority;
import com.eryansky.modules.mail._enum.MailType;
import com.eryansky.modules.mail.entity.common.IEmail;
import com.eryansky.modules.mail.service.InboxManager;
import com.eryansky.modules.mail.service.OutboxManager;
import com.eryansky.modules.mail.utils.EmailUtils;
import com.eryansky.utils.YesOrNo;
import org.jsoup.Jsoup;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 邮件 entity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "T_MAIL_EMAIL")
//@Delete(propertyName = "status",type = PropertyType.S)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class Email extends DataEntity<Email> implements IEmail {

    /**
     * 邮件主题
     */
    private String title;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 是否阅读回执消息 默认值：否
     */
    private Integer isReceipt = YesOrNo.NO.getValue();
    /**
     * 邮件优先级  {@link EmailPriority}
     */
    private Integer priority = EmailPriority.Low.getValue();
    /**
     * 邮件大小 单位：字节
     */
    private Long emailSize;
    /**
     * 附件
     */
    private List<String> fileIds = new ArrayList<String>(0);
    /**
     * 是否是匿名邮件 默认值：否
     */
    private Integer isAnonymous = YesOrNo.NO.getValue();

    /**
     * 账号类型 账号/邮件类型 ${@link MailType}
     */
    private Integer mailType = MailType.System.getValue();

    /**
     * 发送人ID User/MailContact
     */
    private String sender;

    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 标识
     */
    private String uid;

    public Email() {
    }

    @PrePersist
    @Override
    public void prePersist() {
        super.prePersist();
        if(StringUtils.isBlank(uid)){
            this.uid = this.id;
        }
        countEmailSize();
    }

    @PreUpdate
    @Override
    public void preUpdate() {
        super.preUpdate();
        countEmailSize();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(length = 512)
    public String getTitle() {
        return this.title;
    }

    @Column(length = 1)
    public Integer getIsReceipt() {
        return isReceipt;
    }

    public void setIsReceipt(Integer isReceipt) {
        this.isReceipt = isReceipt;
    }


    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @JsonIgnore
    @Column(length = 8192)
    public String getContent() {
        return this.content;
    }

    @Transient
    @Override
    public String getSummary() {
        if(StringUtils.isNotBlank(content)){
            return StringUtils.substring(Jsoup.parse(content).text(), 0, 64);
        }
        return null;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public void setEmailSize(Long emailSize) {
        this.emailSize = emailSize;
    }

    public Long getEmailSize() {
        return this.emailSize;
    }

    @ElementCollection
    @CollectionTable(name = "T_MAIL_EMAIL_FILE", joinColumns = {@JoinColumn(name = "EMAIL_ID")})
    @Column(name = "FILE_ID", length = 36)
    public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }
    @Column(length = 1)
    public Integer getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Integer isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    @Override
    public Integer getMailType() {
        return mailType;
    }

    public void setMailType(Integer mailType) {
        this.mailType = mailType;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Column(length = 36)
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Transient
    public Boolean isSystemMail() {
        return MailType.System.getValue().equals(mailType);
    }

    @Transient
    @Override
    public String getSenderName() {
        if (StringUtils.isNotBlank(id)) {
            return EmailUtils.getSenderName(id);
        }
        return null;
    }

    @Transient
    @Override
    public String getToNames() {
        if (StringUtils.isNotBlank(id)) {
            return EmailUtils.getToContactNames(id);
        }
        return null;
    }

    @Transient
    @Override
    public String getCcNames() {
        if (StringUtils.isNotBlank(id)) {
            return EmailUtils.getCcContactNames(id);
        }
        return null;
    }

    @Transient
    @Override
    public String getBccNames() {
        if (StringUtils.isNotBlank(id)) {
            return EmailUtils.getBccContactNames(id);
        }
        return null;
    }


    @Column(length = 64)
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Transient
    @Override
    public String getEmailId() {
        return this.id;
    }

    @Transient
    @Override
    public String getPriorityView() {
        if (priority != null) {
            return EmailPriority.getEmailPriority(priority).getDescription();
        }
        return null;
    }

    /**
     * 发件箱 如果不存在，则自动创建
     * @return
     */
    @Transient
    @JsonIgnore
    public Outbox getOutbox() {
        if (StringUtils.isNotBlank(this.id)) {
            OutboxManager outboxManager = SpringContextHolder.getBean(OutboxManager.class);
            return outboxManager.getOutboxByEmailId(this.id);
        }
        return new Outbox();
    }


    /**
     * 收件箱
     * @return
     */
    @Transient
    @JsonIgnore
    public List<Inbox> getInboxs() {
        if (StringUtils.isNotBlank(this.id)) {
            InboxManager inboxManager = SpringContextHolder.getBean(InboxManager.class);
            return inboxManager.getInboxsByEmailId(this.id);
        }
        return new ArrayList<Inbox>(0);
    }

    /**
     * 回复邮件
     * @return
     */
    @Transient
    @JsonIgnore
    public Email reply() {
        Email email = new Email();
        email.setTitle(EmailUtils.MSG_REPLY + this.getTitle());
        email.setContent(EmailUtils.getEmail_TopInfo(this));
        email.setPriority(this.getPriority());
        return email;
    }


    /**
     * 转发邮件
     * @return
     */
    @Transient
    @JsonIgnore
    public Email repeat() {
        Email email = new Email();
        email.setTitle(EmailUtils.MSG_REPEAT + this.getTitle());
        email.setContent(EmailUtils.getEmail_TopInfo(this));
        return email;
    }


    /**
     * 计算邮件大小 单位：字节 包含邮件内容以及附件大小
     * @return
     */
    @Transient
    public long countEmailSize(){
        long filesSize = FileUtils.countFileSize(fileIds);
        if(StringUtils.isNotEmpty(content)){
            emailSize =  filesSize + Long.valueOf(content.length());
        }else{
            emailSize = filesSize;
        }
        return emailSize;
    }

    /**
     * 邮件大小显示
     * @return
     */
    @Transient
    public String getEmailSizeView(){
        return PrettyMemoryUtils.prettyByteSize(emailSize);
    }

}
