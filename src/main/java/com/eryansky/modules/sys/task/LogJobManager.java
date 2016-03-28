/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.task;

import com.eryansky.modules.sys.mapper.Log;
import com.eryansky.modules.sys.service.LogService;
import com.eryansky.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

/**
 * 日志管理任务调度
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date: 13-12-28 下午6:06
 */
@Component
public class LogJobManager {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogService logService;

    /**
     * 清理过期日志
     */
    @Scheduled(cron="0 0 0 * * ?")
    public void clearInvalidLog(){
        logger.debug("定时任务...开始：清理过期日志");
        int logKeepTime = AppConstants.getLogKeepTime();
        logService.clearInvalidLog(logKeepTime);
        logger.debug("定时任务...结束：清理过期日志");
    }

    /**
     * 清理过期日志
     */
    @Async
    public void saveAspectLog(Log log, HandlerMethod handler){
        logService.save(log);
    }
}
