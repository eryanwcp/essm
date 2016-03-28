/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 手机是否可以访问
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2015-07-20 15:06
 */
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mobile {

    /**
     * 是否可以访问 是：true 否：false 默认值：true
     * @return
     */
    boolean able() default true;

}