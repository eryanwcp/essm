/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.utils.j2cache;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.ThreadUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.J2Cache;
import com.eryansky.j2cache.lock.DefaultLockCallback;
import com.eryansky.j2cache.lock.LockRetryFrequency;
import com.eryansky.j2cache.util.FSTSerializer;
import com.eryansky.j2cache.util.FstJSONSerializer;
import com.eryansky.j2cache.util.FstSnappySerializer;
import com.eryansky.j2cache.util.JavaSerializer;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.*;
import com.eryansky.utils.CacheConstants;
import com.eryansky.utils.CacheUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
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
public class CacheTest {

    private static Logger logger = LoggerFactory.getLogger(CacheTest.class);

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;
    @Autowired
    private CacheChannel cacheChannel;


    @Test
    public void cache() throws Exception{
        List<TreeNode> treeNodes = resourceService.findTreeNodeResourcesWithPermissions("1");
        System.out.println(JsonMapper.toJsonString(treeNodes));
        System.out.println((String)CacheUtils.get(CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,"1"));
        System.out.println(CacheUtils.keys(CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE));

    }

    @Test
    public void cache2() throws Exception{
        User user = userService.get("1");
        for(int i=0;i<10000;i++){
            String sessionId = Identities.uuid2() + "."+i;
            SecurityUtils.putUserToSession(sessionId,user);
        }
    }


    @Test
    public void cache3() throws Exception{
        User user = userService.get("1");

        Date d1 = Calendar.getInstance().getTime();
        FSTSerializer serializer =new FSTSerializer();
        byte[] r = serializer.serialize(user);
        System.out.println(r.length);
        Date d2 = Calendar.getInstance().getTime();
        System.out.println(d2.getTime() - d1.getTime());
    }


    @Test
    public void cache4() throws Exception{
        User user = userService.get("1");

        Date d1 = Calendar.getInstance().getTime();
        FstSnappySerializer serializer =new FstSnappySerializer();
        byte[] r = serializer.serialize(user);
        System.out.println(r.length);
        Date d2 = Calendar.getInstance().getTime();
        System.out.println(d2.getTime() - d1.getTime());
    }

    @Test
    public void cache5() throws Exception{
        User user = userService.get("1");

        Date d1 = Calendar.getInstance().getTime();
        FstJSONSerializer serializer =new FstJSONSerializer(null);
        byte[] r = serializer.serialize(user);
        System.out.println(r.length);
        Date d2 = Calendar.getInstance().getTime();
        System.out.println(d2.getTime() - d1.getTime());
    }

    @Test
    public void cache6() throws Exception{
        User user = userService.get("1");

        Date d1 = Calendar.getInstance().getTime();
        JavaSerializer serializer =new JavaSerializer();
        byte[] r = serializer.serialize(user);
        System.out.println(r.length);
        Date d2 = Calendar.getInstance().getTime();
        System.out.println(d2.getTime() - d1.getTime());
    }


    @Test
    public void cache10() throws Exception{
        User user = userService.get("1");

        ExecutorService executorService = Executors.newScheduledThreadPool(8);
        String region = "cache1";
        CacheChannel channel = J2Cache.getChannel();

        channel.clear(region);
        Date d1 = Calendar.getInstance().getTime();
        for(int i=0;i<10000;i++){
            int finalI = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    channel.set(region,"i"+ finalI, user,false);
                }
            });

        }

        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                Date d2 = Calendar.getInstance().getTime();
                System.out.println("执行完毕！");
                System.out.println(d2.getTime() - d1.getTime());
                channel.close();
                break;
            }
            ThreadUtils.sleep(200);
        }
    }


    @Test
    public void lock3() throws Exception{

        ExecutorService executorService = Executors.newScheduledThreadPool(4);
        String region = "cache1";
        CacheChannel channel = J2Cache.getChannel();

        Date d1 = Calendar.getInstance().getTime();
        final int[] m = {0};
        String requesId = Thread.currentThread().getName();
        System.out.println(requesId);
        for(int i=0;i<1000;i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    boolean flag = channel.lock(region, LockRetryFrequency.VERY_QUICK, 3, 20, new DefaultLockCallback<Boolean>(null,false) {
                        @Override
                        public Boolean handleObtainLock() {
                            m[0]++;
                            System.out.println(m[0]);
                            return true;
                        }
                    });
                    if(!flag){
                        System.out.println(flag);
                    }
                }
            });

        }
        ThreadUtils.sleep(600000);
//        executorService.shutdown();
//        while (true) {
//            if (executorService.isTerminated()) {
//                Date d2 = Calendar.getInstance().getTime();
//                System.out.println("执行完毕！");
//                System.out.println(d2.getTime() - d1.getTime());
//                channel.close();
//                break;
//            }
//            ThreadUtils.sleep(200);
//        }
    }

    @Test
    public void lock0() throws Exception{

        ExecutorService executorService = Executors.newScheduledThreadPool(8);
        String region = "cache1";
        CacheChannel channel = J2Cache.getChannel();

        Date d1 = Calendar.getInstance().getTime();
        final int[] m = {0};
        String requesId = Thread.currentThread().getName();
        System.out.println(requesId);
        for(int i=0;i<10000;i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    m[0]++;
                    System.out.println(m[0]);
                }
            });

        }
        ThreadUtils.sleep(600000);
    }

    private void s(){
        synchronized (CacheTest.this){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("S");
        }
    }

    private synchronized void ss(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("S");
    }
    private void s2(){
        System.out.println("S2");
    }
    @Test
    public void lock2() throws Exception{

        ExecutorService executorService = Executors.newScheduledThreadPool(4);
        String region = "cache1";
        CacheChannel channel = J2Cache.getChannel();
        Date d1 = Calendar.getInstance().getTime();
        final int[] m = {0};
        for(int i=0;i<5;i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    m[0]++;
                    s();
                }
            });

        }

        for(int j=0;j<10;j++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    s2();
                }
            });

        }

        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                Date d2 = Calendar.getInstance().getTime();
                System.out.println("执行完毕！");
                System.out.println(d2.getTime() - d1.getTime());
                channel.close();
                break;
            }
            ThreadUtils.sleep(200);
        }
    }

    @Test
    public void cache13() throws Exception{
        for(int i=0;i<10;i++){
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String key = "essm_l2";
                    p13(key, finalI);
                }
            }).start();
        }
        ThreadUtils.sleep(300*1000);
    }
    private void p13(String key,int index){
        Result result = CacheUtils.getCacheChannel().lock(key, 20, 30, new DefaultLockCallback<Result>(null,null) {
            @Override
            public Result handleObtainLock() {
                try{

                    return s13(index);
                }catch (ServiceException e){
                    logger.error(e.getMessage(),e);
                    return Result.warnResult().setMsg(e.getMessage());
                }
            }
        });
        System.out.println(result != null ? result.toString():index+" null");
    }

    private Result s13(int index){
        System.out.println(index);
        ThreadUtils.sleep(60*1000);
        return Result.successResult();
    }

}
