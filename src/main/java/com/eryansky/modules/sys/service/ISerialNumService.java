package com.eryansky.modules.sys.service;

import com.eryansky.modules.sys.mapper.SystemSerialNumber;

/**
 * 序列号service接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-07-14
 */
public interface ISerialNumService {

    SystemSerialNumber find(SystemSerialNumber systemSerialNumber);

    String generateSerialNumberByModelCode(String moduleCode);

    /**
     * 设置最小值
     * @param value 最小值，要求：大于等于零
     * @return      流水号生成器实例
     */
    ISerialNumService setMin(int value);

    /**
     * 设置最大值
     * @param value 最大值，要求：小于等于Long.MAX_VALUE ( 9223372036854775807 )
     * @return      流水号生成器实例
     */
    ISerialNumService setMax(long value);

    /**
     * 设置预生成流水号数量
     * @param count 预生成数量
     * @return      流水号生成器实例
     */
    ISerialNumService setPrepare(int count);
}