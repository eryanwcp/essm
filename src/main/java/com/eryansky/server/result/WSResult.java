/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.server.result;

import com.eryansky.common.utils.reflection.ReflectionUtils;
import com.eryansky.server.WsConstants;

import javax.xml.bind.annotation.XmlType;


/**
 * WebService返回结果基类,定义所有返回码.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-11 下午9:25:48 
 *
 */
@XmlType(name = "WSResult", namespace = WsConstants.NS)
public class WSResult {
	//-- 返回代码定义 --//
	// 按项目的规则进行定义，比如1xx代表客户端参数错误，2xx代表业务错误等.
	public static final String SUCCESS = "0";
	public static final String PARAMETER_ERROR = "101";
	public static final String IMAGE_ERROR = "201";

	public static final String SYSTEM_ERROR = "500";
	public static final String SYSTEM_ERROR_MESSAGE = "Runtime unknown internal error.";

	//-- WSResult基本属性 --//
	private String code = SUCCESS;
	private String message;

	/**
	 * 创建结果.
	 */
	public static <T extends WSResult> T buildResult(Class<T> resultClass, String resultCode, String resultMessage) {
		try {
			T result = resultClass.newInstance();
			result.setResult(resultCode, resultMessage);
			return result;
		} catch (Exception ex) {
			throw ReflectionUtils.convertReflectionExceptionToUnchecked(ex);
		}
	}

	/**
	 * 创建默认异常结果.
	 */
	public static <T extends WSResult> T buildDefaultErrorResult(Class<T> resultClass) {
		return buildResult(resultClass, SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 设置返回结果.
	 */
	public void setResult(String resultCode, String resultMessage) {
		this.code = resultCode;
		this.message = resultMessage;
	}
}
