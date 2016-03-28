/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.hibernate;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eryansky.common.utils.reflection.ReflectionUtils;

/**
 * 含默认泛型DAO的EntityManager.
 * 
 * @param <T>
 *            领域对象类型
 * @param <PK>
 *            领域对象的主键类型
 * 
 *            eg. public class UserManager extends
 *            DefaultEntityManager<User,Long>{ }
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class DefaultEntityManager<T, PK extends Serializable> extends
		EntityManager<T, PK> {

	protected HibernateDao<T, PK> entityDao;// 默认的泛型DAO成员变量.

	/**
	 * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		Class<T> entityClass = ReflectionUtils
				.getClassGenricType(getClass());
		entityDao = new HibernateDao<T, PK>(sessionFactory, entityClass);
	}

	@Override
	protected HibernateDao<T, PK> getEntityDao() {
		return entityDao;
	}

}