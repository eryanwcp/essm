/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.model;

import java.util.HashMap;

/**
 * 查询参数类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-6-26
 */
public class Parameter extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 构造类，例：new Parameter(id, parentIds)
	 * @param values 参数值
	 */
	public Parameter(Object... values) {
		if (values != null){
			for (int i=0; i<values.length; i++){
				put("p"+(i+1), values[i]);
			}
		}
	}
	
	/**
	 * 构造类，例：new Parameter(new Object[][]{{"id", id}, {"parentIds", parentIds}})
	 * @param parameters 参数二维数组
	 */
	public Parameter(Object[][] parameters) {
		if (parameters != null){
			for (Object[] os : parameters){
				if (os.length == 2){
					put((String)os[0], os[1]);
				}
			}
		}
	}

	/**
	 * 构造参数
	 * @return
	 */
	public static Parameter newParameter() {
		return new Parameter();
	}

	/**
	 * 添加参数
	 * @param key
	 * @param object
	 * @return
	 */
	public Parameter addParameter(String key,Object object) {
		put(key,object);
		return this;
	}
}
