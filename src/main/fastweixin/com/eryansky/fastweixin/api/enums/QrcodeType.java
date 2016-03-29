package com.eryansky.fastweixin.api.enums;

/**
 * 二维码接口状态枚举
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public enum QrcodeType {

    /**
     * 临时二维码
     */
    QR_SCENE,

    /**
     * 永久二维码
     */
    QR_LIMIT_SCENE,

    /**
     * 永久的字符串参数值
     */
    QR_LIMIT_STR_SCENE
}
