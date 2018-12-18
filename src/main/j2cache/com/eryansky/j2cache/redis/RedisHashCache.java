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
import redis.clients.jedis.BinaryJedisCommands;

import java.io.UnsupportedEncodingException;
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
        this.region = region;
        this.regionBytes = Cache.getRegionName(namespace,region).getBytes();
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
            byte[][] bytes = keys.stream().map(k -> k.getBytes()).toArray(byte[][]::new);
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
            byte[][] bytes = Arrays.stream(keys).map(k -> k.getBytes()).toArray(byte[][]::new);
            client.get().hdel(regionBytes, bytes);
        } finally {
            client.release();
        }
    }

    @Override
    public Collection<String> keys() {
        try {
            return client.get().hkeys(regionBytes).stream().map(bs -> new String(bs)).collect(Collectors.toList());
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

    private byte[] _key(String key) {
        try {
            return (this.region + ":" + key).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return (this.region + ":" + key).getBytes();
        }
    }

    @Override
    public void queuePush(String... values) {
        try {
            for (String value : values) {
                BinaryJedisCommands cmd = client.get();
                cmd.rpush(_key(this.region),value.getBytes());
            }
        } finally {
            client.release();
        }
    }

    @Override
    public String queuePop() {
        try {
            BinaryJedisCommands cmd = client.get();
            byte[] data = cmd.lpop(_key(this.region));
            return data == null ? null:new String(data);
        } finally {
            client.release();
        }
    }

    @Override
    public int queueSize() {
        BinaryJedisCommands cmd = client.get();
        return cmd.llen(_key(this.region)).intValue();
    }

    @Override
    public Collection<String> queueList() {
        BinaryJedisCommands cmd = client.get();
        byte[] keys = _key(this.region);
        long length = cmd.llen(keys);
        if(length == 0){
            return Collections.emptyList();
        }
        List<byte[]> values =  cmd.lrange(keys,0,length-1);
        List<String> valueStrs =  new ArrayList<>(values.size());
        values.forEach(e ->valueStrs.add(new String(e)));
        return valueStrs;
    }

    @Override
    public void queueClear() {
        clear();
    }

}
