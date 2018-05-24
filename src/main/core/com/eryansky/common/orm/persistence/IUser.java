/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.persistence;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-27
 */
public interface IUser {
    /**
     * ID
     *
     * @return
     */
    String getId();

    /**
     * 姓名
     *
     * @return
     */
    String getName();

    /**
     * 登录名
     *
     * @return
     */
    String getLoginName();

    /**
     * 密码
     *
     * @return
     */
    String getPassword();

    /**
     * 用户类型
     *
     * @return
     */
    String getUserType();

    /**
     * 是否是管理员
     * @return
     */
    boolean isAdmin();
}
