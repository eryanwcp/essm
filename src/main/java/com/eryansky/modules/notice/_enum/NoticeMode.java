/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice._enum;

/**
 * 邮件状态
 */
public enum NoticeMode {
    /**
     * 未发布(0)
     */
    UnPublish("0", "未发布"),
    /**
     * 生效(1)
     */
    Effective("1", "已发布"),
    /**
     * 待生效(2)
     */
    Invalidated("2", "已失效"),
    /**
     * 正在发布(3)
     */
    Publishing("3", "正在发布");
    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    NoticeMode(String value, String description) {
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

    public static NoticeMode getByValue(String value) {
        if (null == value)
            return null;
        for (NoticeMode _enum : NoticeMode.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static NoticeMode getByDescription(String description) {
        if (null == description)
            return null;
        for (NoticeMode _enum : NoticeMode.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}