/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
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
 * <br/>支持SpEL表达式
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {

    /**
     * 记录日志 默认值：true
     * <br/>支持SpEL表达式
     * @return
     */
    String logging() default "true";

    /**
     * 日志类型 {@link LogType}
     * @return
     */
    LogType logType() default operate;

    /**
     * 日志详细信息
     * <br/>支持SpEL表达式
     * @return
     */
    String value() default "";
    /**
     * 备注信息
     * <br/>支持SpEL表达式
     * @return
     */
    String remark() default "";

}