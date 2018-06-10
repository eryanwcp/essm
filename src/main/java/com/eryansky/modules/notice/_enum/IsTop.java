/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice._enum;

/**
 * 是否置顶
 */
public enum IsTop {
    /**
     * 不置顶(0)
     */
    No("0", "不置顶"),
    /**
     * 置顶(1)
     */
    Yes("1", "置顶");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    IsTop(String value, String description) {
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

    public static IsTop getByValue(String value) {
        if (null == value)
            return null;
        for (IsTop _enum : IsTop.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static IsTop getByDescription(String description) {
        if (null == description)
            return null;
        for (IsTop _enum : IsTop.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}