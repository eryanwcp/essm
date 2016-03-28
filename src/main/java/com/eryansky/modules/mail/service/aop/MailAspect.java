/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail.service.aop;

import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.monitor.MailMonitorUtils;
import com.eryansky.modules.mail.task.MailAsyncTaskService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 邮件 登录、登出切面
 * @author 尔演&Eryan
 */
@Component
@Aspect
public class MailAspect implements InitializingBean,DisposableBean {

    private static Logger logger = LoggerFactory.getLogger(MailAspect.class);
    @Autowired
    private MailAsyncTaskService mailTaskService;
    /**
     * 登录增强
     * @param joinPoint 切入点
     */
    @After("execution(* com.eryansky.modules.sys.web.LoginController.login(..))")
    public void afterLoginLog(JoinPoint joinPoint) throws Throwable {
        final SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            mailTaskService.asyncStopAndAddUserMailMonitor(sessionInfo.getUserId(), null);

        }
    }


    /**
     * 登出增强
     * @param joinPoint 切入点
     */
    @Before("execution(* com.eryansky.modules.sys.service.UserManager.*logout(..))")
    public void beforeLogoutLog(JoinPoint joinPoint) throws Throwable {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            mailTaskService.stopUserMailMonitor(sessionInfo.getUserId());
        }
    }

    /**
     * 添加/修改邮箱帐号后
     * @param joinPoint
     * @throws Throwable
     */
    @After("execution(* com.eryansky.modules.mail.service.MailAccountManager.saveEntity(..))")
    public void afterAddAccount(JoinPoint joinPoint) throws Throwable {
        final SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        MailAccount mailAccount = (MailAccount)joinPoint.getArgs()[0];
        if(sessionInfo != null){
            mailTaskService.asyncStopAndAddUserMailMonitor(sessionInfo.getUserId(), mailAccount);
        }

    }

    @After("execution(* com.eryansky.modules.mail.service.MailAccountManager.deleteByIds(..))")
    public void afterDeleteAccount(JoinPoint joinPoint) throws Throwable {
        final SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(sessionInfo != null){
            mailTaskService.asyncStopAndAddUserMailMonitor(sessionInfo.getUserId(),null);
        }

    }


    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void destroy() throws Exception {
        MailMonitorUtils.stopAll();
    }
}
