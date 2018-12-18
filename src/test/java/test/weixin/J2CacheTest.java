/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.weixin;

import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.J2Cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-07 20:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
        "classpath:applicationContext-mybatis.xml",
        "classpath:applicationContext-quartz.xml",
        "classpath:applicationContext-j2cache.xml" })
public class J2CacheTest {

    private static Logger logger = LoggerFactory.getLogger(J2CacheTest.class);

    @Autowired
    private CacheChannel cacheChannel = J2Cache.getChannel();

    @Test
    public void test() {
        String region = "QUEUE_01";
        String region2 = "QUEUE_02";
        cacheChannel.queuePush(region,"11");
        cacheChannel.queuePush(region,"22");
        cacheChannel.queuePush(region,"33");
        cacheChannel.queuePush(region2,"123");
        System.out.println(cacheChannel.queuePop(region));
        System.out.println(cacheChannel.queueList(region));
        System.out.println(cacheChannel.queuePop(region));
//        cacheChannel.queueClear(region);
        System.out.println(cacheChannel.queuePop(region));
        System.out.println(cacheChannel.queuePop(region));
        System.out.println(cacheChannel.queuePop(region2));
        System.out.println(cacheChannel.queueList(region));
    }


}
