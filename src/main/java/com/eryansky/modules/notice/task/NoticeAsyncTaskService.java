/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.task;

import com.eryansky.modules.notice.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 通知异步任务
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-12-25 
 */
@Component
public class NoticeAsyncTaskService {

    private static Logger logger = LoggerFactory.getLogger(NoticeAsyncTaskService.class);

    @Autowired
    private NoticeService noticeService;

    /**
     * 发送通知
     * @param noticeId
     */
    @Async
    public void publish(String noticeId){
        try {
            noticeService.publish(noticeId);
        } catch (Exception e) {
            logger.error("发布通知["+noticeId+"]异常"+e.getMessage(),e);
        }
    }
}
