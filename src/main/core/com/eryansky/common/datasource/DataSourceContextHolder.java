/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.datasource;

import javax.sql.DataSource;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-08-13
 */
public abstract class DataSourceContextHolder {

    private static final String MASTER = "master";
    private static final String SLAVE  = "slave";

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
    private static final ThreadLocal<DataSource> masterLocal = new ThreadLocal<DataSource>();
    private static final ThreadLocal<DataSource> slaveLocal = new ThreadLocal<DataSource>();


    public static void setDataSourceType(String dataSourceType) {
        contextHolder.set(dataSourceType);
    }

    public static String getDataSourceType() {
        return contextHolder.get();
    }

    public static void clearDataSourceType() {
        contextHolder.remove();
        masterLocal.remove();
        slaveLocal.remove();
    }


    public static boolean isMaster(){
        return MASTER.endsWith(getDataSourceType());
    }

    public static boolean isSlave(){
        return SLAVE.endsWith(getDataSourceType());
    }

    public static void setSlave(DataSource dataSource){
        slaveLocal.set(dataSource);
    }

    public static void setMaster(DataSource dataSource){
        masterLocal.set(dataSource);
    }

    public static void setMaster(){
        setDataSourceType(MASTER);
    }

    public static void setSlave(){
        setDataSourceType(SLAVE);
    }

}