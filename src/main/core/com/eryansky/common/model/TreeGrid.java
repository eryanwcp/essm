/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.model;

import java.io.Serializable;
import java.util.Map;

/**
 * EasyUI treegrid模型.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-18 下午8:26:12
 * 
 */
@SuppressWarnings("serial")
public class TreeGrid implements Serializable {

	/**
	 * 静态变量 展开节点
	 */
	public static final String STATE_OPEN = "open";
	/**
	 * 静态变量 关闭节点
	 */
	public static final String STATE_CLOASED = "closed";

	/**
	 * 节点id
	 */
	private String id;
	/**
	 * 节点名称
	 */
	private String text;
	/**
	 * 上级节点id
	 */
	private String parentId;
	/**
	 * 上级节点名称
	 */
	private String parentText;
	private String code;
	private String src;
	private String note;
	/**
	 * 自定义属性
	 */
	private Map<String, Object> attributes;
	private String operations;// 其他参数
	/**
	 * 是否展开 (open,closed)-(默认值:open)
	 */
	private String state = STATE_OPEN;

	public TreeGrid() {
		super();
	}

	public TreeGrid(String id, String text, String parentId, String parentText,
			String code, String src, String note,
			Map<String, Object> attributes, String operations, String state) {
		super();
		this.id = id;
		this.text = text;
		this.parentId = parentId;
		this.parentText = parentText;
		this.code = code;
		this.src = src;
		this.note = note;
		this.attributes = attributes;
		this.operations = operations;
		this.state = state;
	}

	public String getOperations() {
		return operations;
	}

	public TreeGrid setOperations(String operations) {
		this.operations = operations;
        return this;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public TreeGrid setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
        return this;
	}

	public String getParentText() {
		return parentText;
	}

	public TreeGrid setParentText(String parentText) {
		this.parentText = parentText;
        return this;
	}

	public String getCode() {
		return code;
	}

	public TreeGrid setCode(String code) {
		this.code = code;
        return this;
	}

	public String getSrc() {
		return src;
	}

	public TreeGrid setSrc(String src) {
		this.src = src;
        return this;
	}

	public String getNote() {
		return note;
	}

	public TreeGrid setNote(String note) {
		this.note = note;
        return this;
	}

	public String getId() {
		return id;
	}

	public TreeGrid setId(String id) {
		this.id = id;
        return this;
	}

	public String getText() {
		return text;
	}

	public TreeGrid setText(String text) {
		this.text = text;
        return this;
	}

	public String getParentId() {
		return parentId;
	}

	public TreeGrid setParentId(String parentId) {
		this.parentId = parentId;
        return this;
	}

	public String getState() {
		return state;
	}

	public TreeGrid setState(String state) {
		this.state = state;
        return this;
	}

}
