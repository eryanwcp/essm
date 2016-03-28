package com.eryansky.codegen.vo;

import java.io.Serializable;

/**
 * 数据库数据源Vo
 * User: 尔演&Eryan eryanwcp@gmail.com
 * Date: 13-12-27 上午9:45
 */
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
     * 目录
     */
    private String catalog;
    /**
     * 模式
     */
    private String schema;


    public DbConfig() {
    }

    public DbConfig(String driverClassName, String url, String username, String password) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
    }


    public DbConfig(String driverClassName, String url, String username, String password, String catalog, String schema) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.catalog = catalog;
        this.schema = schema;
    }

    public String getUrl() {
        return url;
    }

    public DbConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DbConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DbConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public DbConfig setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public DbConfig setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public String getCatalog() {
        return catalog;
    }

    public DbConfig setCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    @Override
    public String toString() {
        return "DbConfig{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", schema='" + schema + '\'' +
                ", catalog='" + catalog + '\'' +
                '}';
    }
}
