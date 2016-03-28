/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys._enum;

/**
 * 机构类型
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-9-9 下午8:09:38
 * @version 1.0
 */
public enum OrganType {
    /** 机构(0) */
    organ(0, "机构(法人单位)"),
    /** 部门(1) */
    department(1, "部门"),
    /** 小组(2) */
    group(2, "小组");

	/**
	 * 值 Integer型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	OrganType(Integer value, String description) {
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

	public static OrganType getOrganType(Integer value) {
		if (null == value)
			return null;
		for (OrganType _enum : OrganType.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static OrganType getOrganType(String description) {
		if (null == description)
			return null;
		for (OrganType _enum : OrganType.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}