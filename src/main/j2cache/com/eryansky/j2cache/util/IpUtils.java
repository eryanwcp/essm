/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.j2cache.util;

import com.google.common.net.InetAddresses;

import java.net.InetAddress;

/**
 *
 * IP地址.
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2018-09-13
 */
public class IpUtils {

    private static final String LOCAL_IP = getActivityLocalIp();

    private IpUtils() {
    }

    public static String getActivityLocalIp(){
        InetAddress inetAddress;
        String local = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            local = InetAddresses.toAddrString(inetAddress);
        } catch (Exception e) {
        }
        return "127.0.0.1".equals(local) ? "":local;
    }

    /**
     * 从InetAddress转化到int, 传输和存储时, 用int代表InetAddress是最小的开销.
     *
     * InetAddress可以是IPV4或IPV6，都会转成IPV4.
     *
     * @see com.google.common.net.InetAddresses#coerceToInteger(InetAddress)
     */
    public static int toInt(InetAddress address) {
        return InetAddresses.coerceToInteger(address);
    }

    /**
     * InetAddress转换为String.
     *
     * InetAddress可以是IPV4或IPV6. 其中IPV4直接调用getHostAddress()
     *
     * @see com.google.common.net.InetAddresses#toAddrString(InetAddress)
     */
    public static String toIpString(InetAddress address) {
        return InetAddresses.toAddrString(address);
    }


}