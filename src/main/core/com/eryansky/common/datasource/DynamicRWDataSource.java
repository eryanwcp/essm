/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 读写分离动态数据源
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-08-13
 */
public class DynamicRWDataSource extends AbstractRoutingDataSource {

    private Logger logger = LoggerFactory.getLogger(DynamicRWDataSource.class);
    private AtomicInteger counter = new AtomicInteger();

    private DataSource master;

    private List<DataSource> slaves;

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceType();
    }

    /**
     * 根据标识获取数据源
     *
     * @return
     */
    @Override
    protected DataSource determineTargetDataSource() {
        DataSource dataSource = null;
        if (DataSourceContextHolder.isMaster()) {
            dataSource = master;
        } else if (DataSourceContextHolder.isSlave()) {
            int count = counter.getAndIncrement();
            if (count > 1000000)
                counter.set(0);
            //简单轮循
            int sequence = count % slaves.size();
            dataSource = slaves.get(sequence);
        } else
            dataSource = master;

        return dataSource;
    }

    public DataSource getMaster() {
        return master;
    }

    public void setMaster(DataSource master) {
        this.master = master;
    }

    public List<DataSource> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<DataSource> slaves) {
        this.slaves = slaves;
    }

    @Override
    public void afterPropertiesSet() {
        //do nothing
    }
}