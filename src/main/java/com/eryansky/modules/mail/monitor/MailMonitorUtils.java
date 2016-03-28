/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail.monitor;

import com.eryansky.common.mail.exception.NotSupportedException;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.collections.ListUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.modules.mail._enum.AccountActivite;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.service.MailAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-11
 */
public class MailMonitorUtils {


    private static Logger logger = LoggerFactory.getLogger(MailMonitorUtils.class);

    private static MailAccountManager mailAccountManager = SpringContextHolder.getBean(MailAccountManager.class);
    private static final Map<String, List<MailMonitor>> dataMap = Maps.newConcurrentMap();

    public static void add(String userId) throws NotSupportedException {
        add(userId,null);
    }


    public static void add(String userId,MailAccount addMailAccount) throws NotSupportedException {
        if(check(userId)){
            logger.info(userId + " check true");
            return;
        }
        List<MailAccount> mailAccounts = mailAccountManager.findUserMailAcoounts(userId, AccountActivite.ACTIVITE.getValue());
        if(addMailAccount != null && AccountActivite.ACTIVITE.getValue().equals(addMailAccount.getActivate())){
            mailAccounts.add(addMailAccount);
            ListUtils.distinctList(mailAccounts);
        }

        if (Collections3.isNotEmpty(mailAccounts)) {
            logger.info("启动用户[{}]邮件监控", userId);
            List<MailMonitor> userMailMonitors = Lists.newArrayList();
            for (MailAccount mailAccount : mailAccounts) {
                MailMonitor mailMonitor = MailMonitor.getMailMonitor(mailAccount);
                userMailMonitors.add(mailMonitor);
            }
            dataMap.put(userId, userMailMonitors);

            for (MailMonitor mailMonitor : userMailMonitors) {
                try {
                    mailMonitor.enable(true);
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }

            }
        }
    }

    /**
     * 添加邮件账号
     * @param userId
     * @param mailAccountId
     * @throws NotSupportedException
     */
    public static void addUserAccount(String userId,String mailAccountId) throws NotSupportedException {
        logger.info("启动用户[{}]邮件[{}]监控", new Object[]{userId,mailAccountId});
        List<MailMonitor> userMailMonitors = dataMap.get(userId);
        boolean flag = true;
        if(Collections3.isEmpty(userMailMonitors)){
            userMailMonitors = new ArrayList<MailMonitor>(1);
        }else{
            for(MailMonitor mailMonitor:userMailMonitors){
                if(mailMonitor.isMailAccount(mailAccountId)){
                    flag = false;
                }
            }
        }

        if(flag){
            MailAccount mailAccount = mailAccountManager.getById(mailAccountId);
            if(mailAccount != null){
                MailMonitor mailMonitor = MailMonitor.getMailMonitor(mailAccount);
                try {
                    mailMonitor.enable(true);
                    logger.debug("mailAccountId:{}", new Object[]{userId});
                    userMailMonitors.add(mailMonitor);
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
                dataMap.put(userId, userMailMonitors);
            }
        }


    }

    private static boolean check(String userId) {
        List<MailMonitor> oldMailMonitors = dataMap.get(userId);
        return Collections3.isNotEmpty(oldMailMonitors);

    }

    /**
     * 停止用户邮件监听
     * @param userId
     */
    public static void stop(String userId) {
        List<MailMonitor> oldMailMonitors = dataMap.get(userId);
        if (Collections3.isNotEmpty(oldMailMonitors)) {
            logger.info("停止用户[{}]邮件监控", new Object[]{userId});
            for (MailMonitor mailMonitor : oldMailMonitors) {
                mailMonitor.dispose();
            }
        }
        dataMap.remove(userId);

    }

    /**
     * 停止邮件监听服务
     */
    public static void stopAll() {
        logger.info("停止邮件监听服务...");
        Set<String> keys = dataMap.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            List<MailMonitor> list = dataMap.get(iterator.next());
            for(MailMonitor mailMonitor:list){
                try {
                    mailMonitor.dispose();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        logger.info("停止邮件监听服务");

    }

}
