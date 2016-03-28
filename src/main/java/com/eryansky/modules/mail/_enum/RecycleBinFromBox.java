/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail._enum;

/**
 * 邮件是否草稿
 */
public enum RecycleBinFromBox {

    /** 发件箱(0) */
    Outbox(0, "发件箱") ,
    /** 草稿箱(1) */
    Draftbox(1, "草稿箱") ,
    /** 收件箱(2) */
	Inbox(2, "收件箱");

	/**
	 * 值 String型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	RecycleBinFromBox(Integer value, String description) {
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

	public static RecycleBinFromBox getRecycleBinFromBox(Integer value) {
		if (null == value)
			return null;
		for (RecycleBinFromBox _enum : RecycleBinFromBox.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static RecycleBinFromBox getRecycleBinFromBox(String description) {
		if (null == description)
			return null;
		for (RecycleBinFromBox _enum : RecycleBinFromBox.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}