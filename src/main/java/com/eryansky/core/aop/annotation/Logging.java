/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.aop.annotation;


import com.eryansky.modules.sys._enum.LogType;

import static com.eryansky.modules.sys._enum.LogType.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否记录日志 用于切面记录日志
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {

    /**
     * 记录日志 默认值：true
     * @return
     */
    boolean logging() default true;

    /**
     * 日志类型 {@link LogType}
     * @return
     */
    LogType logType() default operate;

    /**
     * 日志详细信息
     * <br/>支持占位符 表示第几个参数 例如："{0},{1},..."
     * @return
     */
    String value() default "";

}