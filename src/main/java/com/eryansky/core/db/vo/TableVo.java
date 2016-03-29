/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 元数据表 Vo
 * @author 尔演&Eryan eryanwcp@gmail.com
 *
 */
public class TableVo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 模式
     */
    private String schema;
    /**
     * 目录
     */
    private String catalog;
    /**
     * 表名称
     */
    private String code;

    /**
     * 类型 表/视图
     */
    private String tableType;
    /**
     * 备注
     */
    private String comment;

    /**
     * 主键
     */
    private List<ColumnVo> primaryKeys;
    /**
     * 外键
     */
    private List<ColumnVo> foreignKeys;

    /**
     * 所有列
     */
    private List<ColumnVo> columnVos;


    public TableVo() {
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ColumnVo> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<ColumnVo> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public List<ColumnVo> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<ColumnVo> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public List<ColumnVo> getColumnVos() {
        return columnVos;
    }

    public void setColumnVos(List<ColumnVo> columnVos) {
        this.columnVos = columnVos;
    }

    @Override
    public String toString() {
        return "TableVo{" +
                "schema='" + schema + '\'' +
                ", catalog='" + catalog + '\'' +
                ", code='" + code + '\'' +
                ", tableType='" + tableType + '\'' +
                ", comment='" + comment + '\'' +
                ", primaryKeys=" + primaryKeys +
                ", foreignKeys=" + foreignKeys +
                ", columnVos=" + columnVos +
                '}';
    }
}
