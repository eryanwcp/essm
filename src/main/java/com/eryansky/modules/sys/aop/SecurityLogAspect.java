/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.aop;

import com.eryansky.core.security.SecurityType;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.mapper.Log;
import com.eryansky.modules.sys.service.LogService;
import com.eryansky.modules.sys.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 使用AspectJ实现登录登出日志AOP
 * @author 尔演&Eryan
 */
@Aspect
public class SecurityLogAspect {

    private static Logger logger = LoggerFactory.getLogger(SecurityLogAspect.class);

    @Autowired
    private LogService logService;

    /**
     * 登录增强
     * @param joinPoint 切入点
     */
    @After("execution(* com.eryansky.modules.sys.web.LoginController.login(..))")
    public void afterLoginLog(JoinPoint joinPoint) throws Throwable{
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(sessionInfo != null){
            saveLog(sessionInfo, joinPoint, SecurityType.login); //保存日志
        }
    }

    /**
     * 登出增强
     * @param joinPoint 切入点
     */
    @Before("execution(* com.eryansky.modules.sys.service.UserService.*logout(..))")
    public void beforeLogoutLog(JoinPoint joinPoint) throws Throwable{
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(sessionInfo != null){
            Object[] args = joinPoint.getArgs();
            SecurityType securityType = SecurityType.logout;
            if(args != null && args.length >= 2){
                if(args[1] instanceof SecurityType){
                    securityType = (SecurityType)args[1];
                }
            }

            saveLog(sessionInfo, joinPoint, securityType); //保存日志
        }
    }

    /**
     * 保存日志
     * @param sessionInfo 登录用户session信息
     * @param joinPoint 切入点
     * @param securityType 是否是登录操作
     *                     @see SecurityType
     */
    public void saveLog(SessionInfo sessionInfo, JoinPoint joinPoint,SecurityType securityType){
        Long start = System.currentTimeMillis();
        Long end = 0L;
        // 执行方法名
        String methodName = null;
        String className = null;
        if(joinPoint != null){
            methodName = joinPoint.getSignature().getName();
            className = joinPoint.getTarget().getClass().getSimpleName();
        }else{
            className = UserService.class.getSimpleName();
            methodName = "logout";
        }
        String user = null;

        // 执行方法所消耗的时间
        try {
            Log log = new Log();
            log.setType(LogType.security.getValue());
            log.setUserId(sessionInfo.getUserId());
            log.setModule(className + "-" + methodName);
            log.setIp(sessionInfo.getIp());
            log.setTitle(securityType.getDescription());
            log.setAction("[" + sessionInfo.getLoginName() + "]" + securityType.getDescription());
            log.setUserAgent(sessionInfo.getUserAgent());
            log.setDeviceType(sessionInfo.getDeviceType());
            log.setBrowserType(sessionInfo.getBrowserType());
            if(logger.isDebugEnabled()){
                logger.debug("用户:{},操作类：{},操作方法：{},耗时：{}ms.",new Object[]{user,className,methodName,end - start});
            }
            log.setOperTime(new Date());
            end = System.currentTimeMillis();
            Long opTime = end - start;
            log.setActionTime(opTime.toString());
            logService.save(log);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
