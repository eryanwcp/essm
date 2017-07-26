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

import static com.eryansky.core.web.annotation.MobileValue.*;
import static com.eryansky.core.web.annotation.MobileValue.MOBILE;

/**
 * 移动端支持
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2015-07-20 15:06
 */
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mobile {

    /**
     * 是否启用 是：true 否：false 默认值：true
     * @return
     */
    boolean mobile() default true;

    /**
     * 默认目录 默认值：MOBILE
     * 注：
     * 设置为PC时，文件放置在PC目录，无需实现MOBILE页面
     * 设置为MOBILE时，文件放置在MOBILE目录，无需实现PC页面
     * 设置为ALL时，需要分别实现对应页面
     * @return
     */
    MobileValue value() default MOBILE;

}