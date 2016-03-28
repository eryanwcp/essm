/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.datasource;

import com.eryansky.common.utils.reflection.ReflectionUtils;
import com.eryansky.common.datasource.annotation.DataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Proxy;

/**
 * 多数据源动态配置拦截器
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-08-13
 */
public class DataSourceMethodInterceptor implements MethodInterceptor, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(DataSourceMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> clazz = invocation.getThis().getClass();
        String className = clazz.getName();
        if (ClassUtils.isAssignable(clazz, Proxy.class)) {
            className = invocation.getMethod().getDeclaringClass().getName();
        }
        String methodName = invocation.getMethod().getName();
        Object[] arguments = invocation.getArguments();
        logger.debug("execute {}.{}({})", className, methodName, arguments);

        invocation.getMethod();
        DataSource classDataSource = ReflectionUtils.getAnnotation(invocation.getThis(), DataSource.class);
        DataSource methodDataSource = ReflectionUtils.getAnnotation(invocation.getMethod(), DataSource.class);
        Boolean autoReset = null;
        if(methodDataSource != null){
            DataSourceContextHolder.setDataSourceType(methodDataSource.value());
            autoReset = methodDataSource.autoReset();
        }else if(classDataSource != null){
            DataSourceContextHolder.setDataSourceType(classDataSource.value());
            autoReset = classDataSource.autoReset();
        }else {
            DataSourceContextHolder.clearDataSourceType();
        }

        Object result = invocation.proceed();
        if(autoReset != null && autoReset){
            DataSourceContextHolder.clearDataSourceType();
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}