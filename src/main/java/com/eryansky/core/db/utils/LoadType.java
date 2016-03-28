/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.utils;

/**
 * 存储策略
 * Author: 温春平 wencp@jx.tobacco.gov.cn
 * Date: 2014-04-17 15:42
 */
public enum LoadType {

    /**
     * 更新模式(0)
     */
    Update(0, "更新模式"),
    /**
     * 插入模式(1)
     */
    Insert(1, "插入模式");

    /**
     * 值 Integer型
     */
    private final Integer value;
    /**
     * 描述 String型
     */
    private final String description;

    LoadType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     *
     * @return value
     */
    public Integer getValue() {
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

    public static LoadType getLoadType(Integer value) {
        if (null == value)
            return null;
        for (LoadType _enum : LoadType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static LoadType getLoadType(String description) {
        if (null == description)
            return null;
        for (LoadType _enum : LoadType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }

}