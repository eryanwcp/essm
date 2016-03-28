/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail._enum;

/**
 * 发件箱状态
 */
public enum OutboxMode {
    /** 已发送(0) */
    Sent(0, "已发送"),
    /** 已删除(1) */
    Deleted(1, "已删除"),
	/** 草稿(2) */
	Draft(2, "草稿"),
	/** 发送失败(3) */
	SendFail(3, "发送失败"),
	/** 正在发送(4) */
	Sending(4, "正在发送");
	
	/**
	 * 值 String型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	OutboxMode(Integer value, String description) {
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

	public static OutboxMode getOutboxMode(Integer value) {
		if (null == value)
			return null;
		for (OutboxMode _enum : OutboxMode.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static OutboxMode getOutboxMode(String description) {
		if (null == description)
			return null;
		for (OutboxMode _enum : OutboxMode.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}