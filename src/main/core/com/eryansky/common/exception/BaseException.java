/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.exception;

/**
 * 基本异常, 继承自RuntimeException.
 * <br>从由Spring管理事务的函数中抛出时会触发事务回滚.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-43-10 上午12:08:55
 */
@SuppressWarnings("serial")
public class BaseException extends RuntimeException {

	public BaseException() {
		super();
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

}
