/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.model;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * easyui分页组件datagrid、combogrid数据模型.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-16 上午9:57:59
 */
@SuppressWarnings("serial")
public class Datagrid<T> implements Serializable {
    /**
     * 总记录数
     */
    private long total;
    /**
     * 动态列
     */
    private List<Column> columns = new ArrayList<Column>(0);
    /**
     * 列表行
     */
    private List<T> rows = new ArrayList<T>(0);
    /**
     * 脚列表
     */
    private List<Map<String, Object>> footer = new ArrayList<Map<String, Object>>(0);

    public Datagrid() {
        super();
    }

    /**
     * @param total 总记录数
     * @param rows  列表行
     */
    public Datagrid(long total, List<T> rows) {
        this(total, rows, null, null);
    }

    /**
     * @param total   总记录数
     * @param rows    列表行
     * @param footer  脚列表
     * @param columns 动态列
     */
    public Datagrid(long total, List<T> rows, List<Map<String, Object>> footer,
                    List<Column> columns) {
        super();
        this.total = total;
        this.rows = rows;
        this.footer = footer;
        this.columns = columns;
    }

    /**
     * 总记录数
     */
    public long getTotal() {
        return total;
    }

    /**
     * 设置总记录数
     *
     * @param total 总记录数
     */
    public Datagrid<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    /**
     * 列表行
     */
    public List<T> getRows() {
        return rows;
    }

    /**
     * 设置列表行
     *
     * @param rows 列表行
     */
    public Datagrid<T> setRows(List<T> rows) {
        this.rows = rows;
        return this;
    }

    /**
     * 脚列表
     */
    public List<Map<String, Object>> getFooter() {
        return footer;
    }

    /**
     * 设置脚列表
     *
     * @param footer 脚列表
     */
    public Datagrid<T> setFooter(List<Map<String, Object>> footer) {
        this.footer = footer;
        return this;
    }

    /**
     * 动态列
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * 设置动态列
     *
     * @param columns 动态列
     */
    public Datagrid<T> setColumns(List<Column> columns) {
        this.columns = columns;
        return this;
    }

    /**
     * 添加动态列表
     *
     * @param column 动态列
     * @return
     */
    public Datagrid<T> addColumn(Column column) {
        if (this.columns == null) {
            this.columns = Lists.newArrayList();
        }
        this.columns.add(column);
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
