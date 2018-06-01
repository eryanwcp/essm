/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.quartz;

import com.eryansky.core.quartz.QuartzJob;
import com.eryansky.modules.notice.service.NoticeService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 通知 后台定时任务..
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-2-4 上午09:08:18
 */
@QuartzJob(name = "NoticeJob",cronExp = "0 0/10 * * * ?")
public class NoticeJob extends QuartzJobBean {

	private static final Logger logger = LoggerFactory.getLogger(NoticeJob.class);

	@Autowired
    private NoticeService noticeService;

    /**
     * 轮询通知
     */
    public void execute(){
        logger.debug("轮询任务开始...");
        noticeService.pollNotice();
        logger.debug("轮询任务开始...");
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        execute();
    }
}