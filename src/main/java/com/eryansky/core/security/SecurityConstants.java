/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;


/**
 * 项目中用到的静态变量.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-8-20 上午11:40:56
 */
public class SecurityConstants {
    /**
     * session 未授权的URL
     */
    public static final String SESSION_UNAUTHORITY_URL = "UNAUTHORITY_URL";

    /**
     * session 未授权的页面
     */
    public static final String SESSION_UNAUTHORITY_PAGE = "/common/403.jsp";
    public static final String SESSION_UNAUTHORITY_LOGIN_PAGE = "/jump.jsp";

    /**
     * 超级管理员
     */
    public static final String ROLE_SUPERADMIN = "超级管理员";

}
