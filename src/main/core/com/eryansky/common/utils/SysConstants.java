/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils;

import com.eryansky.common.utils.io.PropertiesLoader;


/**
 * 项目中用到的静态变量.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-8-20 上午11:40:56
 */
public class SysConstants {
    /**
     * session 验证码key
     */
    public static final String SESSION_VALIDATE_CODE = "validateCode";
    
    private static PropertiesLoader appconfig = null;
    private static PropertiesLoader sensitive = null;
    private static PropertiesLoader sqlfilter = null;
    
    /**
     * 配置文件(appconfig.properties)
     */
    public static PropertiesLoader getAppConfig() {
    	if(appconfig == null){
    		appconfig = new PropertiesLoader("appconfig.properties");
    	}
        return appconfig;
    }

    /**
     * 获取配置
     */
    public static String getAppConfig(String key) {
        return SysConstants.getAppConfig().getProperty(key);
    }

    /**
     * 获取配置
     */
    public static String getAppConfig(String key,String defaultValue) {
        return SysConstants.getAppConfig().getProperty(key,defaultValue);
    }

    /**
     * 修改配置文件
     * @param key
     * @param value
     */
    public static void modifyAppConfig(String key,String value) {
        String filePath = "appconfig.properties";
        if(appconfig == null){
            appconfig = new PropertiesLoader(filePath);
        }
        appconfig.modifyProperties(filePath,key,value);
    }
    
    /**
     * 配置文件(sensitive.properties)
     */
    public static PropertiesLoader getSensitive() {
    	if(sensitive == null){
    		sensitive = new PropertiesLoader("sensitive.properties");
    	}
        return sensitive;
    }
    
    /**
     * SQL参数过滤配置文件(sqlfilter.properties)
     */
    public static PropertiesLoader getSqlfilter() {
    	if(sqlfilter == null){
    		sqlfilter = new PropertiesLoader("sqlfilter.properties");
    	}
        return sqlfilter;
    }

    /**
     * jdbc type连接参数(默认:"").
     */
    public static String getJdbcType(){
        return SysConstants.getAppConfig().getProperty("jdbc.type","");
    }
    
    /**
     * jdbc url连接参数(默认:"").
     */
    public static String getJdbcUrl(){
    	return SysConstants.getAppConfig().getProperty("jdbc.url","");
    }

    /**
     * jdbc 驱动类
     * @return
     */
    public static String getJdbcDriverClassName(){
        return SysConstants.getAppConfig().getProperty("jdbc.driverClassName","");
    }

    /**
     * jdbc 用户名
     * @return
     */
    public static String getJdbcUserName(){
        return SysConstants.getAppConfig().getProperty("jdbc.username","");
    }

    /**
     * jdbc 密码
     * @return
     */
    public static String getJdbcPassword(){
        return SysConstants.getAppConfig().getProperty("jdbc.password","");
    }


    /**
     * 获取是否是开发模式(默认:false).
     */
    public static Boolean isdevMode(){
    	return getAppConfig().getBoolean("devMode",false);
    }

}
