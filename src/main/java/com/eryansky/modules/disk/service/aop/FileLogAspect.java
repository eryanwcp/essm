/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.service.aop;

import com.eryansky.common.model.Result;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.disk.entity.FileHistory;
import com.eryansky.modules.disk.entity._enum.FileOperate;
import com.eryansky.modules.disk.service.FileHistoryManager;
import com.eryansky.modules.disk.service.FileManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * 云盘访问切面
 *
 * @author xwj
 */
@Aspect
@Component
public class FileLogAspect {
    private static Logger logger = LoggerFactory.getLogger(FileLogAspect.class);

    @Autowired
    private FileHistoryManager fileHistoryManager;
    @Autowired
    private FileManager fileManager;

    /**
     * 下载切面
     *
     * @param joinPoint
     *            切入点
     */
    @After(value = "execution(* com.eryansky.modules.disk.web.DiskController.downloadDiskFile(..))")
    public void afterDownload(JoinPoint joinPoint) throws Throwable {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null && joinPoint != null) {
            Object[] params = joinPoint.getArgs();
            if (params[2] != null) {
                List<String> fileIds = (List<String>) params[2];
                for (String fileId : fileIds) {
                    saveOrUpdateHistory(sessionInfo, FileOperate.DOWNLOAD,
                            fileId); // 保存日志
                }
            }
        }
    }

    /**
     * 收藏切面
     *
     * @param joinPoint
     *            切入点
     */
    @AfterReturning(value = "execution(* com.eryansky.modules.disk.web.DiskController.CollectFile(..))", returning = "result")
    public void afterCollect(JoinPoint joinPoint, Result result)
            throws Throwable {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null && result != null) {
            if (Result.SUCCESS == result.getCode()) {
                saveOrUpdateHistory(sessionInfo, FileOperate.COLLECT,result.getObj().toString()); // 保存日志
            }
        }
    }

    /**
     * 分享切面
     *
     * @param joinPoint
     *            切入点
     */
    @AfterReturning(value = "execution(* com.eryansky.modules.disk.web.DiskController.shareFile(..))", returning = "result")
    public void afterShare(JoinPoint joinPoint, Result result) throws Throwable {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null && result != null) {
            if (Result.SUCCESS == result.getCode()) {
                saveOrUpdateHistory(sessionInfo, FileOperate.SHARE,result.getObj().toString()); // 保存日志

            }

        }
    }

    /**
     * 保存日志
     *
     * @param sessionInfo
     *            登录用户session信息
     * @param fileOperate
     *            文件操作属性 {@link com.eryansky.modules.disk.entity._enum.FileOperate}
     * @param fileId
     *            文件Id
     */
    public void saveOrUpdateHistory(SessionInfo sessionInfo,
                                    FileOperate fileOperate, String fileId) {

        try {
            String userId = sessionInfo.getUserId();
            FileHistory history = fileHistoryManager.findUniqueForfileCode(
                    fileId, userId);
            if (history == null) {
                history = new FileHistory();
                history.setFileId(fileId);
            }
            history.setUserId(userId);
            history.setOperateTime(Calendar.getInstance().getTime());
            history.setOperateType(fileOperate.getValue());

            fileHistoryManager.saveOrUpdate(history);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
