/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.entity;

/**
 *
 * 状态标识 枚举类型.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-8-11 下午10:48:23
 *
 */
public enum StatusState {
	/** 正常(0) */
	NORMAL("0", "正常"),
	/** 已删除(1) */
	DELETE("1", "已删除"),
	/** 待审核(2) */
	AUDIT("2", "待审核"),
	/** 锁定(3) */
	LOCK("3", "已锁定");

	/**
	 * 值 Integer型
	 */
	private final String value;
	/**
	 * 描述 String型
	 */
	private final String description;

	StatusState(String value, String description) {
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

	public static StatusState getStatusStateByValue(String value) {
		if (null == value)
			return null;
		for (StatusState _enum : StatusState.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}

	public static StatusState getStatusStateByDescription(String description) {
		if (null == description)
			return null;
		for (StatusState _enum : StatusState.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}