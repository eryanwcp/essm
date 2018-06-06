/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.datasource;

import com.eryansky.common.utils.reflection.ReflectionUtils;
import com.eryansky.common.datasource.annotation.SessionFactory;
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
public class SessionFactoryMethodInterceptor implements MethodInterceptor, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(SessionFactoryMethodInterceptor.class);

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
        SessionFactory classSessionFactory = ReflectionUtils.getAnnotation(invocation.getThis(), SessionFactory.class);
        SessionFactory methodSessionFactory = ReflectionUtils.getAnnotation(invocation.getMethod(), SessionFactory.class);
        Boolean autoReset = null;
        if(methodSessionFactory != null){
            SessionFactoryContextHolder.setSessionFactoryType(methodSessionFactory.value());
            autoReset = methodSessionFactory.autoReset();
        }else if(classSessionFactory != null){
            SessionFactoryContextHolder.setSessionFactoryType(classSessionFactory.value());
            autoReset = classSessionFactory.autoReset();
        }else {
            SessionFactoryContextHolder.clearSessionFactory();
        }

        Object result = invocation.proceed();
        if(autoReset != null && autoReset){
            SessionFactoryContextHolder.clearSessionFactory();
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}