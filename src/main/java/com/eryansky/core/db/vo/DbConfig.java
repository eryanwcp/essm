/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db.vo;

import java.io.Serializable;

/**
 * 数据库数据源Vo 
 * @author  尔演&Eryan eryanwcp@gmail.com
 */
@SuppressWarnings("serial")
public class DbConfig implements Serializable {

	/**
	 * JDBC 驱动类路径
	 */
	private String driverClassName;
	/**
	 * JDBC url
	 */
	private String url;
	/**
	 * JDBC 数据库用户名
	 */
	private String username;
	/**
	 * JDBC 数据库密码
	 */
	private String password;

	/**
	 * 模式
	 */
	private String schema;

	public DbConfig() {
	}

	public DbConfig(String driverClassName, String url, String username,
			String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		this.driverClassName = driverClassName;
	}
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Override
	public String toString() {
		return "DbConfig [driverClassName=" + driverClassName + ", url=" + url
				+ ", username=" + username + ", password=" + password
				+ ", schema=" + schema + "]";
	}

	
}
