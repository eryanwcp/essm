/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.entity;

import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.DataEntity;

import javax.persistence.*;

/**
 * 邮件联系人
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-08-13
 */
@Entity
@Table(name = "T_MAIL_CONTACT")
@Delete(propertyName = "status", type = PropertyType.S)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class MailContact extends DataEntity<MailContact> {

    /**
     * 所属用户
     */
    private String userId;

    /**
     * 显示名称
     */
    private String name;
    /**
     * 邮件地址
     */
    private String email;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 备注
     */
    private String remark;

    @Column(length = 36)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(length = 128)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 128)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(length = 36)
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    @Transient
    @JsonIgnore
    public MailContact copy(String contactGroupId){
        MailContact mailContact = new MailContact();
        mailContact.setName(this.name);
        mailContact.setEmail(this.email);
        mailContact.setRemark(this.remark);
        mailContact.setUserId(this.getUserId());
        return mailContact;
    }
}