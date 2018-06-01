/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.quartz;

import com.eryansky.core.quartz.QuartzJob;
import com.eryansky.modules.sys.service.LogService;
import com.eryansky.utils.AppConstants;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 日志管理任务调度
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date: 13-12-28 下午6:06
 */
@QuartzJob(name = "LogJob", cronExp = "0 0 0 * * ?")
public class LogJob extends QuartzJobBean {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogService logService;

    /**
     * 清理过期日志
     */
    public void execute(){
        logger.info("定时任务...开始：备份并清理过期日志");
        int logKeepTime = AppConstants.getLogKeepTime();
        logService.insertToHistoryAndClear(logKeepTime);
        logger.info("定时任务...结束：备份并清理过期日志");
    }


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        execute();
    }

}
