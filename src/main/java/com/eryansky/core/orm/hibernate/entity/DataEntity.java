/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.hibernate.entity;

import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.persistence.IDataEntity;
import com.eryansky.common.utils.Identities;
import com.eryansky.core.security.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.util.Calendar;
import java.util.Date;

/**
 * 自定义UUID基础实体
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-07-06 
 */
@MappedSuperclass
public abstract class DataEntity<T> extends BaseEntity<T> implements IDataEntity {

    /**
     * {@link StatusState}
     */
    public static final String STATUS_NORMAL = "0";//正常
    public static final String STATUS_DELETE = "1";//删除
    public static final String STATUS_AUDIT = "2";//审核
    public static final String STATUS_LOCK = "3";//锁定

    /**
     * 记录状态标志位 {@link StatusState}
     */
    protected String status = StatusState.NORMAL.getValue();

    /**
     * 操作版本(乐观锁,用于并发控制)
     */
    protected Integer version;

    /**
     * 记录创建者用户登录名
     */
    protected String createUser;
    /**
     * 记录创建时间
     */
    protected Date createTime;

    /**
     * 记录更新用户 用户登录名
     */
    protected String updateUser;
    /**
     * 记录更新时间
     */
    protected Date updateTime;

    public DataEntity() {
        super();
    }

    public DataEntity(String id) {
        this();
        this.id = id;
    }

    @PrePersist
    public void prePersist() {
        // 不限制ID为UUID，调用setIsNewRecord()使用自定义ID
        if (!this.isNewRecord){
            setId(Identities.uuid2());
        }
        String user = SecurityUtils.getCurrentUserId();
        this.updateUser = user;
        this.createUser = user;
        this.updateTime = Calendar.getInstance().getTime();
        this.createTime = this.updateTime;
    }

    @PreUpdate
    public void preUpdate() {
        String user = SecurityUtils.getCurrentUserId();
        this.updateUser = user;
        this.updateTime = Calendar.getInstance().getTime();
    }


    /**
     * 状态标志位
     */
    @Column(length = 1)
    public String getStatus() {
        return status;
    }

    /**
     * 状态描述
     */
    @Transient
    public String getStatusView() {
        StatusState s = StatusState.getStatusStateByValue(status);
        String str = "";
        if (s != null) {
            str = s.getDescription();
        }
        return str;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * 版本号(乐观锁)
     */
    @Version
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }


    /**
     * 记录创建者 用户登录名
     */
    @Column( updatable = false, length = 36)
    public String getCreateUser() {
        return createUser;
    }

    @Override
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }


    /**
     * 记录创建时间.
     */
    // 设定JSON序列化时的日期格式
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 记录更新用户 用户登录名
     */
    @Column(length = 36)
    public String getUpdateUser() {
        return updateUser;
    }

    @Override
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }


    /**
     * 记录更新时间
     */
    // 设定JSON序列化时的日期格式
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
