/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys._enum;

/**
 * 用户类型
 */
public enum UserType {

    System("0", "系统"),
	User("1", "员工");

	/**
	 * 值 String型
	 */
	private final String value;
	/**
	 * 描述 String型
	 */
	private final String description;

    UserType(String value, String description) {
		this.value = value;
		this.description = description;
	}

	/**
	 * 获取值
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
     * 获取描述信息
     * @return description
     */
	public String getDescription() {
		return description;
	}

	public static UserType getByValue(String value) {
		if (null == value)
			return null;
		for (UserType _enum : UserType.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static UserType getByDescription(String description) {
		if (null == description)
			return null;
		for (UserType _enum : UserType.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}