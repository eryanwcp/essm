package com.eryansky.j2cache.redis;

import com.eryansky.j2cache.Cache;
import com.eryansky.j2cache.CacheException;
import com.eryansky.j2cache.Level2Cache;
import com.eryansky.j2cache.lock.LockCallback;
import com.eryansky.j2cache.lock.LockCantObtainException;
import com.eryansky.j2cache.lock.LockInsideExecutedException;
import com.eryansky.j2cache.lock.LockRetryFrequency;
import io.lettuce.core.cluster.RedisClusterClient;
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
                byte[][] bytes = keys.stream().map(k -> _key(k)).toArray(byte[][]::new);
                return ((MultiKeyBinaryCommands)cmd).mget(bytes);
            }
            return keys.stream().map(k -> getBytes(k)).collect(Collectors.toList());
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
                bytes.forEach((k,v) -> setBytes(k, v));
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
                byte[][] bytes = Arrays.stream(keys).map(k -> _key(k)).toArray(byte[][]::new);
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
                if (keys != null && keys.length > 0)
                    ((MultiKeyCommands) cmd).del(keys);
            }
            else
                throw new CacheException("clear() not implemented in Redis Generic Mode");
        } finally {
            client.release();
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

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    @Override
    public <T> T lock(String lockKey, LockRetryFrequency frequency, int timeoutInSecond, long keyExpireSeconds, LockCallback<T> lockCallback) throws LockInsideExecutedException, LockCantObtainException {
//        long curentTime = System.currentTimeMillis();
//        long expireSecond = curentTime / 1000 + keyExpireSeconds;
//        long expireMillisSecond = curentTime + keyExpireSeconds * 1000;

        int retryCount = Float.valueOf(timeoutInSecond * 1000 / frequency.getRetryInterval()).intValue();
        for (int i = 0; i < retryCount; i++) {
            String result = client.get().set(_key(lockKey), "".getBytes(), SET_IF_NOT_EXIST.getBytes(), SET_WITH_EXPIRE_TIME.getBytes(), keyExpireSeconds);
            boolean flag = LOCK_SUCCESS.equals(result);
            if(flag) {
                try {
                    return lockCallback.handleObtainLock();
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                    LockInsideExecutedException ie = new LockInsideExecutedException(e);
                    return lockCallback.handleException(ie);
                } finally {
                    client.get().del(_key(lockKey));
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
    }

}
