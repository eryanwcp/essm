/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package test.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-07
 */
public class ConfigUtils {

    private static Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

    private static Properties config = null;

    public static Properties getConfig() {
        if (config == null) {
            config = new Properties();
            InputStream inStream = null;
            try {
                inStream = ConfigUtils.class.getClassLoader()
                        .getResourceAsStream("cn/com/jfit/proxy/config/config.properties");
                config.load(inStream);
                inStream.close();
            } catch (Exception e) {
                logger.info("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
                e.printStackTrace();
            } finally {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return config;
    }

    public static <T> T getProperty(String key) {
        return (T) getConfig().getProperty(key);
    }

    public static <T> T getProperty(String key, String defaultValue) {
        return (T) getConfig().getProperty(key, defaultValue);
    }

    /**
     * 代理端口
     * @return
     */
    public static int getHttpPort(){
        String value = getProperty("http.port","80");
        return Integer.valueOf(value);
    }

    /**
     * SSL加密端口
     * @return
     */
    public static int getHttpRediectPort(){
        String value = getProperty("http.redirectPort","443");
        return Integer.valueOf(value);
    }



    /**
     * 代理类型 HTTP/HTTPS
     * @return
     */
    public static String getProxyType(){
        return getProperty("proxy.type","HTTP");
    }

    /**
     * 代理端口
     * @return
     */
    public static int getProxyPort(){
        String value = getProperty("proxy.port","80");
        return Integer.valueOf(value);
    }

    /**
     * SSL加密端口
     * @return
     */
    public static int getProxyRediectPort(){
        String value = getProperty("proxy.redirectPort","443");
        return Integer.valueOf(value);
    }


    /**
     * 代理端口
     * @return
     */
    public static int getThreadPoolSize(){
        String value = getProperty("proxy.threadPool.threadCount","5");
        return Integer.valueOf(value);
    }

    /**
     * 代理端口
     * @return
     */
    public static int getTimeout(){
        String value = getProperty("proxy.timeout","5000");
        return Integer.valueOf(value);
    }

}
