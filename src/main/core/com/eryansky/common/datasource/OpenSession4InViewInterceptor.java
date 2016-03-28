/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.datasource;

import org.hibernate.SessionFactory;


/**
 * 动态数据源支持
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-07-02
 */
public class OpenSession4InViewInterceptor extends org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor {

	public SessionFactory getSessionFactory() {
		DynamicSessionFactory dynamicSessionFactory = (DynamicSessionFactory) super.getSessionFactory();
		SessionFactory hibernateSessionFactory = dynamicSessionFactory.getHibernateSessionFactory();
		return hibernateSessionFactory;
	}

}
