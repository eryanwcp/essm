/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.task;

import com.eryansky.modules.notice.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 通知 后台定时任务..
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-2-4 上午09:08:18
 */
@Component
public class NoticeJobManager {

	private static final Logger logger = LoggerFactory.getLogger(NoticeJobManager.class);

	@Autowired
    private NoticeService noticeService;

    /**
     * 轮询通知
     */
    @Scheduled(cron="0 0/5 * * * ?")
    public void pollNotice(){
        logger.debug("轮询任务开始...");
        noticeService.pollNotice();
        logger.debug("轮询任务开始...");
    }
    
}