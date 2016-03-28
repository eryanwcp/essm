/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.BaseEntity;
import com.eryansky.modules.mail._enum.ReceiveObjectType;
import com.eryansky.modules.mail._enum.ReceiveType;
import com.eryansky.modules.mail.utils.EmailUtils;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.*;

/**
 * 发件 发送信息
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-08-13
 */
@Entity
@Table(name = "t_mail_receive_info")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class ReceiveInfo extends BaseEntity<ReceiveInfo> implements IContact{

    /**
     * 邮件发送类型 {@link ReceiveType}
     */
    private Integer receiveType;
    /**
     * 邮件ID
     */
    private String emailId;
    /**
     * 接收人类型 {@link com.eryansky.modules.mail._enum.ReceiveObjectType}
     */
    private Integer receiveObjectType;
    /**
     * 接收账号ID 用户ID或联系人ID {@link MailContact}
    */
    private String receiveObjectId;

    public ReceiveInfo() {
        super();

    }

    public ReceiveInfo(String id) {
        super(id);
    }

    @Column(length = 1)
    public Integer getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(Integer receiveType) {
        this.receiveType = receiveType;
    }


    @Column(length = 36)
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @Column(length = 1)
    public Integer getReceiveObjectType() {
        return receiveObjectType;
    }

    public void setReceiveObjectType(Integer receiveObjectType) {
        this.receiveObjectType = receiveObjectType;
    }

    @Column(length = 36)
    public String getReceiveObjectId() {
        return receiveObjectId;
    }

    public void setReceiveObjectId(String receiveObjectId) {
        this.receiveObjectId = receiveObjectId;
    }

    /**
     * 获取联系人显示名称
     *
     * @return
     */
    @Transient
    public String getNameView() {
        if (ReceiveObjectType.User.getValue().equals(receiveObjectType)) {
            return UserUtils.getUserName(receiveObjectId);
        } else if (ReceiveObjectType.Contact.getValue().equals(receiveObjectType)) {
            return EmailUtils.getMailContactName(receiveObjectId);
        }

        return receiveObjectId;
    }


    /**
     * 联系人地址
     * @return
     */
    @Transient
    public String getContactEmail() {
        if (ReceiveObjectType.Contact.getValue().equals(receiveObjectType)) {
            return EmailUtils.getMailContact(receiveObjectId).getEmail();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        ReceiveInfo that = (ReceiveInfo)obj;
        boolean flag = this.getEmailId().equals(that.getEmailId()) && this.getReceiveObjectId().equals(that.getReceiveObjectId()) && this.getReceiveObjectId().equals(that.getReceiveObjectId());
        return null == this.getId() ? false : this.getId().equals(that.getId()) || flag;
}
}
