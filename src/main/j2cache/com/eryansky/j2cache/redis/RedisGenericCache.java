package com.eryansky.j2cache.redis;

import com.eryansky.j2cache.Cache;
import com.eryansky.j2cache.CacheException;
import com.eryansky.j2cache.Level2Cache;
import com.eryansky.j2cache.lock.LockCallback;
import com.eryansky.j2cache.lock.LockCantObtainException;
import com.eryansky.j2cache.lock.LockInsideExecutedException;
import com.eryansky.j2cache.lock.LockRetryFrequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.MultiKeyBinaryCommands;
import redis.clients.jedis.MultiKeyCommands;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis 缓存操作封装，基于 region+_key 实现多个 Region 的缓存（
 * @author Winter Lau(javayou@gmail.com)
 */
public class RedisGenericCache implements Level2Cache {

    private final static Logger log = LoggerFactory.getLogger(RedisGenericCache.class);

    private String namespace;
    private String region;
    private RedisClient client;

    /**
     * 缓存构造
     * @param namespace 命名空间，用于在多个实例中避免 _key 的重叠
     * @param region 缓存区域的名称
     * @param client 缓存客户端接口
     */
    public RedisGenericCache(String namespace, String region, RedisClient client) {
        if (region == null || region.trim().isEmpty())
            region = "_"; // 缺省region

        this.client = client;
        this.namespace = namespace;
        this.region = Cache.getRegionName(namespace,region);
    }

    @Override
    public boolean supportTTL() {
        return true;
    }


    private byte[] _key(String key) {
        try {
            return (this.region + ":" + key).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return (this.region + ":" + key).getBytes();
        }
    }

    @Override
    public byte[] getBytes(String key) {
        try {
            return client.get().get(_key(key));
        } finally {
            client.release();
        }
    }

    @Override
    public List<byte[]> getBytes(Collection<String> keys) {
        try {
            BinaryJedisCommands cmd = client.get();
            if(cmd instanceof MultiKeyBinaryCommands) {
                byte[][] bytes = keys.stream().map(this::_key).toArray(byte[][]::new);
                return ((MultiKeyBinaryCommands)cmd).mget(bytes);
            }
            return keys.stream().map(this::getBytes).collect(Collectors.toList());
        } finally {
            client.release();
        }
    }

    @Override
    public void setBytes(String key, byte[] bytes) {
        try {
            client.get().set(_key(key), bytes);
        } finally {
            client.release();
        }
    }

    @Override
    public void setBytes(Map<String,byte[]> bytes) {
        try {
            BinaryJedisCommands cmd = client.get();
            if(cmd instanceof MultiKeyBinaryCommands) {
                byte[][] data = new byte[bytes.size() * 2][];
                int idx = 0;
                for(String key : bytes.keySet()){
                    data[idx++] = _key(key);
                    data[idx++] = bytes.get(key);
                }
                ((MultiKeyBinaryCommands)cmd).mset(data);
            }
            else
                bytes.forEach(this::setBytes);
        } finally {
            client.release();
        }
    }

    @Override
    public void setBytes(String key, byte[] bytes, long timeToLiveInSeconds) {
        if (timeToLiveInSeconds <= 0) {
            log.debug("Invalid timeToLiveInSeconds value : {} , skipped it.", timeToLiveInSeconds);
            setBytes(key, bytes);
        }
        else {
            try {
                client.get().setex(_key(key), (int) timeToLiveInSeconds, bytes);
            } finally {
                client.release();
            }
        }
    }

    @Override
    public void setBytes(Map<String,byte[]> bytes, long timeToLiveInSeconds) {
        try {
            /* 为了支持 TTL ，没法使用批量写入方法 */
            /*
            BinaryJedisCommands cmd = client.get();
            if(cmd instanceof MultiKeyBinaryCommands) {
                byte[][] data = new byte[bytes.size() * 2][];
                int idx = 0;
                for(String key : bytes.keySet()){
                    data[idx++] = _key(key);
                    data[idx++] = bytes.get(key);
                }
                ((MultiKeyBinaryCommands)cmd).mset(data);
            }
            else
            */
            bytes.forEach((k,v) -> setBytes(k, v, timeToLiveInSeconds));
        } finally {
            client.release();
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return client.get().exists(_key(key));
        } finally {
            client.release();
        }
    }

    /**
     * 性能可能极其低下，谨慎使用
     */
    @Override
    public Collection<String> keys() {
        try {
            BinaryJedisCommands cmd = client.get();
            if (cmd instanceof MultiKeyCommands) {
                Collection<String> keys = ((MultiKeyCommands) cmd).keys(this.region + ":*");
                return keys.stream().map(k -> k.substring(this.region.length()+1)).collect(Collectors.toList());
            }
        } finally {
            client.release();
        }
        throw new CacheException("keys() not implemented in Redis Generic Mode");
    }

    @Override
    public void evict(String...keys) {
        try {
            BinaryJedisCommands cmd = client.get();
            if (cmd instanceof BinaryJedis) {
                byte[][] bytes = Arrays.stream(keys).map(this::_key).toArray(byte[][]::new);
                ((BinaryJedis)cmd).del(bytes);
            }
            else {
                for (String key : keys)
                    cmd.del(_key(key));
            }
        } finally {
            client.release();
        }
    }

    /**
     * 性能可能极其低下，谨慎使用
     */
    @Override
    public void clear() {
        try {
            BinaryJedisCommands cmd = client.get();
            if (cmd instanceof MultiKeyCommands) {
                String[] keys = ((MultiKeyCommands) cmd).keys(this.region + ":*").stream().toArray(String[]::new);
                if (keys.length > 0) {
                    ((MultiKeyCommands) cmd).del(keys);
                }
            }
            else
                throw new CacheException("clear() not implemented in Redis Generic Mode");
        } finally {
            client.release();
        }
    }

    @Override
    public Long ttl(String key) {
        try {
            return client.get().ttl(_key(key));
        } finally {
            client.release();
        }
    }

    @Override
    public void queuePush(String... values) {
        try {
            for (String value : values) {
                BinaryJedisCommands cmd = client.get();
                cmd.rpush(region.getBytes(),value.getBytes());
            }
        } finally {
            client.release();
        }
    }

    @Override
    public String queuePop() {
        try {
            BinaryJedisCommands cmd = client.get();
            byte[] data = cmd.lpop(region.getBytes());
            return data == null ? null:new String(data);
        } finally {
            client.release();
        }
    }

    @Override
    public int queueSize() {
        try {
            BinaryJedisCommands cmd = client.get();
            return cmd.llen(region.getBytes()).intValue();
        } finally {
            client.release();
        }
    }

    @Override
    public Collection<String> queueList() {
        try {
            BinaryJedisCommands cmd = client.get();
            long length = cmd.llen(region.getBytes());
            if(length == 0){
                return Collections.emptyList();
            }
            List<byte[]> values =  cmd.lrange(region.getBytes(),0,length-1);
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
            for (int i = 0; i < retryCount; i++) {
                Long result = client.get().setnx(region.getBytes(),String.valueOf(expireMillisSecond).getBytes());
                boolean flag = 1L == result;
                if(flag) {
                    try {
                        client.get().expireAt(region.getBytes(),expireSecond);
                        return lockCallback.handleObtainLock();
                    } catch (Exception e) {
                        log.error(e.getMessage(),e);
                        LockInsideExecutedException ie = new LockInsideExecutedException(e);
                        return lockCallback.handleException(ie);
                    } finally {
                        client.get().del(region.getBytes());
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
