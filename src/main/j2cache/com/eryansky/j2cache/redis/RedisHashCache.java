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
package com.eryansky.j2cache.redis;

import com.eryansky.j2cache.Cache;
import com.eryansky.j2cache.Level2Cache;
import com.eryansky.j2cache.lock.LockCallback;
import com.eryansky.j2cache.lock.LockCantObtainException;
import com.eryansky.j2cache.lock.LockInsideExecutedException;
import com.eryansky.j2cache.lock.LockRetryFrequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedisCommands;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis 缓存操作封装，基于 Hashs 实现多个 Region 的缓存（
 * @author wendal
 * @author Winter Lau(javayou@gmail.com)
 *
 * @deprecated  hash 存储模式并适合缓存场景，首先无法单独对 key 设置 expire ，此外在大量的数据情况下，性能更差。
 */
@Deprecated
public class RedisHashCache implements Level2Cache {

    private final static Logger log = LoggerFactory.getLogger(RedisHashCache.class);

    private String namespace;
    private String region;
    private byte[] regionBytes;
    private RedisClient client;

    /**
     * 缓存构造
     * @param namespace 命名空间，用于在多个实例中避免 key 的重叠
     * @param region 缓存区域的名称
     * @param client 缓存客户端接口
     */
    public RedisHashCache(String namespace, String region, RedisClient client) {
        if (region == null || region.trim().isEmpty())
            region = "_"; // 缺省region

        this.client = client;
        this.namespace = namespace;
        this.region = Cache.getRegionName(namespace,region);
        this.regionBytes = region.getBytes();
    }


    @Override
    public byte[] getBytes(String key) {
        try {
            return client.get().hget(regionBytes, key.getBytes());
        } finally {
            client.release();
        }
    }

    @Override
    public List<byte[]> getBytes(Collection<String> keys) {
        try {
            byte[][] bytes = keys.stream().map(String::getBytes).toArray(byte[][]::new);
            return client.get().hmget(regionBytes, bytes);
        } finally {
            client.release();
        }
    }

    @Override
    public void setBytes(String key, byte[] bytes) {
        try {
            client.get().hset(regionBytes, key.getBytes(), bytes);
        } finally {
            client.release();
        }
    }

    @Override
    public void setBytes(Map<String,byte[]> bytes) {
        try {
            Map<byte[], byte[]> data = new HashMap<>();
            bytes.forEach((k,v) -> data.put(k.getBytes(), v));
            client.get().hmset(regionBytes, data);
        } finally {
            client.release();
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return client.get().hexists(regionBytes, key.getBytes());
        } finally {
            client.release();
        }
    }

    @Override
    public void evict(String...keys) {
        if (keys == null || keys.length == 0)
            return;
        try {
            byte[][] bytes = Arrays.stream(keys).map(String::getBytes).toArray(byte[][]::new);
            client.get().hdel(regionBytes, bytes);
        } finally {
            client.release();
        }
    }

    @Override
    public Collection<String> keys() {
        try {
            return client.get().hkeys(regionBytes).stream().map(String::new).collect(Collectors.toList());
//            return client.get().hkeys(regionBytes).stream().map(k->new String(k).substring(this.region.length()+1)).collect(Collectors.toList());
        } finally {
            client.release();
        }
    }

    @Override
    public void clear() {
        try {
            client.get().del(regionBytes);
        } finally {
            client.release();
        }
    }


    @Override
    public Long ttl(String key) {
        try {
            return client.get().ttl(key.getBytes());
        } finally {
            client.release();
        }
    }

    @Override
    public void queuePush(String... values) {
        try {
            for (String value : values) {
                BinaryJedisCommands cmd = client.get();
                cmd.rpush(regionBytes,value.getBytes());
            }
        } finally {
            client.release();
        }
    }

    @Override
    public String queuePop() {
        try {
            BinaryJedisCommands cmd = client.get();
            byte[] data = cmd.lpop(regionBytes);
            return data == null ? null:new String(data);
        } finally {
            client.release();
        }
    }

    @Override
    public int queueSize() {
        try {
            BinaryJedisCommands cmd = client.get();
            return cmd.llen(regionBytes).intValue();
        } finally {
            client.release();
        }
    }

    @Override
    public Collection<String> queueList() {
        try {
            BinaryJedisCommands cmd = client.get();
            long length = cmd.llen(regionBytes);
            if(length == 0){
                return Collections.emptyList();
            }
            List<byte[]> values =  cmd.lrange(regionBytes,0,length-1);
            return values.stream().map(String::new).collect(Collectors.toList());
        } finally {
            client.release();
        }
    }

    @Override
    public void queueClear() {
        clear();
    }

    @Override
    public <T> T lock(LockRetryFrequency frequency, int timeoutInSecond, long keyExpireSeconds, LockCallback<T> lockCallback) throws LockInsideExecutedException, LockCantObtainException {
        long curentTime = System.currentTimeMillis();
        /*
         * 设置加锁过期时间
         */
        long expireSecond = curentTime / 1000L + keyExpireSeconds;
        /*
         * 作为值存入锁中(记录这把锁持有最终时限)
         */
        long expireMillisSecond = curentTime + keyExpireSeconds * 1000L;

        int retryCount = Float.valueOf(timeoutInSecond * 1000 / frequency.getRetryInterval()).intValue();

        try {
            BinaryJedisCommands cmd = client.get();
            for (int i = 0; i < retryCount; i++) {
                Long result = cmd.setnx(regionBytes,String.valueOf(expireMillisSecond).getBytes());
                boolean flag = 1L == result;
                if(flag) {
                    try {
                        cmd.expireAt(regionBytes,expireSecond);
                        return lockCallback.handleObtainLock();
                    } catch (Exception e) {
                        log.error(e.getMessage(),e);
                        LockInsideExecutedException ie = new LockInsideExecutedException(e);
                        return lockCallback.handleException(ie);
                    } finally {
                        cmd.del(regionBytes);
                    }
                } else {
                    try {
                        Thread.sleep(frequency.getRetryInterval());
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(),e);
                    }
                }
            }
            return lockCallback.handleNotObtainLock();
        } finally {
            client.release();
        }
    }


}
