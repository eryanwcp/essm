/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.service.task;

import com.eryansky.modules.disk.service.FileHistoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 云盘 后台定时任务.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-2-4 上午09:08:18
 */
public class DiskJobManager {

	private static final Logger logger = LoggerFactory.getLogger(DiskJobManager.class);

	@Autowired
    private FileHistoryManager fileHistoryManager;

    /**
     * 清除所有人员云盘3个月外的访问记录
     */
    public void clearHistory() {
        try {
            logger.debug("清除任务开始...");
            fileHistoryManager.clearHistory(3);
            logger.debug("清除任务任务结束.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}