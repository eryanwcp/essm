package com.eryansky.modules.mail.entity.common;

import com.eryansky.modules.mail._enum.EmailPriority;

import java.util.Date;

/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
public interface IEmail {

    /**
     * @return 邮件ID
     */
    String getEmailId();

    /**
     * @return 邮件标题
     */
    String getTitle();
    /**
     *
     * @return 邮件内容
     */
    String getContent();
    /**
     *
     * @return 邮件内容摘要
     */
    String getSummary();

    /**
     * 是否需要回执 {@link com.eryansky.utils.YesOrNo}
     * @return
     */
    Integer getIsReceipt();

    /**
     * @return 邮件优先级 {@link EmailPriority}
     */
    Integer getPriority();
    /**
     * @return 邮件优先级 显示 {@link EmailPriority}
     */
    String getPriorityView();

    /**
     * @return 邮件大小 单位：字节
     */
    Long getEmailSize();

    /**
     * 发件人
     * @return
     */
    String getSender();

    /**
     * 发件人
     * @return
     */
    String getSenderName();

    /**
     * 发送时间
     * @return
     */
    public Date getSendTime();

    /**
     * 收件人 名称
     * @return
     */
    String getToNames();

    /**
     * 抄送人 名称
     * @return
     */
    String getCcNames();
    /**
     * 密送人 名称
     * @return
     */
    String getBccNames();


    /**
     * 邮件类型
     * @return
     */
    Integer getMailType();
}
