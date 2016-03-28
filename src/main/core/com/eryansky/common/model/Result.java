/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.model;

import com.eryansky.common.utils.mapper.JsonMapper;

import java.io.Serializable;

/**
 * Ajax请求Json响应结果模型.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-16 上午9:57:59
 */
@SuppressWarnings("serial")
public class Result implements Serializable {

	/**
	 * 成功
	 */
	public static final int SUCCESS = 1;
	/**
	 * 警告
	 */
	public static final int WARN = 2;
	/**
	 * 失败
	 */
	public static final int ERROR = 0;
	
	/**
	 * 成功消息
	 */
	public static final String SUCCESS_MSG = "操作成功！";
	/**
	 * 警告消息
	 */
	public static final String WARN_MSG = "警告信息！";
	/**
	 * 失败消息
	 */
	public static final String ERROR_MSG = "操作失败:发生未知异常！";

	/**
	 * 结果状态码(可自定义结果状态码) 1:操作成功 0:操作失败
	 */
	private int code;
	/**
	 * 响应结果描述
	 */
	private String msg;
	/**
	 * 其它数据信息（比如跳转地址）
	 */
	private Object obj;

	public Result() {
		super();
	}

	/**
	 * 
	 * @param code
	 *            结果状态码(可自定义结果状态码或者使用内部静态变量 1:操作成功 0:操作失败 2:警告)
	 * @param msg
	 *            响应结果描述
	 * @param obj
	 *            其它数据信息（比如跳转地址）
	 */
	public Result(int code, String msg, Object obj) {
		super();
		this.code = code;
		this.msg = msg;
		this.obj = obj;
	}

	/**
	 * 默认操作成功结果.
	 */
	public static Result successResult() {
		return new Result(SUCCESS, SUCCESS_MSG, null);
	}
	/**
	 * 默认操作警告结果.
	 */
	public static Result warnResult() {
		return new Result(WARN, WARN_MSG, null);
	}


	/**
	 * 默认操作失败结果.
	 */
	public static Result errorResult() {
		return new Result(ERROR, ERROR_MSG, null);
	}

	/**
	 * 结果状态码(可自定义结果状态码) 1:操作成功 0:操作失败
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 设置结果状态码(约定 1:操作成功 0:操作失败 2:警告)
	 * 
	 * @param code
	 *            结果状态码
	 */
	public Result setCode(int code) {
		this.code = code;
        return this;
	}

	/**
	 * 响应结果描述
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * 设置响应结果描述
	 * 
	 * @param msg
	 *            响应结果描述
	 */
	public Result setMsg(String msg) {
		this.msg = msg;
        return this;
	}

	/**
	 * 其它数据信息（比如跳转地址）
	 */
	public Object getObj() {
		return obj;
	}

	/**
	 * 设置其它数据信息（比如跳转地址）
	 * 
	 * @param obj
	 *            其它数据信息（比如跳转地址）
	 */
	public Result setObj(Object obj) {
		this.obj = obj;
        return this;
	}

	@SuppressWarnings("static-access")
	@Override
	public String toString() {
		return new JsonMapper().nonDefaultMapper().toJson(this);
	}
}
