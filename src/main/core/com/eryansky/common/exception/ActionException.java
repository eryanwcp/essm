/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.exception;

/**
 * Action层异常, 继承自BaseException.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-43-10 上午12:08:55
 */
@SuppressWarnings("serial")
public class ActionException extends BaseException {

	public ActionException() {
		super();
	}

	public ActionException(String message) {
		super(message);
	}

	public ActionException(Throwable cause) {
		super(cause);
	}

	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}
}
