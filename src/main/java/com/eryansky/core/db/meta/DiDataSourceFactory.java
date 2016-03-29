/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db.meta;

import com.eryansky.common.exception.SystemException;
import com.eryansky.core.db.vo.DbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * 数据源工厂类 根据驱动获取数据库操作实例。
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 1.0
 * @date 2013-7-15 下午4:12:09
 */
public class DiDataSourceFactory {

	private Logger logger = LoggerFactory.getLogger(getClass());

	protected String catalog = null;
	protected String schema = null;
	
	private DbConfig dbConfig;

	private DiDataSource diDataSource;

	/**
	 * 数据库类型
	 */
	// public static final String DATABSE_TYPE_MYSQL ="mysql";
	// public static final String DATABSE_TYPE_POSTGRE ="postgresql";
	// public static final String DATABSE_TYPE_ORACLE ="oracle";
	public static final String DATABSE_TYPE_SQLSERVER = "sqlserver";
	public static final String DATABSE_TYPE_DB2 = "db2";
	public static final String DATABSE_TYPE_MySQL = "mysql";

	private DiDataSourceFactory() {

	}

	public DiDataSourceFactory(DbConfig dbConfig) {
		super();
		try {
			this.diDataSource = create(dbConfig);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 创建数据库实例
	 * 
	 * @param dbConfig
	 *            数据源配置VO
	 * @return
	 * @throws SQLException
	 */
	public static DiDataSource create(DbConfig dbConfig)
			throws SQLException {
		LoggerFactory.getLogger(DiDataSource.class).debug(
				dbConfig.toString());
		DiDataSource db = null;
		String jdbcUrl = dbConfig.getUrl();
		if (jdbcUrl.indexOf(DATABSE_TYPE_DB2) != -1) {
			db = new DB2DiDataSource(dbConfig);
		} else if (jdbcUrl.indexOf(DATABSE_TYPE_SQLSERVER) != -1) {
			db = new SQLServerDiDataSource(dbConfig);
		} else if (jdbcUrl.indexOf(DATABSE_TYPE_MySQL) != -1) {
			db = new MySQLDataSource(dbConfig);
		} else {
			throw new SystemException("未识别数据源：driverClassName["
					+ dbConfig.getDriverClassName() + "]");
		}
		return db;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public DiDataSource getDiDataSource() {
		return diDataSource;
	}

	public DbConfig getDbConfig() {
		return dbConfig;
	}

	public void setDbConfig(DbConfig dbConfig) {
		this.dbConfig = dbConfig;
	}
	
	

}
