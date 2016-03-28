/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.datasource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import javax.sql.DataSource;

/**
 * 重写HibernateTransactionManager事务管理器，实现自己的动态的事务管理器
 */
public class DynamicTransactionManager extends HibernateTransactionManager {
 
    private static final long serialVersionUID = -4655721479296819154L;
    
    /** 
     * @see org.springframework.orm.hibernate4.HibernateTransactionManager#getDataSource()
     */
    @Override
    public DataSource getDataSource() {
        return SessionFactoryUtils.getDataSource(getSessionFactory());
    }

    /**
     * @see org.springframework.orm.hibernate4.HibernateTransactionManager#getSessionFactory()
     */
    @Override
    public SessionFactory getSessionFactory() {
        DynamicSessionFactory dynamicSessionFactory = (DynamicSessionFactory) super.getSessionFactory();
        SessionFactory hibernateSessionFactory = dynamicSessionFactory.getHibernateSessionFactory();
        return hibernateSessionFactory;  
    }

    //重写afterPropertiesSet，跳过数据源的初始化等操作
    public void afterPropertiesSet() {
        return;
    }
}