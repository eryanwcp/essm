/**
 *  Copyright (c) 2012-2017 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.task;

import com.eryansky.modules.sys.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 同步organ扩展表
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2017-09-19
 */
@Component
public class SyncOrganToExtendJob {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SystemService systemService;

    /**
     * 执行任务
     */
    @Scheduled(cron="0 0 0 * * ?")
    public void execute(){
        logger.info("定时任务...开始：同步organ扩展表");
        systemService.deleteOrganExtend();
        systemService.insertToOrganExtend();
        logger.info("定时任务...结束：同步organ扩展表");
    }


}
