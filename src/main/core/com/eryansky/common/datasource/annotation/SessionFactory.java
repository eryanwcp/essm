/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.datasource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SessionFactory
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-08-13
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionFactory {

    /**
     * SessionFactory名称 （注：Spring Bean id）
     * @return
     */
    String value();
    /**
     * 自动恢复默认数据源 默认值：true
     * @return
     */
    boolean autoReset() default true;
}
