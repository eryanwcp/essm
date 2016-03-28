/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.hibernate;

import org.hibernate.criterion.MatchMode;

/**
 * QBC like查询表达式.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-23 下午10:24:33
 * 
 */
@SuppressWarnings("serial")
public class LikeExpression extends org.hibernate.criterion.LikeExpression {

	protected LikeExpression(String propertyName, String value,
			MatchMode matchMode, Character escapeChar, boolean ignoreCase) {
		super(propertyName, value, matchMode, escapeChar, ignoreCase);
	}

}