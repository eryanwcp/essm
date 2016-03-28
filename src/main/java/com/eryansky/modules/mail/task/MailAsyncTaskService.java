/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail.task;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.monitor.MailMonitorUtils;
import com.eryansky.modules.mail.service.EmailManager;
import com.eryansky.modules.mail.service.MailAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 邮件异步任务
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-12-25 
 */
@Service
public class MailAsyncTaskService {

    private static Logger logger = LoggerFactory.getLogger(MailAsyncTaskService.class);

    @Autowired
    private EmailManager emailManager;
    @Autowired
    private MailAccountManager mailAccountManager;
    /**
     * 启动用户邮件监听服务
     * @param userId
     */
    @Async
    public void addUserMailMonitor(String userId){
        try {
            if(logger.isDebugEnabled()){
                logger.debug("thread:{} userId:{}",new Object[]{Thread.currentThread().getName(),userId});
            }
            MailMonitorUtils.add(userId);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }


    @Async
    public void asyncStopAndAddUserMailMonitor(String userId, MailAccount addMailAccount){
        try {
            if(logger.isDebugEnabled()){
                logger.debug("thread:{} userId:{}",new Object[]{Thread.currentThread().getName(),userId});
            }
            MailMonitorUtils.stop(userId);
            MailMonitorUtils.add(userId,addMailAccount);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    public void stopAndAddUserMailMonitor(String userId, MailAccount addMailAccount){
        try {
            if(logger.isDebugEnabled()){
                logger.debug("thread:{} userId:{}",new Object[]{Thread.currentThread().getName(),userId});
            }
            MailMonitorUtils.stop(userId);
            MailMonitorUtils.add(userId,addMailAccount);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * 停止用户邮件监听服务
     * @param userId
     */
    public void stopUserMailMonitor(String userId){
        try {
            if(logger.isDebugEnabled()){
                logger.debug("thread:{} userId:{}",new Object[]{Thread.currentThread().getName(),userId});
            }
            MailMonitorUtils.stop(userId);
            logger.debug("stopUserMailMonitor {}", new Object[]{userId});
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * 发送邮件
     * @param emailId
     */
    @Async
    public void sendEmail(String emailId){
        try {
            if(logger.isDebugEnabled()){
                logger.debug("thread:{} emailId:{}",new Object[]{Thread.currentThread().getName(),emailId});
            }
            emailManager.sendEmail(emailId);
        } catch (Exception e) {
            logger.error("发送邮件["+emailId+"]失败"+e.getMessage(),e);
        }
    }

    /**
     * 接收邮件
     * @param userId
     * @param mailAccountIds
     */
//    @Async
//    public void receiveMail(String userId,List<String> mailAccountIds){
//        try {
//            if(Collections3.isNotEmpty(mailAccountIds)){
//                for(String accountId:mailAccountIds){
//                    EmailUtils.syncToInbox(userId, accountId);
//                }
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(),e);
//        }
//    }

    /**
     * 接收邮件
     * @param userId
     * @param mailAccountId
     */
    @Async
    public void receiveMail(String userId,String mailAccountId){
        try {
            MailAccount mailAccount = null;
            if(StringUtils.isNotBlank(mailAccountId)){
                mailAccount = mailAccountManager.getById(mailAccountId);
            }
            stopAndAddUserMailMonitor(userId,mailAccount);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
