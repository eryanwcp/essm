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


}