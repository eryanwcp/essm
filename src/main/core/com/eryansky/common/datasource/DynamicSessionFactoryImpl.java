/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.datasource;

import org.hibernate.*;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

/**
 * 动态数据源实现
 */
@SuppressWarnings({ "unchecked", "deprecation" })
public class DynamicSessionFactoryImpl implements DynamicSessionFactory {
 
    private static final long serialVersionUID = 5384069312247414885L;
    
    private Map<Object, SessionFactory> targetSessionFactorys;  
    private SessionFactory defaultTargetSessionFactory;

    /**
     * 重写该方法
     * @see DynamicSessionFactory#getHibernateSessionFactory()
     * @return
     */
    @Override
    public SessionFactory getHibernateSessionFactory() {
        SessionFactory targetSessionFactory = targetSessionFactorys.get(SessionFactoryContextHolder.getSessionFactory());
        if (targetSessionFactory != null) {  
            return targetSessionFactory;  
        } else if (defaultTargetSessionFactory != null) {  
            return defaultTargetSessionFactory;  
        }  
        return null;
    }


    @Override
    public void close() throws HibernateException {
        this.getHibernateSessionFactory().close();
    }

    @Override
    public boolean containsFetchProfileDefinition(String s) {
        return this.getHibernateSessionFactory().containsFetchProfileDefinition(s);
    }

    @Override
    public void evict(Class clazz) throws HibernateException {
        this.getHibernateSessionFactory().evict(clazz);
    }

    @Override
    public void evict(Class clazz, Serializable serializable) throws HibernateException {
        this.getHibernateSessionFactory().evict(clazz, serializable);
    }

    @Override
    public void evictCollection(String s) throws HibernateException {
        this.getHibernateSessionFactory().evictCollection(s);
    }

    @Override
    public void evictCollection(String s, Serializable serializable) throws HibernateException {
        this.getHibernateSessionFactory().evictCollection(s, serializable);
    }

    @Override
    public void evictEntity(String entity) throws HibernateException {
        this.getHibernateSessionFactory().evictEntity(entity);
    }

    @Override
    public void evictEntity(String entity, Serializable serializable) throws HibernateException {
        this.getHibernateSessionFactory().evictEntity(entity, serializable);
    }

    @Override
    public void evictQueries() throws HibernateException {
        this.getHibernateSessionFactory().evictQueries();
    }

    @Override
    public void evictQueries(String queries) throws HibernateException {
        this.getHibernateSessionFactory().evictQueries(queries);
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() {
        return this.getHibernateSessionFactory().getAllClassMetadata();
    }

    @Override
    public Map getAllCollectionMetadata() {
        return this.getHibernateSessionFactory().getAllCollectionMetadata();
    }

    @Override
    public Cache getCache() {
        return this.getHibernateSessionFactory().getCache();
    }

    @Override
    public ClassMetadata getClassMetadata(Class clazz) {
        return this.getHibernateSessionFactory().getClassMetadata(clazz);
    }

    @Override
    public ClassMetadata getClassMetadata(String classMetadata) {
        return this.getHibernateSessionFactory().getClassMetadata(classMetadata);
    }

    @Override
    public CollectionMetadata getCollectionMetadata(String collectionMetadata) {
        return this.getHibernateSessionFactory().getCollectionMetadata(collectionMetadata);
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        return this.getHibernateSessionFactory().getCurrentSession();
    }

    @Override
    public StatelessSessionBuilder withStatelessOptions() {
        return this.getHibernateSessionFactory().withStatelessOptions();
    }

    @Override
    public Set getDefinedFilterNames() {
        return this.getHibernateSessionFactory().getDefinedFilterNames();
    }

    @Override
    public FilterDefinition getFilterDefinition(String definition) throws HibernateException {
        return this.getHibernateSessionFactory().getFilterDefinition(definition);
    }

    @Override
    public Statistics getStatistics() {
        return this.getHibernateSessionFactory().getStatistics();
    }

    @Override
    public TypeHelper getTypeHelper() {
        return this.getHibernateSessionFactory().getTypeHelper();
    }

    @Override
    public boolean isClosed() {
        return this.getHibernateSessionFactory().isClosed();
    }

    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        return this.getHibernateSessionFactory().getSessionFactoryOptions();
    }

    @Override
    public SessionBuilder withOptions() {
        return this.getHibernateSessionFactory().withOptions();
    }

    @Override
    public Session openSession() throws HibernateException {
        return this.getHibernateSessionFactory().openSession();
    }

    @Override
    public StatelessSession openStatelessSession() {
        return this.getHibernateSessionFactory().openStatelessSession();
    }

    @Override
    public StatelessSession openStatelessSession(Connection connection) {
        return this.getHibernateSessionFactory().openStatelessSession(connection);
    }

    @Override
    public Reference getReference() throws NamingException {
        return this.getHibernateSessionFactory().getReference();
    }
 
    public void setTargetSessionFactorys(Map<Object, SessionFactory> targetSessionFactorys) {
        this.targetSessionFactorys = targetSessionFactorys;
    }
 
    public void setDefaultTargetSessionFactory(SessionFactory defaultTargetSessionFactory) {
        this.defaultTargetSessionFactory = defaultTargetSessionFactory;
    }

}