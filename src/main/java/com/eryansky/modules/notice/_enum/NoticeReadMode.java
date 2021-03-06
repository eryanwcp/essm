/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice._enum;

/**
 * 通知公告
 */
public enum NoticeReadMode {
    /**
     * 未读(0)
     */
    unreaded("0", "未读"),
    /**
     * 已读(1)
     */
    readed("1", "已读"),
    /**
     * 未知(2)
     */
    unknow("2", "未知");


    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    NoticeReadMode(String value, String description) {
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

    public static NoticeReadMode getByValue(String value) {
        if (null == value)
            return null;
        for (NoticeReadMode _enum : NoticeReadMode.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static NoticeReadMode getByDescription(String description) {
        if (null == description)
            return null;
        for (NoticeReadMode _enum : NoticeReadMode.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}