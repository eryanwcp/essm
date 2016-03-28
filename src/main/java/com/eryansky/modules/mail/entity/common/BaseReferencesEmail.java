/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail.entity.common;

import com.eryansky.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.eryansky.modules.mail.entity.Email;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.utils.EmailUtils;
import org.jsoup.Jsoup;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 引用邮件基类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-01-28
 */
@MappedSuperclass
public class BaseReferencesEmail<T> extends DataEntity<T> implements IEmail {

    public BaseReferencesEmail() {
    }

    /**
     * 邮件ID
     */
    private String emailId;

    /**
     * 账号 内部邮件默认账号为null {@link MailAccount}
     */
    private String mailAccountId;


    @Column(length = 36)
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @Column(length = 36)
    public String getMailAccountId() {
        return mailAccountId;
    }

    public void setMailAccountId(String mailAccountId) {
        this.mailAccountId = mailAccountId;
    }


    private Email email;

    /**
     * 获取邮件 缓存机制
     * @return
     */
    @Transient
    @JsonIgnore
    public Email getEmail(){
        if(StringUtils.isNotBlank(emailId)){
            this.email = EmailUtils.getEmail(emailId);
        }
        return email;
    }

    @Transient
    @Override
    public String getTitle() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            return email.getTitle();
        }
        return null;
    }

    @JsonIgnore
    @Transient
    @Override
    public String getContent() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            return email.getContent();
        }
        return null;
    }

    @Transient
    @Override
    public String getSummary() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            if(StringUtils.isNotBlank(email.getContent())){
                return StringUtils.substring(Jsoup.parse(email.getContent()).text(),0,64);
            }
        }

        return null;
    }

    @Transient
    @Override
    public Integer getIsReceipt() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            return email.getIsReceipt();
        }
        return null;
    }

    @Transient
    @Override
    public Integer getPriority() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            return email.getPriority();
        }
        return null;
    }

    @Transient
    @Override
    public String getPriorityView() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            return email.getPriorityView();
        }
        return null;
    }

    @Transient
    @Override
    public Long getEmailSize() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            return email.getEmailSize();
        }
        return null;
    }

    @Transient
    @Override
    public String getSender() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            return email.getSender();
        }
        return null;
    }

    @Transient
    @Override
    public String getSenderName() {
        if(StringUtils.isNotBlank(emailId)){
            return EmailUtils.getSenderName(emailId);
        }
        return null;
    }

    @Transient
    @Override
    public String getToNames() {
        if (StringUtils.isNotBlank(emailId)) {
            return EmailUtils.getToContactNames(emailId);
        }
        return null;
    }

    @Transient
    @Override
    public String getCcNames() {
        if(StringUtils.isNotBlank(emailId)){
            return EmailUtils.getCcContactNames(emailId);
        }
        return null;
    }

    @Transient
    @Override
    public String getBccNames() {
        if(StringUtils.isNotBlank(emailId)){
            return EmailUtils.getBccContactNames(emailId);
        }
        return null;
    }

    @Transient
    @Override
    public Integer getMailType() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            return email.getMailType();
        }
        return null;
    }

    /**
     * 发件时间
     * @return
     */
    @Transient
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    public Date getSendTime() {
        if(StringUtils.isNotBlank(emailId)){
            Email email = getEmail();
            return email.getSendTime();
        }
        return null;
    }

}
