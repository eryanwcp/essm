/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice._enum;

/**
 * 消息通道
 */
public enum TipMessage {

    Mail("mail", "邮件提醒"),
    SMS("sms", "短信提醒"),
    Weixin("weixin", "微信提醒"),
    Message("message", "消息提醒");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    TipMessage(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取描述信息
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public static TipMessage getByValue(String value) {
        if (null == value)
            return null;
        for (TipMessage _enum : TipMessage.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static TipMessage getByDescription(String description) {
        if (null == description)
            return null;
        for (TipMessage _enum : TipMessage.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}