/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;


import com.eryansky.core.orm.mybatis.entity.DataEntity;

/**
 * 系统配置参数
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-12-18
 */
public class Config extends DataEntity<Config> {
    /**
     * 属性标识
     */
    private String code;
    /**
     * 属性值
     */
    private String value;
    /**
     * 备注
     */
    private String remark;

    public Config() {
        super();
    }

    public Config(String id) {
        super(id);
    }

    public Config(String code, String value, String remark) {
        this();
        this.code = code;
        this.value = value;
        this.remark = remark;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
