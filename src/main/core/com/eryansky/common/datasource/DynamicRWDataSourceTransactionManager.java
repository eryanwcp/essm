package com.eryansky.common.datasource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * 动态数据源JDBC事务管理
 */
public class DynamicRWDataSourceTransactionManager extends DataSourceTransactionManager {

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        //获取当前事务切点的方法的读写属性（在spring的xml或者事务注解中的配置）
        boolean readOnly = definition.isReadOnly();
        if (readOnly) {
            DataSourceContextHolder.setSlave();
        } else {
            DataSourceContextHolder.setMaster();
        }
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DataSourceContextHolder.clearDataSourceType();
    }
}