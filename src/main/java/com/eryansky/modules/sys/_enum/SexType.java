/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys._enum;

/**
 * 
 * 性别标识 枚举类型.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-8-11 下午10:48:23
 * 
 */
public enum SexType {
	/** 女(0) */
	girl(0, "女"),
	/** 男(1) */
	boy(1, "男"),
	/** 保密(2) */
	secrecy(2, "保密");

	/**
	 * 值 Integer型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	SexType(Integer value, String description) {
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

	public static SexType getSexType(Integer value) {
		if (null == value)
			return null;
		for (SexType _enum : SexType.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static SexType getSexType(String description) {
		if (null == description)
			return null;
		for (SexType _enum : SexType.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}