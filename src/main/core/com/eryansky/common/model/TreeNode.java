/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * easyui树形节点TreeNode模型.
 * 
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2013-1-11 下午10:01:30
 */
@SuppressWarnings("serial")
public class TreeNode implements Serializable {

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
     * 父级节点id （用于zTree简单数据模型）
     */
    private String pId;
	/**
	 * 树节点名称
	 */
	private String text;
	/**
	 * 前面的小图标样式
	 */
	private String iconCls = "";
	/**
	 * 是否勾选状态（默认：否false）
	 */
	private Boolean checked = false;
	/**
	 * ztree 设置节点是否隐藏 checkbox / radio [setting.check.enable = true 时有效]
	 */
	private Boolean nocheck = false;
	/**
	 * 自定义属性
	 */
	private Map<String, Object> attributes = new HashMap<String, Object>(0);
	/**
	 * 子节点
	 */
	private List<TreeNode> children;
	/**
	 * 是否展开 (open,closed)-(默认值:open)
	 */
	private String state = STATE_OPEN;

	public TreeNode() {
		this(null, null, "");
	}

	/**
	 * 
	 * @param id
	 *            节点id
	 * @param text
	 *            树节点名称
	 */
	public TreeNode(String id, String text) {
		this(id, text, "");
	}

	/**
	 * 
	 * @param id
	 *            节点id
	 * @param text
	 *            树节点名称
	 * @param iconCls
	 *            图标样式
	 */
	public TreeNode(String id, String text, String iconCls) {
		this(id, text, STATE_OPEN, iconCls);
	}

	/**
	 * 
	 * @param id
	 *            节点id
	 * @param text
	 *            树节点名称
	 * @param state
	 *            是否展开
	 * @param iconCls
	 *            图标样式
	 */
	public TreeNode(String id, String text, String state, String iconCls) {
		this.id = id;
		this.text = text;
		this.state = state;
		this.iconCls = iconCls;
		this.children = Lists.newArrayList();
	}

	/**
	 * 
	 * @param id
	 *            节点id
	 * @param text
	 *            树节点名称
	 * @param iconCls
	 *            图标样式
	 * @param checked
	 *            是否勾选状态（默认：否）
	 * @param attributes
	 *            自定义属性
	 * @param children
	 *            子节点
	 * @param state
	 *            是否展开
	 */
	public TreeNode(String id, String text, String iconCls, Boolean checked,
			Map<String, Object> attributes, List<TreeNode> children,
			String state) {
		super();
		this.id = id;
		this.text = text;
		this.iconCls = iconCls;
		this.checked = checked;
		this.attributes = attributes;
		this.children = children;
		this.state = state;
	}

	/**
	 * 添加子节点.
	 * 
	 * @param childNode
	 *            子节点
	 */
	public TreeNode addChild(TreeNode childNode) {
		this.children.add(childNode);
        return this;
	}

	/**
	 * 添加自定义属性
	 * @param key key
	 * @param object 值
	 * @return
	 */
	public TreeNode addAttributes(String key, Object object) {
		this.attributes.put(key,object);
		return this;
	}

	/**
	 * 节点id
	 */
	public String getId() {
		return id;
	}

	public TreeNode setId(String id) {
		this.id = id;
        return this;
	}

    /**
     * 父级节点id （用于zTree简单数据模型）
     * @return
     */
    public String getpId() {
        return pId;
    }

    /**
     * @param pId 父级节点id （用于zTree简单数据模型）
     * @return
     */
    public TreeNode setpId(String pId) {
        this.pId = pId;
        return this;
    }

    /**
	 * 树节点名称
	 */
	public String getText() {
		return text;
	}

	public TreeNode setText(String text) {
		this.text = text;
        return this;
	}

	/**
	 * 是否勾选状态（默认：否）
	 */
	public Boolean getChecked() {
		return checked;
	}

	public TreeNode setChecked(Boolean checked) {
		this.checked = checked;
        return this;
	}

	public Boolean getNocheck() {
		return nocheck;
	}

	public TreeNode setNocheck(Boolean nocheck) {
		this.nocheck = nocheck;
		return this;
	}

	/**
	 * 自定义属性
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public TreeNode setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
        return this;
	}

	/**
	 * 子节点
	 */
	public List<TreeNode> getChildren() {
		return children;
	}

	public TreeNode setChildren(List<TreeNode> children) {
		this.children = children;
        return this;
	}

	/**
	 * 是否展开
	 */
	public String getState() {
		return state;
	}

	public TreeNode setState(String state) {
		this.state = state;
        return this;
	}

	/**
	 * 图标样式
	 */
	public String getIconCls() {
		return iconCls;
	}

	public TreeNode setIconCls(String iconCls) {
		this.iconCls = iconCls;
        return this;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
