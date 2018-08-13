/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice._enum;

/**
 * 消息接收对象类型
 */
public enum MessageReceiveObjectType {

    Organ("organ", "单位/部门"),
    Member("member", "会员"),
    User("user", "用户");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    MessageReceiveObjectType(String value, String description) {
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

    public static MessageReceiveObjectType getByValue(String value) {
        if (null == value)
            return null;
        for (MessageReceiveObjectType _enum : MessageReceiveObjectType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static MessageReceiveObjectType getByDescription(String description) {
        if (null == description)
            return null;
        for (MessageReceiveObjectType _enum : MessageReceiveObjectType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}
