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
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.BaseRedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import com.eryansky.j2cache.CacheException;
import com.eryansky.j2cache.Level2Cache;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * Lettuce 的基类，封装了普通 Redis 连接和集群 Redis 连接的差异
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public abstract class LettuceCache implements Level2Cache {

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
    public void push(String... values) {
        for(String value:values){
            if(redisClient instanceof RedisClient){
                ((RedisClient)redisClient).connect().sync().rpush(Cache.getRegionName(namespace,region),value);
            }else if(redisClient instanceof RedisClusterClient){
                ((RedisClusterClient)redisClient).connect().sync().rpush(Cache.getRegionName(namespace,region),values);
            }
        }
    }

    @Override
    public String pop() {
        if(redisClient instanceof RedisClient){
            return ((RedisClient)redisClient).connect().sync().lpop(Cache.getRegionName(namespace,region));
        }else if(redisClient instanceof RedisClusterClient){
            return ((RedisClusterClient)redisClient).connect().sync().lpop(Cache.getRegionName(namespace,region));
        }
        return null;
    }

    @Override
    public void clearQueue() {
        clear();
    }
}
