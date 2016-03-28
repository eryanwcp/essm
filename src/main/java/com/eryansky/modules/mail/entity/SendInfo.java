/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.BaseEntity;
import com.eryansky.modules.mail._enum.ReceiveObjectType;
import com.eryansky.modules.mail.utils.EmailUtils;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 发送配置信息
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-18
 */
@Entity
@Table(name = "t_mail_send_info")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class SendInfo extends BaseEntity<SendInfo> implements IContact{

    /**
     * 发件箱ID
     */
    private String outboxId;

    /**
     * 接收类型 {@link com.eryansky.modules.mail._enum.ReceiveType}
     */
    private Integer receiveType;
    /**
     * 接收对象类型 {@link ReceiveObjectType}
     */
    private Integer receiveObjectType;
    /**
     * 接收对象 用户ID/联系人ID/联系人组ID
     */
    private String receiveObjectId;

    @Column(length = 36)
    public String getOutboxId() {
        return outboxId;
    }

    public void setOutboxId(String outboxId) {
        this.outboxId = outboxId;
    }

    @Column(length = 1)
    public Integer getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(Integer receiveType) {
        this.receiveType = receiveType;
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

    @Transient
    public String getNameView(){
        String str = null;
        if(ReceiveObjectType.User.getValue().equals(receiveObjectType)){
            str = UserUtils.getUserName(receiveObjectId);
        }else if(ReceiveObjectType.UserGroup.getValue().equals(receiveObjectType)){
            str = EmailUtils.getContactGroupName(receiveObjectId);
        }else if(ReceiveObjectType.Organ.getValue().equals(receiveObjectType)){
            str = OrganUtils.getOrganName(receiveObjectId);
        }else if(ReceiveObjectType.Contact.getValue().equals(receiveObjectType)){
            str = EmailUtils.getMailContactName(receiveObjectId);
        }if(ReceiveObjectType.ContactGroup.getValue().equals(receiveObjectType)){
            str = EmailUtils.getContactGroupName(receiveObjectId);
        }
        return str;
    }
}
