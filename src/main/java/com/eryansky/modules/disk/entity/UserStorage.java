/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.BaseEntity;
import com.eryansky.utils.AppConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户云盘存储空间配置
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-24
 */
@Entity
@Table(name = "T_DISK_USER_STORAGE")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class UserStorage extends BaseEntity<UserStorage> {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 最大限制大小 单位：M {@link AppConstants}
     */
    private Integer limitSize;

    public UserStorage() {
//        limitSize = AppConstants.getDiskUserLimitSize();
    }


    @Column(length = 36)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }
}
