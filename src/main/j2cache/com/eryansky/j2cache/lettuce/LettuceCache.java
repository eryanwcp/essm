/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eryansky.j2cache.lettuce;

import com.eryansky.j2cache.Cache;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.lock.LockCallback;
import com.eryansky.j2cache.lock.LockCantObtainException;
import com.eryansky.j2cache.lock.LockInsideExecutedException;
import com.eryansky.j2cache.lock.LockRetryFrequency;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.BaseRedisCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import com.eryansky.j2cache.CacheException;
import com.eryansky.j2cache.Level2Cache;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Lettuce 的基类，封装了普通 Redis 连接和集群 Redis 连接的差异
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public abstract class LettuceCache implements Level2Cache {

    private final Logger logger = LoggerFactory.getLogger(LettuceCache.class);

    protected String namespace;
    protected String region;
    protected AbstractRedisClient redisClient;
    protected GenericObjectPool<StatefulConnection<String, byte[]>> pool;

    protected StatefulConnection connect() {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    protected BaseRedisCommands sync(StatefulConnection conn) {
        if(conn instanceof StatefulRedisClusterConnection)
            return ((StatefulRedisClusterConnection)conn).sync();
        else if(conn instanceof StatefulRedisConnection)
            return ((StatefulRedisConnection)conn).sync();
        return null;
    }


    @Override
    public void queuePush(String... values) {
        for(String value:values){
            if(redisClient instanceof RedisClient){
                ((RedisClient)redisClient).connect().sync().rpush(region,value);
            }else if(redisClient instanceof RedisClusterClient){
                ((RedisClusterClient)redisClient).connect().sync().rpush(region,values);
            }
        }
    }

    @Override
    public String queuePop() {
        if(redisClient instanceof RedisClient){
            return ((RedisClient)redisClient).connect().sync().lpop(region);
        }else if(redisClient instanceof RedisClusterClient){
            return ((RedisClusterClient)redisClient).connect().sync().lpop(region);
        }
        return null;
    }

    @Override
    public int queueSize() {
        if(redisClient instanceof RedisClient){
            return ((RedisClient)redisClient).connect().sync().llen(region).intValue();
        }else if(redisClient instanceof RedisClusterClient){
            return ((RedisClusterClient)redisClient).connect().sync().llen(region).intValue();
        }
        return 0;
    }

    @Override
    public Collection<String> queueList() {
        if(redisClient instanceof RedisClient){
            RedisCommands cmd = ((RedisClient)redisClient).connect().sync();
            long length = cmd.llen(region);
            if(length == 0){
                return Collections.emptyList();
            }
            return cmd.lrange(region,0,length-1);
        }else if(redisClient instanceof RedisClusterClient){
            RedisAdvancedClusterCommands cmd = ((RedisClusterClient)redisClient).connect().sync();
            long length = cmd.llen(region);
            if(length == 0){
                return Collections.emptyList();
            }
            return cmd.lrange(region,0,length-1);
        }
        return Collections.emptyList();
    }

    @Override
    public void queueClear() {
        clear();
    }

    @Override
    public <T> T lock(LockRetryFrequency frequency, int timeoutInSecond, long keyExpireSeconds, LockCallback<T> lockCallback) throws LockInsideExecutedException, LockCantObtainException {
        if(redisClient instanceof RedisClient){
            RedisCommands cmd = ((RedisClient)redisClient).connect().sync();
//            long curentTime = System.currentTimeMillis();
//            long expireSecond = curentTime / 1000 + keyExpireSeconds;
//            long expireMillisSecond = curentTime + keyExpireSeconds * 1000;

            int retryCount = Float.valueOf(timeoutInSecond * 1000 / frequency.getRetryInterval()).intValue();

            for (int i = 0; i < retryCount; i++) {
                boolean flag = cmd.setnx(region,String.valueOf(keyExpireSeconds));
                if(flag) {
                    try {
                        cmd.expire(region, keyExpireSeconds);
                        return lockCallback.handleObtainLock();
                    } catch (Exception e) {
                        logger.error(e.getMessage(),e);
                        LockInsideExecutedException ie = new LockInsideExecutedException(e);
                        return lockCallback.handleException(ie);
                    } finally {
                        cmd.del(region);
                    }
                } else {
                    try {
                        Thread.sleep(frequency.getRetryInterval());
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
            return lockCallback.handleNotObtainLock();
        }else if(redisClient instanceof RedisClusterClient){
            RedisAdvancedClusterCommands cmd = ((RedisClusterClient)redisClient).connect().sync();
//            long curentTime = System.currentTimeMillis();
//            long expireSecond = curentTime / 1000 + keyExpireSeconds;
//            long expireMillisSecond = curentTime + keyExpireSeconds * 1000;

            int retryCount = Float.valueOf(timeoutInSecond * 1000 / frequency.getRetryInterval()).intValue();

            for (int i = 0; i < retryCount; i++) {
                boolean flag = cmd.setnx(region,String.valueOf(keyExpireSeconds));
                if(flag) {
                    try {
                        cmd.expire(region, keyExpireSeconds);
                        return lockCallback.handleObtainLock();
                    } catch (Exception e) {
                        logger.error(e.getMessage(),e);
                        LockInsideExecutedException ie = new LockInsideExecutedException(e);
                        return lockCallback.handleException(ie);
                    } finally {
                        cmd.del(region);
                    }
                } else {
                    try {
                        Thread.sleep(frequency.getRetryInterval());
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
            return lockCallback.handleNotObtainLock();
        }
        return null;
    }

}
