/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice._enum;

/**
 * 接收对象类型
 */
public enum ReceiveObjectType {
    /**
     * 用户(0)
     */
    User("0", "用户"),
    /**
     * 联系人(1)
     */
    UserGroup("1", "用户组"),
    /**
     * 部门(2)
     */
    Organ("2", "部门"),
    /**
     * 联系人(3)
     */
    Contact("3", "联系人"),
    /**
     * 联系人组(4)
     */
    ContactGroup("4", "联系人组");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    ReceiveObjectType(String value, String description) {
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

    public static ReceiveObjectType getByValue(String value) {
        if (null == value)
            return null;
        for (ReceiveObjectType _enum : ReceiveObjectType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static ReceiveObjectType getByDescription(String description) {
        if (null == description)
            return null;
        for (ReceiveObjectType _enum : ReceiveObjectType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}