/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.entity;

import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.Date;

/**
 * 用户口令修改信息记录
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-05-14
 */
@Entity
@Table(name = "t_sys_user_password")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class UserPassword extends DataEntity<UserPassword> {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 口令修改时间
     */
    private Date modifyTime = Calendar.getInstance().getTime();
    /**
     * 原始密码
     */
    private String originalPassword;
    /**
     * 秘密 md5等方式加密
     */
    private String password;

    public UserPassword() {
        super();
    }

    public UserPassword(String id) {
        super(id);
    }

    public UserPassword(String userId, String password) {
        this();
        this.userId = userId;
        this.password = password;
    }

    public UserPassword(String userId, String originalPassword, String password) {
        this();
        this.userId = userId;
        this.originalPassword = originalPassword;
        this.password = password;
    }

    @Column(length = 36)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    @Column(updatable = false, nullable = false)
    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Column(length = 64)
    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    @Column(length = 64)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
