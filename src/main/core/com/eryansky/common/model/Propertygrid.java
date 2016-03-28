/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * easyui propertygrid.
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
@SuppressWarnings("serial")
public class Propertygrid implements Serializable {
    /**
     * 总记录数
     */
    private long total;
    /**
     * 列表 Map包含属性：name、value、group、editor等
     */
    private List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>(0);

    public Propertygrid() {
    }

    public Propertygrid(long total, List<Map<String, Object>> rows) {
        this.total = total;
        this.rows = rows;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public Propertygrid setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public Propertygrid setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
