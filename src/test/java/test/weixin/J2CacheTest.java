/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.weixin;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.fastweixin.company.api.QYMenuAPI;
import com.eryansky.fastweixin.company.api.QYMessageAPI;
import com.eryansky.fastweixin.company.api.QYUserAPI;
import com.eryansky.fastweixin.company.api.config.QYAPIConfig;
import com.eryansky.fastweixin.company.api.entity.QYMenu;
import com.eryansky.fastweixin.company.api.entity.QYMenuButton;
import com.eryansky.fastweixin.company.api.enums.QYMenuType;
import com.eryansky.fastweixin.company.api.enums.QYResultType;
import com.eryansky.fastweixin.company.api.response.GetQYSendMessageResponse;
import com.eryansky.fastweixin.company.api.response.GetQYUserInfoResponse;
import com.eryansky.fastweixin.company.message.QYTextMsg;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.J2Cache;
import com.eryansky.modules.weixin.utils.WeixinUtils;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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
        cacheChannel.push(region,"1");
        cacheChannel.push(region,"2");
        cacheChannel.push(region,"3");
        System.out.println(cacheChannel.pop(region));
        System.out.println(cacheChannel.pop(region));
//        cacheChannel.clearQueue(region);
        System.out.println(cacheChannel.pop(region));
        System.out.println(cacheChannel.pop(region));
    }


}
