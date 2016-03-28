/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

/**
 * 
 * 用户安全日志类型 枚举类型.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-8-11 下午10:48:23
 * 
 */
public enum SecurityType {
	/** 用户登录(0) */
	login(0, "用户登录"),
	/** 用户注销(1) */
	logout(1, "用户注销"),
	/** 用户非正常注销(2) */
    logout_abnormal(2, "用户非正常注销"),
	/** 强制下线(3) */
	offline(3, "强制下线");

	/**
	 * 值 Integer型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	SecurityType(Integer value, String description) {
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

	public static SecurityType getSecurityType(Integer value) {
		if (null == value)
			return null;
		for (SecurityType _enum : SecurityType.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static SecurityType getSecurityType(String description) {
		if (null == description)
			return null;
		for (SecurityType _enum : SecurityType.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}