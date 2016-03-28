/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm;

import java.util.Date;

/**
 * 属性数据类型.
 * S代表String,I代表Integer,L代表Long, N代表Double, D代表Date,B代表Boolean
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-5-21 下午7:36:33 
 * @version 1.0
 */
public enum PropertyType {

	/**
	 * String
	 */
	S(String.class),
	/**
	 * Integer
	 */
	I(Integer.class),
	/**
	 * Long
	 */
	L(Long.class),
	/**
	 * Double
	 */
	N(Double.class), 
	/**
	 * Date
	 */
	D(Date.class), 
	/**
	 * Boolean
	 */
	B(Boolean.class);

	//类型Class
	private Class<?> clazz;

	private PropertyType(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * 获取类型Class
	 * 
	 * @return Class
	 */
	public Class<?> getValue() {
		return clazz;
	}
}