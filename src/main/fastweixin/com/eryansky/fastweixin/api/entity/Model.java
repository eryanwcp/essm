package com.eryansky.fastweixin.api.entity;

import java.io.Serializable;

/**
 * 模型接口，所有需要传输的对象都需要实现，提供转json的方法
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public interface Model extends Serializable {

    /**
     * 将model转成json字符串
     *
     * @return json字符串
     */
    String toJsonString();
}
