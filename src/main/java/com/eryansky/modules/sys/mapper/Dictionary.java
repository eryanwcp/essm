/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;

import com.eryansky.common.excel.annotation.Excel;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 系统字典类型Entity.
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2013-1-23 下午9:08:36
 */

@SuppressWarnings("serial")
public class Dictionary extends DataEntity<Dictionary> {

    /**
     * 类型名称
     */
    @Excel(exportName = "类型名称", exportFieldWidth = 30)
    private String name;
    /**
     * 类型编码
     */
    @Excel(exportName = "类型编码", exportFieldWidth = 20)
    private String code;

    /**
     * 父级类型 即分组
     */
    private Dictionary group;
    /**
     * 备注
     */
    private String remark;

    /**
     * 排序
     */
    @Excel(exportName = "排序", exportFieldWidth = 10)
    private Integer orderNo;

    public Dictionary() {
        super();
    }

    public Dictionary(String id) {
        super(id);
    }

    /**
     * 系统数据字典类型构造函数.
     *
     * @param name
     *            类型名称
     * @param code
     *            类型编码
     * @param orderNo
     *            排序
     */
    public Dictionary(String name, String code, Integer orderNo) {
        this();
        this.name = name;
        this.code = code;
        this.orderNo = orderNo;
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

    @JsonIgnore
    public Dictionary getGroup() {
        return group;
    }

    public void setGroup(Dictionary group) {
        this.group = group;
    }



    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGroupName() {
        if (group != null) {
            return group.getName();
        }
        return null;
    }

    @JsonProperty("_parentId")
    public String getGroupId() {
        if (group != null) {
            return group.getId();
        }
        return null;
    }

}