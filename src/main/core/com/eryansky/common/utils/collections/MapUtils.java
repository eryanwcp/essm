/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.collections;
import java.util.Map;
import java.util.Map.Entry;

import com.eryansky.common.utils.ObjectUtils;
import com.eryansky.common.utils.StringUtils;

/**
 * map工具类，用于实现一些map的常用操作
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2012-1-9下午2:54:34
 */
public class MapUtils {

    /**
     * 判断map是否为空或大小为0
     * 
     * @param str
     * @return 若map为null或长度为0, 返回true; 否则返回false.
     * 
     * <pre>
     *      isEmpty(null)   =   true;
     *      isEmpty({})     =   true;
     *      isEmpty({1, 2})    =   false;
     * </pre>
     */
    public static boolean isEmpty(Map<?, ?> sourceMap) {
        return (sourceMap == null || sourceMap.size() == 0);
    }

    /**
     * 向map中put key和value对，key必须非null，并且为非空字符串
     * 
     * @param map
     * @param key
     * @param value
     * @return 若put成功，返回true，否则返回false
     *         <ul>
     *         <li>若map为null，返回false</li>
     *         <li>若key为null或空字符串，返回false</li>
     *         <li>{@link java.util.Map#put(Object, Object)} 异常返回false</li>
     *         <li>以上皆不满足，返回true</li>
     *         </ul>
     */
    public static boolean putMapNotEmptyKey(Map<String, String> map, String key, String value) {
        if (map == null || StringUtils.isEmpty(key)) {
            return false;
        }

        try {
            map.put(key, value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 向map中put字符串，该字符串必须非null，并且为非空字符串
     * 
     * @param map
     * @param key
     * @param value
     * @return 若put成功，返回true，否则返回false
     *         <ul>
     *         <li>若map为null，返回false</li>
     *         <li>若key为null，返回false</li>
     *         <li>若value为null或空字符串，返回false</li>
     *         <li>{@link java.util.Map#put(Object, Object)} 异常返回false</li>
     *         <li>以上皆不满足，返回true</li>
     *         </ul>
     */
    public static boolean putMapNotEmptyValue(Map<String, String> map, String key, String value) {
        if (map == null || StringUtils.isEmpty(key)) {
            return false;
        }

        if (!StringUtils.isEmpty(value)) {
            try {
                map.put(key, value);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 向map中put字符串(value)，若字符串为null或者为空字符串，put默认值(defaultValue)
     * 
     * @param map
     * @param key
     * @param value
     * @param defaultValue
     * @return 若put成功，返回true，否则返回false
     *         <ul>
     *         <li>若map为null，返回false</li>
     *         <li>若key为null，返回false</li>
     *         <li>{@link java.util.Map#put(Object, Object)} 异常返回false</li>
     *         <li>若value为null或空字符串，put默认值(defaultValue)，返回true</li>
     *         <li>若value不为null，且不为空字符串，put字符串(value)，返回true</li>
     *         </ul>
     */
    public static boolean putMapNotEmptyValue(Map<String, String> map, String key, String value, String defaultValue) {
        if (map == null || key == null) {
            return false;
        }

        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        try {
            map.put(key, value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据value得到key的值，从头开始匹配
     * <ul>
     * <li>如果map为空，返回null</li>
     * <li>如果map中存在value为查找的value（对于null同样适用），返回key，否则返回null</li>
     * <li></li>
     * <li></li>
     * </ul>
     * 
     * @param <V>
     * @param map map
     * @param value value值
     * @return
     */
    public static <V> String getKeyByValue(Map<String, V> map, V value) {
        if (isEmpty(map)) {
            return null;
        }

        for (Entry<String, V> entry : map.entrySet()) {
            if (ObjectUtils.isEquals(entry.getValue(), value)) {
                return entry.getKey();
            }
        }

        return null;
    }
}