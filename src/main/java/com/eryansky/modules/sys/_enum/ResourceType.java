/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys._enum;

/**
 * 
 * 资源类型标识 枚举类型.
 * <br>菜单(0) 功能(1)
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-01-28 上午10:48:23
 * 
 */
public enum ResourceType {
	/** 应用(10) */
	app(10, "应用"),
	/** 菜单(0) */
	menu(0, "菜单"),
	/** 按钮(1) */
	function(1, "功能"),
    /** CMS栏目资源(21) */
    CMS(21, "栏目");

	/**
	 * 值 Integer型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	ResourceType(Integer value, String description) {
		this.value = value;
		this.description = description;
	}

	/**
	 * 获取值
	 * @return value
	 */
	public Integer getValue() {
		return value;
	}

	/**
     * 获取描述信息
     * @return description
     */
	public String getDescription() {
		return description;
	}

	public static ResourceType getResourceType(Integer value) {
		if (null == value)
			return null;
		for (ResourceType _enum : ResourceType.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static ResourceType getResourceType(String description) {
		if (null == description)
			return null;
		for (ResourceType _enum : ResourceType.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}