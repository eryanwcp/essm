/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.utils;

import com.eryansky.common.model.TreeNode;
import com.eryansky.common.utils.ThreadUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.modules.sys.mapper.Resource;
import com.eryansky.modules.sys.service.*;
import com.eryansky.modules.sys.utils.SystemSerialNumberUtils;
import com.eryansky.utils.CacheConstants;
import com.eryansky.utils.CacheUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2018-05-11 20:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
        "classpath:applicationContext-mybatis.xml",
        "classpath:applicationContext-quartz.xml",
        "classpath:applicationContext-j2cache.xml" })
public class SystemSerialNumberServiceTest {

    private static Logger logger = LoggerFactory.getLogger(SystemSerialNumberServiceTest.class);

    @Test
    public void test() throws Exception{
        System.out.println(SystemSerialNumberUtils.generateSerialNumberByModelCode("test"));
        System.out.println(SystemSerialNumberUtils.generateSerialNumberByModelCode("test"));
        System.out.println(SystemSerialNumberUtils.generateSerialNumberByModelCode("test"));
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Test
    public void test2() throws Exception{
        for(int i=0;i<100;i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(SystemSerialNumberUtils.generateSerialNumberByModelCode("test"));
                }
            });
        }
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                System.out.println("执行完毕！");
                break;
            }
            ThreadUtils.sleep(200);
        }
    }


}
