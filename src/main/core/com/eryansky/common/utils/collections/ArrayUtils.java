/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.collections;

import com.eryansky.common.utils.ObjectUtils;

/**
 * array工具类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2012-1-9下午2:55:08
 */
public class ArrayUtils {

    /**
     * 判断array是否为空或大小为0
     * 
     * @param sourceArray
     * @return 若array为null或长度为0, 返回true; 否则返回false.
     * 
     * <pre>
     *      isEmpty(null)   =   true;
     *      isEmpty({})     =   true;
     *      isEmpty({1})    =   false;
     * </pre>
     */
    public static <V> boolean isEmpty(V[] sourceArray) {
        return (sourceArray == null || sourceArray.length == 0);
    }

    /**
     * 得到array中某个元素（第一次发现）的前一个元素
     * <ul>
     * <li>若数组为空，返回defaultValue</li>
     * <li>若数组中未找到value，返回defaultValue</li>
     * <li>若找到了value并且不为第一个元素，返回该元素的前一个元素</li>
     * <li>若找到了value并且为第一个元素，isCircle为true时，返回数组最后一个元素；isCircle为false时，返回defaultValue</li>
     * </ul>
     * 
     * @param <V>
     * @param sourceArray 源array
     * @param value 待查找值，若value为null同样适用，会查找第一个为null的值
     * @param defaultValue 默认返回值
     * @param isCircle 是否是圆
     * @return
     */
    public static <V> V getLast(V[] sourceArray, V value, V defaultValue, boolean isCircle) {
        if (isEmpty(sourceArray)) {
            return defaultValue;
        }

        int currentPosition = -1;
        for (int i = 0; i < sourceArray.length; i++) {
            if (ObjectUtils.isEquals(value, sourceArray[i])) {
                currentPosition = i;
                break;
            }
        }
        if (currentPosition == -1) {
            return defaultValue;
        }

        if (currentPosition == 0) {
            return isCircle ? sourceArray[sourceArray.length - 1] : defaultValue;
        }
        return sourceArray[currentPosition - 1];
    }

    /**
     * 得到array中某个元素（第一次发现）的后一个元素
     * <ul>
     * <li>若数组为空，返回defaultValue</li>
     * <li>若数组中未找到value，返回defaultValue</li>
     * <li>若找到了value并且不为最后一个元素，返回该元素的下一个元素</li>
     * <li>若找到了value并且为最后一个元素，isCircle为true时，返回数组第一个元素；isCircle为false时，返回defaultValue</li>
     * </ul>
     * 
     * @param <V>
     * @param sourceArray 源array
     * @param value 待查找值，若value为null同样适用，会查找第一个为null的值
     * @param defaultValue 默认返回值
     * @param isCircle 是否是圆
     * @return
     */
    public static <V> V getNext(V[] sourceArray, V value, V defaultValue, boolean isCircle) {
        if (isEmpty(sourceArray)) {
            return defaultValue;
        }

        int currentPosition = -1;
        for (int i = 0; i < sourceArray.length; i++) {
            if (ObjectUtils.isEquals(value, sourceArray[i])) {
                currentPosition = i;
                break;
            }
        }
        if (currentPosition == -1) {
            return defaultValue;
        }

        if (currentPosition == sourceArray.length - 1) {
            return isCircle ? sourceArray[0] : defaultValue;
        }
        return sourceArray[currentPosition + 1];
    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getLast(Object[], Object, Object, boolean)} defaultValue为null
     */
    public static <V> V getLast(V[] sourceArray, V value, boolean isCircle) {
        return getLast(sourceArray, value, null, isCircle);
    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getNext(Object[], Object, Object, boolean)} defaultValue为null
     */
    public static <V> V getNext(V[] sourceArray, V value, boolean isCircle) {
        return getNext(sourceArray, value, null, isCircle);
    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getLast(Object[], Object, Object, boolean)} Object为Long
     */
    public static long getLast(long[] sourceArray, long value, Long defaultValue, boolean isCircle) {
        Long[] array = ObjectUtils.transformLongArray(sourceArray);
        return getLast(array, value, defaultValue, isCircle);

    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getNext(Object[], Object, Object, boolean)} Object为Long
     */
    public static long getNext(long[] sourceArray, long value, Long defaultValue, boolean isCircle) {
        Long[] array = ObjectUtils.transformLongArray(sourceArray);
        return getNext(array, value, defaultValue, isCircle);
    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getLast(long[], long, Long, boolean)} defaultValue为null
     */
    public static long getLast(long[] sourceArray, long value, boolean isCircle) {
        return getLast(sourceArray, value, null, isCircle);

    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getNext(long[], long, Long, boolean)} defaultValue为null
     */
    public static long getNext(long[] sourceArray, long value, boolean isCircle) {
        return getNext(sourceArray, value, null, isCircle);
    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getLast(Object[], Object, Object, boolean)} Object为Integer
     */
    public static int getLast(int[] sourceArray, int value, Integer defaultValue, boolean isCircle) {
        Integer[] array = ObjectUtils.transformIntArray(sourceArray);
        return getLast(array, value, defaultValue, isCircle);

    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getNext(Object[], Object, Object, boolean)} Object为Integer
     */
    public static int getNext(int[] sourceArray, int value, Integer defaultValue, boolean isCircle) {
        Integer[] array = ObjectUtils.transformIntArray(sourceArray);
        return getNext(array, value, defaultValue, isCircle);
    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getLast(int[], int, Integer, boolean)} defaultValue为null
     */
    public static int getLast(int[] sourceArray, int value, boolean isCircle) {
        return getLast(sourceArray, value, null, isCircle);

    }

    /**
     * 参考{@link com.eryansky.common.utils.collections.ArrayUtils#getNext(int[], int, Integer, boolean)} defaultValue为null
     */
    public static int getNext(int[] sourceArray, int value, boolean isCircle) {
        return getNext(sourceArray, value, null, isCircle);
    }
}