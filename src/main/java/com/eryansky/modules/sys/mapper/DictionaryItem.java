/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;

import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 数据字典项
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2013-1-23 下午9:08:36
 */
@SuppressWarnings("serial")
public class DictionaryItem extends DataEntity<DictionaryItem> {

    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数编码
     */
    private String code;
    /**
     * 参数值
     */
    private String value;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer orderNo;
    /**
     * 上级编码
     */
    private DictionaryItem parent;

    /**
     * 系统字典类型
     */
    private Dictionary dictionary;

    public DictionaryItem() {
        super();
    }

    public DictionaryItem(String id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @JsonIgnore
    public DictionaryItem getParent() {
        return parent;
    }

    public void setParent(DictionaryItem parent) {
        this.parent = parent;
    }

    @JsonIgnore
    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public String getParentId() {
        if (parent != null) {
            return parent.getId();
        }
        return null;
    }


    public String getParentCode() {
        if (parent != null) {
            return parent.getCode();
        }
        return null;
    }


    public String getParentName() {
        if (parent != null) {
            return parent.getName();
        }
        return null;
    }


    public String getDictionaryCode() {
        if (dictionary != null) {
            return dictionary.getCode();
        }
        return null;
    }

    public String getDictionaryId() {
        if (dictionary != null) {
            return dictionary.getId();
        }
        return null;
    }


    public String getDictionaryName() {
        if (dictionary != null) {
            return dictionary.getName();
        }
        return null;
    }

}