/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.aop.interceptor;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Lists;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.mapper.Log;
import com.eryansky.modules.sys.service.LogService;
import com.eryansky.utils.SpringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * 日志拦截 切面
 * <p/>
 * web层拦截参考 @see com.eryansky.core.web.interceptor.LogInterceptor
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2016-04-13
 */
public class LogMethodInterceptor implements MethodInterceptor, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(LogMethodInterceptor.class);

    @Autowired
    private LogService logService;
    /**
     * 默认方法
     */
    private List<String> defaultMethods = Lists.newArrayList();
    /**
     * 拦截的方法
     */
    private List<String> aspectMethods;

    public LogMethodInterceptor() {
        defaultMethods.add("save*");
        defaultMethods.add("update*");
        defaultMethods.add("insert*");
        defaultMethods.add("merge*");
        defaultMethods.add("remove*");
        defaultMethods.add("delete*");
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Long start = 0L;
        Long end = 0L;
        start = System.currentTimeMillis();
        Object result = methodInvocation.proceed();
        Method method = methodInvocation.getMethod();
        Object[] args = methodInvocation.getArguments();
        end = System.currentTimeMillis();

        // 执行方法名
        String methodName = method.getName();
        String className = methodInvocation.getThis().getClass().getSimpleName();
        logger.info(methodInvocation.getThis().getClass().getName() + "."+ methodName);
        String userId = null;

        String ip = null;
        // 登录名
        SessionInfo sessionInfo = null;
        // 当前用户
        try {
            try {
                sessionInfo = SecurityUtils.getCurrentSessionInfo();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            if (sessionInfo != null) {
                userId = sessionInfo.getUserId();
                ip = sessionInfo.getIp();
            } else {
                userId = "1";
                ip = "127.0.0.1";
//                logger.warn("sessionInfo为空.");
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        String name = className;
        // 操作类型
        String opertype = methodName;

        //注解式日志
        Logging logging = method.getAnnotation(Logging.class);//注解式日志
        boolean loglog = false;
        String remark = null;
        String newLogValue = null;
        String _LogType = LogType.operate.getValue();
        if (logging != null && Boolean.valueOf(SpringUtils.parseSpel(logging.logging(), method, args))) {
            loglog = true;
            _LogType = logging.logType().getValue();
            String logValue = logging.value();
            newLogValue = logValue;
            if (StringUtils.isNotBlank(logValue)) {
                newLogValue = SpringUtils.parseSpel(logValue, method, args);
            }

            remark = newLogValue;
        }

        boolean pattendefaultMethod = false;//匹配默认方法
        if (Collections3.isNotEmpty(defaultMethods)) {
            for (String defaultMethod : defaultMethods) {
                pattendefaultMethod = StringUtils.simpleWildcardMatch(defaultMethod, opertype);
                if (pattendefaultMethod) {
                    break;
                }
            }
        }

        if (loglog || (pattendefaultMethod && (logging == null || logging != null && !Boolean.valueOf(SpringUtils.parseSpel(logging.logging(), method, args))))) {
            Long time = end - start;
            Log log = new Log();
            log.setTitle(newLogValue);
            log.setType(_LogType);
            log.setUserId(userId);
            log.setModule(name);
            log.setAction(opertype);
            log.setOperTime(new Date(start));
            log.setActionTime(time.toString());
            log.setIp(ip);
            log.setRemark(remark);
            if (sessionInfo != null) {
                log.setUserAgent(sessionInfo.getUserAgent());
                log.setDeviceType(sessionInfo.getDeviceType());
                log.setBrowserType(sessionInfo.getBrowserType());
            }
            logService.save(log);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("用户:{},操作类：{},操作方法：{},耗时：{}ms.", new Object[]{userId, className, methodName, end - start});
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public List<String> getDefaultMethods() {
        return defaultMethods;
    }

    public void setDefaultMethods(List<String> defaultMethods) {
        this.defaultMethods = defaultMethods;
    }

    public List<String> getAspectMethods() {
        return aspectMethods;
    }

    public void setAspectMethods(List<String> aspectMethods) {
        this.aspectMethods = aspectMethods;
        if (Collections3.isNotEmpty(aspectMethods)) {
            this.defaultMethods.addAll(aspectMethods);
        }
    }
}

