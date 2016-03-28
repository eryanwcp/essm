/**
 *  Copyright (c) 2013 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.exception;

import java.sql.SQLException;

/**
 * 主键冲突异常类
 * @author 温春平 wencp@jx.tobacco.gov.cn
 *
 */
public class ConstraintViolationException extends Exception {

	private static final long serialVersionUID = 2436470648496539235L;
	
	public ConstraintViolationException(SQLException ex){
		super(ex);
	}
	

}
