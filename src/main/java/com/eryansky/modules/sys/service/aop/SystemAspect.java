/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service.aop;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.service.SystemService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 切面
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-10
 */
@Component
@Aspect
public class SystemAspect implements InitializingBean, DisposableBean {

    private static Logger logger = LoggerFactory.getLogger(SystemAspect.class);

    @Autowired
    private SystemService systemService;

    /**
     * 同步到扩展机构表
     * @param joinPoint 切入点
     * @param returnObj 返回值
     */
    @AfterReturning(value = "execution(* com.eryansky.modules.sys.service.OrganService.saveOrgan(..)) || " +
            "execution(* com.eryansky.modules.sys.service.OrganService.deleteById(..))",returning = "returnObj")
    public void afterSyncOrganToExtend(JoinPoint joinPoint,Object returnObj) {
        if(returnObj != null){
            Parameter parameter = Parameter.newParameter();
            if(returnObj instanceof String){
                String id = (String) returnObj;
                parameter.put("id", id);
                systemService.syncOrganToExtend(parameter);
            }else if(returnObj instanceof Organ){
                Organ organ = (Organ) returnObj;
                parameter.put("id", organ.getId());
                systemService.syncOrganToExtend(parameter);
            }
        }else{
            systemService.syncOrganToExtend();
        }

    }


    @Override
    public void destroy() throws Exception {
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
