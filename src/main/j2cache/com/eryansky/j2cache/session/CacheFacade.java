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
package com.eryansky.j2cache.session;

import com.eryansky.j2cache.util.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存封装入口
 * @author Winter Lau(javayou@gmail.com)
 */
public class CacheFacade extends JedisPubSub implements Closeable, AutoCloseable, CacheExpiredListener {

    private final Logger logger = LoggerFactory.getLogger(CacheFacade.class);

    private static final Map<String, Object> _g_keyLocks = new ConcurrentHashMap<>();

    private CaffeineCache cache1;
    private RedisCache cache2;

    private RedisClient redisClient;
    private String pubsub_channel;

    private boolean discardNonSerializable;

    public CacheFacade(int maxSizeInMemory, int maxAge, Properties redisConf, boolean discardNonSerializable) {
        this.discardNonSerializable = discardNonSerializable;
        this.cache1 = new CaffeineCache(maxSizeInMemory, maxAge, this);

        logger.info("J2Cache Session L1 CacheProvider {}.",this.cache1.getClass().getName());

        String enabled = redisConf.getProperty("enabled");
        if(!"true".equalsIgnoreCase(enabled)){
            logger.info("J2Cache Session L2/redis not enabled.");
            return;
        }
        JedisPoolConfig poolConfig = RedisUtils.newPoolConfig(redisConf, null);

        String hosts = redisConf.getProperty("hosts");
        hosts = hosts != null ? hosts:"127.0.0.1:6379";
        String mode = redisConf.getProperty("mode");
        mode = mode != null ? mode:"single";
        String clusterName = redisConf.getProperty("cluster_name");
        clusterName = clusterName != null ? clusterName:"j2cache-session";
        String password = redisConf.getProperty("password");
        String _database = redisConf.getProperty("database");
        int database = _database != null ? Integer.parseInt(_database):0;

        this.pubsub_channel = redisConf.getProperty("channel");
        this.pubsub_channel = this.pubsub_channel != null ? this.pubsub_channel:"j2cache-session";

        long ct = System.currentTimeMillis();

        this.redisClient = new RedisClient.Builder()
                .mode(mode)
                .hosts(hosts)
                .password(password)
                .cluster(clusterName)
                .database(database)
                .poolConfig(poolConfig).newClient();

        this.cache2 = new RedisCache(null, redisClient);
        logger.info("J2Cache Session L2 CacheProvider {}.",this.cache2.getClass().getName());

        this.publish(Command.join());

        Thread subscribeThread = new Thread(()-> {
            //当 Redis 重启会导致订阅线程断开连接，需要进行重连
            int tryCount = 0;
            while(true) {
                try {
                    Jedis jedis = (Jedis)redisClient.get();
                    jedis.subscribe(this, pubsub_channel);
                    logger.info("Disconnect to redis channel: " + pubsub_channel);
                    break;
                } catch (JedisConnectionException e) {
                    if (tryCount++ < 3) {
                        // 失败先快速释放再重连
                        redisClient.release();
                        continue;
                    }
                    tryCount = 0;
                    logger.error("Failed connect to redis, reconnect it.", e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie){
                        break;
                    }
                }
            }
        }, "RedisSessionSubscribeThread");

        subscribeThread.setDaemon(true);
        subscribeThread.start();

        logger.info("Connected to redis channel:" + pubsub_channel + ", time " + (System.currentTimeMillis()-ct) + " ms.");

    }

    /**
     * 发送广播消息
     * @param cmd 待发布的消息
     */
    public void publish(Command cmd) {
        if(this.cache2 == null){
            return;
        }
        try  {
            BinaryJedisCommands redis = redisClient.get();
            if(redis instanceof Jedis) {
                Jedis jedis = (Jedis) redis;
                jedis.publish(pubsub_channel, cmd.toString());
            }
            else if(redis instanceof JedisCluster) {
                JedisCluster jedis = (JedisCluster) redis;
                jedis.publish(pubsub_channel, cmd.toString());
            }
        } finally {
            redisClient.release();
        }
    }

    /**
     * 当接收到订阅频道获得的消息时触发此方法
     * @param channel 频道名称
     * @param message 消息体
     */
    @Override
    public void onMessage(String channel, String message) {
        if(this.cache2 == null){
            return;
        }
        Command cmd = Command.parse(message);

        try {
            if (cmd == null || cmd.isLocal())
                return;

            switch (cmd.getOperator()) {
                case Command.OPT_JOIN:
                    logger.info("Server-"+cmd.getSrc() + " joined !");
                    break;
                case Command.OPT_DELETE_SESSION:
                    cache1.evict(cmd.getSession());
                    logger.debug("Received session clear command, session=" + cmd.getSession());
                    break;
                case Command.OPT_QUIT:
                    logger.info("Server-"+cmd.getSrc() + " quit !");
                    break;
                default:
                    logger.warn("Unknown command type = " + cmd.getOperator());
            }
        } catch (Exception e) {
            logger.error("Failed to handle received command", e);
        }
    }

    @Override
    public void notifyElementExpired(String session_id) {
        if(this.cache2 == null){
            return;
        }
        this.publish(new Command(Command.OPT_DELETE_SESSION, session_id, null));
    }

    @Override
    public void close() {
        try {
            if(this.cache2 != null){
                this.publish(Command.quit());
                if (this.isSubscribed())
                    this.unsubscribe();
            }
        } finally {
            this.cache1.close();
            if(this.cache2 != null){
                this.cache2.close();
                try {
                    this.redisClient.close();
                } catch (IOException e) {}
            }

        }
    }

    /**
     * 读取 Session 对象信息
     * @param session_id  会话id
     * @return 返回会话对象
     */
    public SessionObject getSession(String session_id) {
        SessionObject session = (SessionObject)cache1.get(session_id);
        if(session != null)
            return session;
        synchronized (_g_keyLocks.computeIfAbsent(session_id, v -> new Object())) {
            session = (SessionObject)cache1.get(session_id);
            if(session != null)
                return session;
            try {
                if(this.cache2 == null){
                    return session;
                }
                List<String> keys = cache2.keys(session_id);
                if(keys.size() == 0)
                    return null;

                List<byte[]> datas = cache2.getBytes(session_id, keys);
                session = new SessionObject(session_id, keys, datas);
                cache1.put(session_id, session);
            } catch (Exception e) {
                logger.error("Failed to read session from j2cache", e);
            } finally {
                _g_keyLocks.remove(session_id);
            }
        }
        return session;
    }

    /**
     * 保存 Session 对象信息
     * @param session 会话对象
     */
    public void saveSession(SessionObject session) {
        //write to caffeine
        cache1.put(session.getId(), session);
        if(this.cache2 == null){
            return;
        }
        //write to redis
        cache2.setBytes(session.getId(), new HashMap<String, byte[]>() {{
            put(SessionObject.KEY_CREATE_AT, String.valueOf(session.getCreated_at()).getBytes());
            put(SessionObject.KEY_ACCESS_AT, String.valueOf(session.getLastAccess_at()).getBytes());
            put(SessionObject.KEY_SERVICE_HOST, IpUtils.getActivityLocalIp().getBytes());
            session.getAttributes().entrySet().forEach((e)-> {
                try {
                    put(e.getKey(), Serializer.write(e.getValue()));
                } catch (RuntimeException | IOException excp) {
                    if(!discardNonSerializable)
                        throw ((excp instanceof RuntimeException)?(RuntimeException)excp : new RuntimeException(excp));
                }
            });
        }}, cache1.getExpire());
    }

    /**
     * 更新 session 的最后一次访问时间
     * @param session 会话对象
     */
    public void updateSessionAccessTime(SessionObject session) {
        try {
            session.setLastAccess_at(System.currentTimeMillis());
            cache1.put(session.getId(), session);
            if(this.cache2 != null){
                cache2.setBytes(session.getId(), SessionObject.KEY_ACCESS_AT, String.valueOf(session.getLastAccess_at()).getBytes());
                cache2.ttl(session.getId(), cache1.getExpire());
            }
        } finally {
            if(this.cache2 != null){
                this.publish(new Command(Command.OPT_DELETE_SESSION, session.getId(), null));
            }
        }
    }

    public void setSessionAttribute(SessionObject session, String key) {
        try {
            cache1.put(session.getId(), session);
            if(this.cache2 != null){
                try {
                    cache2.setBytes(session.getId(), key, Serializer.write(session.get(key)));
                } catch (RuntimeException | IOException e) {
                    if(!this.discardNonSerializable)
                        throw ((e instanceof RuntimeException)?(RuntimeException)e : new RuntimeException(e));
                }
            }
        } finally {
            if(this.cache2 != null){
                this.publish(new Command(Command.OPT_DELETE_SESSION, session.getId(), null));
            }
        }
    }

    public void removeSessionAttribute(SessionObject session, String key) {
        try {
            cache1.put(session.getId(), session);
            if(this.cache2 != null){
                cache2.evict(session.getId(), key);
            }
        } finally {
            if(this.cache2 != null){
                this.publish(new Command(Command.OPT_DELETE_SESSION, session.getId(), null));
            }
        }
    }

    /**
     * 删除会话
     * @param session_id 会话id
     */
    public void deleteSession(String session_id) {
        try {
            cache1.evict(session_id);
            if(this.cache2 != null){
                cache2.clear(session_id);
            }
        } finally {
            if(this.cache2 != null){
                this.publish(new Command(Command.OPT_DELETE_SESSION, session_id, null));
            }
        }
    }

}
