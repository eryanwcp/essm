/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.utils;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.mapper.JsonMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-10-19 
 */
public class AppUtils {

    private AppUtils(){

    }

    public static String toJson(Object object){
        String json = JsonMapper.getInstance().toJson(object);
        return json;
    }

    /**
     * url and para separator *
     */
    public static final String URL_AND_PARA_SEPARATOR = "?";
    /**
     * parameters separator *
     */
    public static final String PARAMETERS_SEPARATOR = "&";
    /**
     * paths separator *
     */
    public static final String PATHS_SEPARATOR = "/";
    /**
     * equal sign *
     */
    public static final String EQUAL_SIGN = "=";

    /**
     * join paras
     *
     * @param parasMap paras map, key is para name, value is para value
     * @return join key and value with {@link #EQUAL_SIGN}, join keys with {@link #PARAMETERS_SEPARATOR}
     */
    public static String joinParas(Map<String, String> parasMap) {
        if (parasMap == null || parasMap.size() == 0) {
            return null;
        }

        StringBuilder paras = new StringBuilder();
        Iterator<Map.Entry<String, String>> ite = parasMap.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) ite.next();
            paras.append(entry.getKey()).append(EQUAL_SIGN).append(entry.getValue());
            if (ite.hasNext()) {
                paras.append(PARAMETERS_SEPARATOR);
            }
        }
        return paras.toString();
    }

    /**
     * join paras with encoded value
     *
     * @param parasMap
     * @return
     * @see #joinParas(Map)
     * @see StringUtils#utf8Encode(String)
     */
    public static String joinParasWithEncodedValue(Map<String, Object> parasMap) {
        StringBuilder paras = new StringBuilder("");
        if (parasMap != null && parasMap.size() > 0) {
            Iterator<Map.Entry<String, Object>> ite = parasMap.entrySet().iterator();
            try {
                while (ite.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) ite.next();
                    paras.append(entry.getKey()).append(EQUAL_SIGN).append(StringUtils.utf8Encode((String) entry.getValue()));
                    if (ite.hasNext()) {
                        paras.append(PARAMETERS_SEPARATOR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return paras.toString();
    }

    /**
     * append a key and value pair to url
     *
     * @param url
     * @param paraKey
     * @param paraValue
     * @return
     */
    public static String appendParaToUrl(String url, String paraKey, String paraValue) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);
        if (!url.contains(URL_AND_PARA_SEPARATOR)) {
            sb.append(URL_AND_PARA_SEPARATOR);
        } else {
            sb.append(PARAMETERS_SEPARATOR);
        }
        return sb.append(paraKey).append(EQUAL_SIGN).append(paraValue).toString();
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String truncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim().toLowerCase();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param url url地址
     * @return url请求参数部分
     */
    public static Map<String, String> urlRequest(String url) {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit = null;

        String strUrlParam = truncateUrlPage(url);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (StringUtils.isNotBlank(arrSplitEqual[0])) {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }


    //         \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),字符串在编译时会被转码一次,所以是 "\\b"
    // \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)
    static String androidReg = "\\bandroid|Nexus\\b";
    static String iosReg = "ip(hone|od|ad)";

    static Pattern androidPat = Pattern.compile(androidReg, Pattern.CASE_INSENSITIVE);
    static Pattern iosPat = Pattern.compile(iosReg, Pattern.CASE_INSENSITIVE);

    /**
     *
     * @param userAgent
     * @return
     */
    public static boolean likeAndroid(String userAgent){
        if(null == userAgent){
            userAgent = "";
        }
        // 匹配
        Matcher matcherAndroid = androidPat.matcher(userAgent);
        if(matcherAndroid.find()){
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param userAgent
     * @return
     */
    public static boolean likeIOS(String userAgent){
        if(null == userAgent){
            userAgent = "";
        }
        // 匹配
        Matcher matcherIOS = iosPat.matcher(userAgent);
        if(matcherIOS.find()){
            return true;
        } else {
            return false;
        }
    }
}
