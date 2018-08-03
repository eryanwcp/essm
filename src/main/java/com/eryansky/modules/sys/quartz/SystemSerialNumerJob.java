/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.quartz;

import com.eryansky.core.quartz.QuartzJob;
import com.eryansky.modules.sys.service.SystemSerialNumberService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 序列号管理 重置最大值定时任务
 * 每天凌晨执行
 */
@QuartzJob(name = "SystemSerialNumerJob", cronExp = "0 0 0 * * ?")
public class SystemSerialNumerJob extends QuartzJobBean {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SystemSerialNumberService systemSerialNumberService;

    public void execute(){
        logger.debug("定时任务...开始：序列号管理 定时任务");
        systemSerialNumberService.resetSerialNumber();
        logger.debug("定时任务...结束：序列号管理 定时任务");
    }


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        execute();
    }

}
