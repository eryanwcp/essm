/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db.exception;

import java.sql.SQLException;

/**
 * 主键冲突异常类
 * @author 尔演&Eryan eryanwcp@gmail.com
 *
 */
public class ConstraintViolationException extends Exception {

	private static final long serialVersionUID = 2436470648496539235L;
	
	public ConstraintViolationException(SQLException ex){
		super(ex);
	}
	

}
