/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.persistence;

import java.util.Date;

/**
 * 抽象接口
 *
 */
public interface IDataEntity{

    /**
     * 状态标志位
     */
    String getStatus();
    /**
     * 状态描述
     */
    String getStatusView();


    /**
     * 设置 状态标志位
     *
     * @param status 状态标志位
     */
    void setStatus(String status);

    /**
     * 版本号(乐观锁)
     */
    Integer getVersion();

    /**
     * 设置 版本号(乐观锁)
     *
     * @param version 版本号(乐观锁)
     */
    void setVersion(Integer version);

    /**
     * 记录创建者 用户登录名
     */
    String getCreateUser();

    /**
     * 设置 记记录创建者 用户登录名
     *
     * @param createUser 用户登录名
     */
    void setCreateUser(String createUser);

    /**
     * 记录创建时间.
     */
    // 设定JSON序列化时的日期格式
    Date getCreateTime();

    /**
     * 设置 记录创建时间
     *
     * @param createTime 记录创建时间
     */
    void setCreateTime(Date createTime);

    /**
     * 记录更新用户 用户登录名
     */
    String getUpdateUser();

    /**
     * 设置 记录更新用户 用户登录名
     *
     * @param updateUser 用户登录名
     */
    void setUpdateUser(String updateUser);

    /**
     * 记录更新时间
     */
    Date getUpdateTime();

    /**
     * 设置 记录更新时间
     *
     * @param updateTime 记录更新时间
     */
    void setUpdateTime(Date updateTime);

}

