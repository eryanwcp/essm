/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * easyui动态列column模型.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-11-23 下午2:11:50
 */
public class Column implements Serializable{

    //对齐方式
    public static final String ALIGN_LEFT = "left";
    public static final String ALIGN_CENTER = "center";
    public static final String ALIGN_RIGHT = "right";

	/**
	 * 字段名称
	 */
	private String field;
	/**
	 * 显示标题
	 */
	private String title;
	/**
	 * 宽度
	 */
	private Integer width;
	/**
	 * 跨行数
	 */
	private Integer rowspan;
	/**
	 * 跨列数
	 */
	private Integer colspan;
	/**
	 * 是否选checkbox
	 */
	private Boolean checkbox;

	/**
	 * 索引
	 */
	private Integer index;

	/**
	 * 对齐方式(默认：'left',可选值：'left'，'right'，'center' 默认左对齐)
	 */
	private String align = ALIGN_LEFT;

    public Column() {
    }

    public Column(Integer index, String field, String title, Integer width,
			String align) {
		super();
		this.index = index;
		this.field = field;
		this.title = title;
		this.width = width;
		this.align = align;
	}

	/**
	 * 字段名称
	 */
	public String getField() {
		return field;
	}

	public Column setField(String field) {
		this.field = field;
        return this;
	}

	/**
	 * 显示标题
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置显示标题
	 */
	public Column setTitle(String title) {
		this.title = title;
        return this;
	}

	/**
	 * 宽度
	 */
	public Integer getWidth() {
		return width;
	}

	/**
	 * 设置宽度
	 */
	public Column setWidth(Integer width) {
		this.width = width;
        return this;
	}

	/**
	 * 跨行数
	 */
	public Integer getRowspan() {
		return rowspan;
	}

	/**
	 * 设置跨行数
	 */
	public Column setRowspan(Integer rowspan) {
		this.rowspan = rowspan;
        return this;
	}

	/**
	 * 跨列数
	 */
	public Integer getColspan() {
		return colspan;
	}

	/**
	 * 设置跨列数
	 */
	public Column setColspan(Integer colspan) {
		this.colspan = colspan;
        return this;
	}

	/**
	 * 是否选中checkbox
	 */
	public Boolean isCheckbox() {
		return checkbox;
	}

	/**
	 * 设置是否选中
	 */
	public Column setCheckbox(boolean checkbox) {
		this.checkbox = checkbox;
        return this;
	}

	/**
	 * 对齐方式(默认：'left',可选值：'left'，'right'，'center' 默认左对齐)
	 */
	public String getAlign() {
		return align;
	}

	/**
	 * 设置对齐方式(可选值：'left'，'right'，'center' 默认左对齐)
	 */
	public Column setAlign(String align) {
		this.align = align;
        return this;
	}

	/**
	 * 索引值
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * 设置索引值
	 * 
	 * @param index
	 */
	public Column setIndex(Integer index) {
		this.index = index;
        return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}