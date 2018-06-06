/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.model;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * easyui树形节点Menu模型.
 * 
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2013-9-11 下午10:01:30
 */
@SuppressWarnings("serial")
public class Menu implements Serializable {

	/**
	 * 节点id
	 */
	private String id;
	/**
	 * 树节点名称
	 */
	private String text;
	/**
	 * 前面的小图标样式
	 */
	private String iconCls;
	/**
	 * 父级ID
	 */
	private String pId;
	/**
	 * URL
	 */
	private String href;
	/**
	 * 自定义属性
	 */
	private Map<String, Object> attributes = Maps.newHashMap();
	/**
	 * 子节点
	 */
	private List<Menu> children = new ArrayList<Menu>(0);

	public Menu() {
	}

	public Menu(String id, String text) {
		this.id = id;
		this.text = text;
	}

	public Menu(String id, String text, String iconCls) {
		this.id = id;
		this.text = text;
		this.iconCls = iconCls;
	}

	/**
	 * 添加子节点.
	 * 
	 * @param childMenu
	 *            子节点
	 */
	public Menu addChild(Menu childMenu) {
		this.children.add(childMenu);
        return this;
	}

	/**
	 * 添加自定义属性
	 * @param key key
	 * @param object 值
	 * @return
	 */
	public Menu addAttribute(String key, Object object) {
		this.attributes.put(key,object);
		return this;
	}

	/**
	 * 返回自定义属性
	 * @param key
	 * @param <T>
	 * @return
	 */
	public <T> T getAttribute(String key) {
		return (T) attributes.get(key);
	}

	/**
	 * 节点id
	 */
	public String getId() {
		return id;
	}

	public Menu setId(String id) {
		this.id = id;
        return this;
	}

	/**
	 * 树节点名称
	 */
	public String getText() {
		return text;
	}

	public Menu setText(String text) {
		this.text = text;
        return this;
	}

    public String getHref() {
        return href;
    }

    public Menu setHref(String href) {
        this.href = href;
        return this;
    }

    /**
	 * 自定义属性
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public Menu setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
        return this;
	}

	/**
	 * 子节点
	 */
	public List<Menu> getChildren() {
		return children;
	}

	public Menu setChildren(List<Menu> children) {
		this.children = children;
        return this;
	}


	/**
	 * 图标样式
	 */
	public String getIconCls() {
		return iconCls;
	}

	public Menu setIconCls(String iconCls) {
		this.iconCls = iconCls;
        return this;
	}

	/**
	 * 父级ID
	 * @return
	 */
	public String getpId() {
		return pId;
	}

	/**
	 * 设置父级ID
	 * @param pId 父级ID
	 * @return
	 */
	public Menu setpId(String pId) {
		this.pId = pId;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
