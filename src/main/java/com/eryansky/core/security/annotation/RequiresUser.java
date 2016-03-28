/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户登录验证注解
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-06-24 15:06
 */
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresUser {

    /**
     * 用户是否需要登录 是：true 否：false 默认值：true
     * @return
     */
    boolean required() default true;

}