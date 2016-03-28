/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys._enum;

/**
 * 日志类型
 */
public enum LogType {
    /** 安全日志(0) */
    security(0, "安全"),
    /** 操作日志(1) */
    operate(1, "操作"),
    /** 访问日志(2) */
    access(2, "访问"),
    /** 异常(3) */
    exception(3, "异常");

	/**
	 * 值 Integer型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

    LogType(Integer value, String description) {
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

	public static LogType getLogType(Integer value) {
		if (null == value)
			return null;
		for (LogType _enum : LogType.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static LogType getLogType(String description) {
		if (null == description)
			return null;
		for (LogType _enum : LogType.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}