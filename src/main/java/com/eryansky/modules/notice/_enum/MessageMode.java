/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice._enum;

/**
 * 消息状态
 */
public enum MessageMode {

    Draft("draft", "草稿"),
    Publishing("Publishing", "正在发布"),
    Published("Published", "已发布");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    MessageMode(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取描述信息
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public static MessageMode getByValue(String value) {
        if (null == value)
            return null;
        for (MessageMode _enum : MessageMode.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static MessageMode getByDescription(String description) {
        if (null == description)
            return null;
        for (MessageMode _enum : MessageMode.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}
