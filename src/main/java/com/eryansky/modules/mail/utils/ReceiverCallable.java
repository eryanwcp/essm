/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.modules.mail._enum.AccountActivite;
import com.eryansky.modules.mail.service.MailAccountManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-08-21
 */
public class ReceiverCallable implements Callable<Boolean> {

    private String userId;
    private String mailAccountId;
    private static MailAccountManager mailAccountManager = SpringContextHolder.getBean(MailAccountManager.class);
    private List<String> mailAccountIds = new ArrayList<String>(1);

    private ReceiverCallable() {

    }

    public ReceiverCallable(String userId) {
        this.userId = userId;
        mailAccountIds = mailAccountManager.getUserMailAcoountIds(userId, AccountActivite.ACTIVITE.getValue());
    }

    public ReceiverCallable(String userId, String mailAccountId) {
        this.userId = userId;
        mailAccountIds.add(mailAccountId);
    }

    @Override
    public Boolean call() throws Exception {
        for (String mailAccountId : mailAccountIds) {
            EmailUtils.syncToInbox(mailAccountId, userId);
        }

        return true;
    }
}
