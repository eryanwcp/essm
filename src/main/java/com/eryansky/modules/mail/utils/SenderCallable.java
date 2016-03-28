/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.modules.mail.entity.Email;
import com.eryansky.modules.mail.entity.ReceiveInfo;
import com.eryansky.modules.mail.service.EmailManager;
import com.eryansky.modules.mail.service.ReceiveInfoManager;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-08-21
 */
public class SenderCallable implements Callable<Boolean> {

    private String mailAccountId;
    private String emailId;
    private static EmailManager emailManager = SpringContextHolder.getBean(EmailManager.class);
    private static ReceiveInfoManager receiveInfoManager = SpringContextHolder.getBean(ReceiveInfoManager.class);
    private List<ReceiveInfo> receiveInfos;

    private SenderCallable() {

    }

    public SenderCallable(String emailId, String mailAccountId) {
        this.emailId = emailId;
        this.mailAccountId = mailAccountId;
        receiveInfos = receiveInfoManager.findReceiveInfos(emailId,null);
    }

    @Override
    public Boolean call() throws Exception {
        Email email = emailManager.loadById(emailId);
        EmailUtils.sendMail(mailAccountId, email,receiveInfos);
        return true;
    }
}
