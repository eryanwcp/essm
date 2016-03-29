/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db;

import com.eryansky.common.exception.DaoException;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC事务管理器
 * Author: 尔演&Eryan eryanwcp@gmail.com
 * Date: 2014-02-20 09:50
 */
public class TransactionManager {

    private Connection conn;

    public TransactionManager(Connection conn) {
        this.conn = conn;
    }

    /**
     * 开启事务
     *
     * @throws DaoException
     */
    public void beginTransaction() throws DaoException {
        try {
            if (conn != null) {
                conn.setAutoCommit(false);  //把事务提交方式改为手工提交
            }
        } catch (SQLException e) {
            throw new DaoException("开启事务时出现异常", e);
        }
    }

    /**
     * 提交事务并
     *
     * @throws DaoException
     */
    public void commit() throws DaoException {
        try {
            if (conn != null) {
                conn.commit(); //提交事务
            }
        } catch (SQLException e) {
            throw new DaoException("提交事务时出现异常", e);
        }
    }

    /**
     * 提交事务并关闭连接
     *
     * @throws DaoException
     */
    public void commitAndClose() throws DaoException {
        try {
            if (conn != null) {
                conn.commit(); //提交事务
            }
        } catch (SQLException e) {
            throw new DaoException("提交事务时出现异常", e);
        } finally {
            try {
                DbUtils.close(conn);
            } catch (SQLException se) {
                throw new DaoException("关闭连接时出现异常", se);
            }
        }
    }

    /**
     * 回滚并关闭连接
     *
     * @throws DaoException
     */
    public void rollbackAndClose() throws DaoException {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new DaoException("回滚事务时出现异常", e);
        } finally {
            try {
                DbUtils.close(conn);
            } catch (SQLException se) {
                throw new DaoException("关闭连接时出现异常", se);
            }
        }
    }

    public Connection getConn() {
        return conn;
    }
}
