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
package com.eryansky.j2cache;

import com.eryansky.j2cache.lock.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cache Channel, the J2Cache methods explored to developers
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public abstract class CacheChannel implements Closeable , AutoCloseable {

	private final Logger logger = LoggerFactory.getLogger(CacheChannel.class);

	private static final Map<String, Object> _g_keyLocks = new ConcurrentHashMap<>();
	private J2CacheConfig config;
	private CacheProviderHolder holder;
    private boolean defaultCacheNullObject ;
	private boolean closed;
	private static final Map<String, LinkedBlockingQueue<String>> mQueueMap = new ConcurrentHashMap<>();
	private static final Map<String, ReentrantLock> mLockMap = new ConcurrentHashMap<>();

	public CacheChannel(J2CacheConfig config, CacheProviderHolder holder) {
		this.config = config;
		this.holder = holder;
		this.defaultCacheNullObject = config.isDefaultCacheNullObject();
		this.closed = false;
	}

	private NullObject newNullObject() {
		return new NullObject();
	}

	/**
	 * <p>Just for Inner Use.</p>
	 *
	 * <p>To clear the whole region when received this event .</p>
	 *
	 * @param region Cache region name
	 */
	protected abstract void sendClearCmd(String region);

	/**
	 * <p>Just for Inner Use.</p>
	 *
	 * <p>To remove cached data when received this event .</p>
	 *
	 * @param region Cache region name
	 * @param keys	Cache data key
	 */
	protected abstract void sendEvictCmd(String region, String...keys);

	/**
	 * 读取缓存（用户无需判断返回的对象是否为空）
	 * @param region Cache region name
	 * @param key Cache data key
     * @param cacheNullObject 是否缓存空对象
	 * @return cache object
	 */
	public CacheObject get(String region, String key, boolean...cacheNullObject)  {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		CacheObject obj = new CacheObject(region, key, CacheObject.LEVEL_1);
		obj.setValue(holder.getLevel1Cache(region).get(key));
		if(obj.rawValue() != null)
			return obj;

		String lock_key = key + '%' + region;
		synchronized (_g_keyLocks.computeIfAbsent(lock_key, v -> new Object())) {
			obj.setValue(holder.getLevel1Cache(region).get(key));
			if(obj.rawValue() != null)
				return obj;

			try {
				obj.setLevel(CacheObject.LEVEL_2);
				obj.setValue(holder.getLevel2Cache(region).get(key));
				if (obj.rawValue() != null) {
					holder.getLevel1Cache(region).put(key, obj.rawValue());
				}else {
					boolean cacheNull = (cacheNullObject.length > 0) ? cacheNullObject[0] : defaultCacheNullObject;
					if (cacheNull)
						set(region, key, newNullObject(), true);
				}
			} finally {
				_g_keyLocks.remove(lock_key);
			}
		}

		return obj;
	}

	/**
	 * 支持外部数据自动加载的缓存方法
	 * @param region Cache region name
	 * @param key Cache data key
	 * @param loader data loader
	 * @param cacheNullObject  true if you need to cache null object
	 * @return cache object
	 */
	public CacheObject get(String region, String key, Function<String, Object> loader, boolean...cacheNullObject) {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		CacheObject cache = get(region, key, false);

		if (cache.rawValue() != null)
			return cache ;

		String lock_key = key + '@' + region;
		synchronized (_g_keyLocks.computeIfAbsent(lock_key, v -> new Object())) {
			cache = get(region, key, false);

			if (cache.rawValue() != null)
				return cache ;

			try {
				Object obj = loader.apply(key);
				boolean cacheNull = (cacheNullObject.length>0)?cacheNullObject[0]: defaultCacheNullObject;
				set(region, key, obj, cacheNull);
				cache = new CacheObject(region, key, CacheObject.LEVEL_OUTER, obj);
			} finally {
				_g_keyLocks.remove(lock_key);
			}
		}
		return cache;
	}

	/**
	 * 批量读取缓存中的对象（用户无需判断返回的对象是否为空）
	 * @param region Cache region name
	 * @param keys cache keys
	 * @return cache object
	 */
	public Map<String, CacheObject> get(String region, Collection<String> keys)  {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		final Map<String, Object> objs = holder.getLevel1Cache(region).get(keys);
		List<String> level2Keys = keys.stream().filter(k -> !objs.containsKey(k) || objs.get(k) == null).collect(Collectors.toList());
		Map<String, CacheObject> results = objs.entrySet().stream().filter(p -> p.getValue() != null).collect(
			Collectors.toMap(
				p -> p.getKey(),
				p -> new CacheObject(region, p.getKey(), CacheObject.LEVEL_1, p.getValue())
			)
		);

		Map<String, Object> objs_level2 = holder.getLevel2Cache(region).get(level2Keys);
		objs_level2.forEach((k,v) -> {
			results.put(k, new CacheObject(region, k, CacheObject.LEVEL_2, v));
			if (v != null)
				holder.getLevel1Cache(region).put(k, v);
		});

		return results;
	}

	/**
	 * 使用数据加载器的批量缓存读取
	 * @param region Cache region name
	 * @param keys cache keys
	 * @param loader data loader
	 * @param cacheNullObject true if you need to cache null object
	 * @return multiple cache data
	 */
	public Map<String, CacheObject> get(String region, Collection<String> keys, Function<String, Object> loader, boolean...cacheNullObject)  {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		Map<String, CacheObject> results = get(region, keys);
		results.entrySet().stream().filter(e -> e.getValue().rawValue() == null).forEach( e -> {
			String lock_key = e.getKey() + '@' + region;
			synchronized (_g_keyLocks.computeIfAbsent(lock_key, v -> new Object())) {
				CacheObject cache = get(region, e.getKey(), false);
				if(cache.rawValue() == null) {
					try {
						Object obj = loader.apply(e.getKey());
						boolean cacheNull = (cacheNullObject.length>0)?cacheNullObject[0]: defaultCacheNullObject;
						set(region, e.getKey(), obj, cacheNull);
						e.getValue().setValue(obj);
						e.getValue().setLevel(CacheObject.LEVEL_OUTER);
					} finally {
						_g_keyLocks.remove(lock_key);
					}
				}
				else {
					e.setValue(cache);
				}
			}
		});
		return results;
	}

	/**
	 * 判断某个缓存键是否存在
	 * @param region Cache region name
	 * @param key cache key
	 * @return true if key exists
	 */
	public boolean exists(String region, String key) {
		return check(region, key) > 0;
	}

	/**
	 * 判断某个key存在于哪级的缓存中
	 * @param region cache region
	 * @param key cache key
	 * @return  0(不存在),1(一级),2(二级)
	 */
	public int check(String region, String key) {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		if(holder.getLevel1Cache(region).exists(key))
			return 1;
		if(holder.getLevel2Cache(region).exists(key))
			return 2;
		return 0;
	}

	/**
	 * Write data to J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @param value: Cache value
	 */
	public void set(String region, String key, Object value) {
		set(region, key, value, defaultCacheNullObject);
	}

	/**
	 * Write data to J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @param value: Cache value
	 * @param cacheNullObject if allow cache null object
	 */
	public void set(String region, String key, Object value, boolean cacheNullObject) {
		if (!cacheNullObject && value == null)
			return ;

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		try {
			Level1Cache level1 = holder.getLevel1Cache(region);
			level1.put(key, (value==null && cacheNullObject)?newNullObject():value);
			Level2Cache level2 = holder.getLevel2Cache(region);
			if(config.isSyncTtlToRedis())
				level2.put(key, (value==null && cacheNullObject)?newNullObject():value, level1.ttl());
			else
				level2.put(key, (value==null && cacheNullObject)?newNullObject():value);
		} finally {
			this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
		}
    }


	/**
	 * Write data to j2cache with expired setting
	 *
	 * <strong>注意：强烈不推荐使用带 TTL 的 set 方法，所有的缓存 TTL 都应该预先配置好，避免多个节点的缓存 Region 配置不同步</strong>
	 *
	 * @param region Cache Region name
	 * @param key Cache Key
	 * @param value Cache value
	 * @param timeToLiveInSeconds cache expired in second
	 */
	public void set(String region, String key, Object value, long timeToLiveInSeconds ) {
		set(region, key, value, timeToLiveInSeconds, defaultCacheNullObject);
	}

	/**
	 * Write data to j2cache with expired setting
	 *
	 * <strong>注意：强烈不推荐使用带 TTL 的 set 方法，所有的缓存 TTL 都应该预先配置好，避免多个节点的缓存 Region 配置不同步</strong>
	 *
	 * @param region Cache Region name
	 * @param key Cache Key
	 * @param value Cache value
	 * @param timeToLiveInSeconds cache expired in second
	 * @param cacheNullObject if allow cache null object
	 */
    public void set(String region, String key, Object value, long timeToLiveInSeconds, boolean cacheNullObject) {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		if (!cacheNullObject && value == null)
			return ;

    	if(timeToLiveInSeconds <= 0)
    		set(region, key, value, cacheNullObject);
    	else {
			try {
				holder.getLevel1Cache(region, timeToLiveInSeconds).put(key, (value==null && cacheNullObject)?newNullObject():value);
				Level2Cache level2 = holder.getLevel2Cache(region);
				if(config.isSyncTtlToRedis())
					level2.put(key, (value==null && cacheNullObject)?newNullObject():value, timeToLiveInSeconds);
				else
					level2.put(key, (value==null && cacheNullObject)?newNullObject():value);
			} finally {
				this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
			}
		}
	}

	/**
	 * 批量插入数据
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 */
	public void set(String region, Map<String, Object> elements){
    	set(region, elements, defaultCacheNullObject);
	}

	/**
	 * 批量插入数据
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 * @param cacheNullObject if allow cache null object
	 */
	public void set(String region, Map<String, Object> elements, boolean cacheNullObject)  {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		try {
			if (cacheNullObject && elements.containsValue(null)) {
				Map<String, Object> newElems = new HashMap<>();
				newElems.putAll(elements);
				newElems.forEach((k,v) -> {
					if (v == null)
						newElems.put(k, newNullObject());
				});
				Level1Cache level1 = holder.getLevel1Cache(region);
				level1.put(newElems);
				if(config.isSyncTtlToRedis())
					holder.getLevel2Cache(region).put(newElems, level1.ttl());
				else
					holder.getLevel2Cache(region).put(newElems);
			}
			else {
				Level1Cache level1 = holder.getLevel1Cache(region);
				level1.put(elements);
				if(config.isSyncTtlToRedis())
					holder.getLevel2Cache(region).put(elements, level1.ttl());
				else
					holder.getLevel2Cache(region).put(elements);
			}
		} finally {
			//广播
			this.sendEvictCmd(region, elements.keySet().stream().toArray(String[]::new));
		}
	}

	/**
	 * 带失效时间的批量缓存数据插入
	 *
	 * <strong>注意：强烈不推荐使用带 TTL 的 set 方法，所有的缓存 TTL 都应该预先配置好，避免多个节点的缓存 Region 配置不同步</strong>
	 *
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 * @param timeToLiveInSeconds cache expired in second
	 */
	public void set(String region, Map<String, Object> elements, long timeToLiveInSeconds){
		set(region, elements, timeToLiveInSeconds, defaultCacheNullObject);
	}

	/**
	 * 带失效时间的批量缓存数据插入
	 *
	 * <strong>注意：强烈不推荐使用带 TTL 的 set 方法，所有的缓存 TTL 都应该预先配置好，避免多个节点的缓存 Region 配置不同步</strong>
	 *
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 * @param timeToLiveInSeconds cache expired in second
	 * @param cacheNullObject if allow cache null object
	 */
	public void set(String region, Map<String, Object> elements, long timeToLiveInSeconds, boolean cacheNullObject)  {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		if(timeToLiveInSeconds <= 0)
			set(region, elements, cacheNullObject);
		else {
			try {
				if (cacheNullObject && elements.containsValue(null)) {
					Map<String, Object> newElems = new HashMap<>();
					newElems.putAll(elements);
					newElems.forEach((k,v) -> {
						if (v == null)
							newElems.put(k, newNullObject());
					});
					holder.getLevel1Cache(region, timeToLiveInSeconds).put(newElems);
					if(config.isSyncTtlToRedis())
						holder.getLevel2Cache(region).put(newElems, timeToLiveInSeconds);
					else
						holder.getLevel2Cache(region).put(newElems);
				}
				else {
					holder.getLevel1Cache(region, timeToLiveInSeconds).put(elements);
					if(config.isSyncTtlToRedis())
						holder.getLevel2Cache(region).put(elements, timeToLiveInSeconds);
					else
						holder.getLevel2Cache(region).put(elements);
				}
			} finally {
				//广播
				this.sendEvictCmd(region, elements.keySet().stream().toArray(String[]::new));
			}
		}
	}

	/**
	 * Remove cached data in J2Cache
	 *
	 * @param region:  Cache Region name
	 * @param keys: Cache key
	 */
	public void evict(String region, String...keys)  {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		try {
			//先清比较耗时的二级缓存，再清一级缓存
			holder.getLevel2Cache(region).evict(keys);
			holder.getLevel1Cache(region).evict(keys);
		} finally {
			this.sendEvictCmd(region, keys); //发送广播
		}
    }

	/**
	 * Clear the cache
	 *
	 * @param region: Cache region name
	 */
	public void clear(String region)  {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		try {
			//先清比较耗时的二级缓存，再清一级缓存
			holder.getLevel2Cache(region).clear();
			holder.getLevel1Cache(region).clear();
		}finally {
			this.sendClearCmd(region);
		}
    }

	/**
	 * 返回所有的缓存区域
	 * @return all the regions
	 */
	public Collection<Region> regions() {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		return holder.regions();
	}

	/**
	 * 删除缓存 Region
	 * @param region Cache Region Name
	 */
	public void removeRegion(String region) {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		holder.getL1Provider().removeCache(region);
	}

	/**
	 * <p>Get cache region keys</p>
	 * <p><strong>Notice: ehcache3 not support keys</strong></p>
	 *
	 * @param region: Cache region name
	 * @return key list
	 */
	public Collection<String> keys(String region)  {

		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		Set<String> keys = new HashSet<>();
		keys.addAll(holder.getLevel1Cache(region).keys());
		keys.addAll(holder.getLevel2Cache(region).keys());
		return keys;
    }

	/**
	 * Close J2Cache
	 */
	@Override
	public void close() {
		this.closed = true;
	}

	/**
	 * 获取一级缓存接口
	 * @return 返回一级缓存的 CacheProvider 实例
	 */
	public CacheProvider getL1Provider() {
		return holder.getL1Provider();
	}

	/**
	 * <p>获取二级缓存的接口，该方法可用来直接获取 J2Cache 底层的 Redis 客户端实例</p>
	 * <p>方法如下：</p>
	 * <code>
	 *     CacheChannel channel = J2Cache.getChannel();
	 *     RedisClient client = ((RedisCacheProvider)channel.getL2Provider()).getRedisClient();
	 *     try {
	 *     	   client.get().xxxxx(...);
	 *     } finally {
	 *         client.release();
	 *     }
	 * </code>
	 * @return 返回二级缓存的 CacheProvider 实例
	 */
	public CacheProvider getL2Provider() {
		return holder.getL2Provider();
	}

	/**
	 * Cache Region Define
	 */
	public static class Region {

		private String name;
		private long size;
		private long ttl;

		public Region(String name, long size, long ttl) {
			this.name = name;
			this.size = size;
			this.ttl = ttl;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public long getTtl() {
			return ttl;
		}

		public void setTtl(long ttl) {
			this.ttl = ttl;
		}

		@Override
		public String toString() {
			return String.format("[%s,size:%d,ttl:%d]", name, size, ttl);
		}
	}


	/**
	 * 队列 压入
	 * @param region
	 * @param values
	 */
	public void queuePush(String region, String... values){
		if(closed)
			throw new IllegalStateException("CacheChannel closed");

		Level2Cache level2Cache = holder.getLevel2Cache(region);
		if(!(level2Cache instanceof NullCache)){
			level2Cache.queuePush(values);
		}else{
			LinkedBlockingQueue<String> queue = mQueueMap.computeIfAbsent(region, k -> {return new LinkedBlockingQueue<String>();});
			for(String value:values){
				queue.add(value);
			}
		}
	}

	/**
	 * 队列 弹出
	 * @param region
	 * @return
	 */
	public String queuePop(String region) {
		if(closed)
			throw new IllegalStateException("CacheChannel closed");
		Level2Cache level2Cache = holder.getLevel2Cache(region);
		if(!(level2Cache instanceof NullCache)){
			return level2Cache.queuePop();
		}else{
			LinkedBlockingQueue<String> queue = mQueueMap.computeIfAbsent(region, k -> {return new LinkedBlockingQueue<String>();});
			return queue.poll();
		}
	}

	/**
	 * 队列 大小
	 * @param region
	 * @return
	 */
	public int queueSize(String region) {
		if(closed)
			throw new IllegalStateException("CacheChannel closed");
		Level2Cache level2Cache = holder.getLevel2Cache(region);
		if(!(level2Cache instanceof NullCache)){
			return level2Cache.queueSize();
		}else{
			LinkedBlockingQueue<String> queue = mQueueMap.computeIfAbsent(region, k -> {return new LinkedBlockingQueue<String>();});
			return queue.size();
		}
	}

	/**
	 * 队列 列表
	 * @param region
	 * @return
	 */
	public Collection<String> queueList(String region) {
		if(closed)
			throw new IllegalStateException("CacheChannel closed");
		Level2Cache level2Cache = holder.getLevel2Cache(region);
		if(!(level2Cache instanceof NullCache)){
			return level2Cache.queueList();
		}else{
			LinkedBlockingQueue<String> queue = mQueueMap.computeIfAbsent(region, k -> {return new LinkedBlockingQueue<String>();});
			return new ArrayList<>(queue);
		}
	}

	/**
	 * 队列 清空
	 * @param region
	 */
	public void queueClear(String region) {
		if(closed)
			throw new IllegalStateException("CacheChannel closed");
        Level2Cache level2Cache = holder.getLevel2Cache(region);
        if(!(level2Cache instanceof NullCache)){
            level2Cache.queueClear();
        }else{
			LinkedBlockingQueue<String> queue = mQueueMap.computeIfAbsent(region, k -> {return new LinkedBlockingQueue<String>();});
			queue.clear();
        }
	}

	/**
	 * 锁定对象（自动释放锁）
	 * @param region 锁对象
	 * @param keyExpireSeconds 锁超时时间（使用redis有效） 单位：秒
	 * @param lockCallback 回调函数
	 * @param <T>
	 * @return
	 * @throws LockInsideExecutedException
	 * @throws LockCantObtainException
	 */
	public <T> T lock(String region, long keyExpireSeconds,
					  LockCallback<T> lockCallback) throws LockInsideExecutedException, LockCantObtainException {
		return lock(region,LockRetryFrequency.VERY_SLOW,1,keyExpireSeconds,lockCallback);
	}

	/**
	 * 锁定对象（自动释放锁）
	 * @param region 锁对象
	 * @param timeoutInSecond 获取锁超时时间 单位：秒
	 * @param keyExpireSeconds 锁超时时间（使用redis有效） 单位：秒
	 * @param lockCallback 回调函数
	 * @param <T>
	 * @return
	 * @throws LockInsideExecutedException
	 * @throws LockCantObtainException
	 */
	public <T> T lock(String region, int timeoutInSecond, long keyExpireSeconds,
					  LockCallback<T> lockCallback) throws LockInsideExecutedException, LockCantObtainException {
		return lock(region,LockRetryFrequency.NORMAL,timeoutInSecond,keyExpireSeconds,lockCallback);
	}

	/**
	 * 锁定对象（自动释放锁）
	 * @param region 锁对象
	 * @param frequency {@link LockRetryFrequency}
	 * @param timeoutInSecond 获取锁超时时间 单位：秒
	 * @param keyExpireSeconds 锁超时时间（使用redis有效） 单位：秒
	 * @param lockCallback 回调函数
	 * @param <T>
	 * @return
	 * @throws LockInsideExecutedException
	 * @throws LockCantObtainException
	 */
	public <T> T lock(String region, LockRetryFrequency frequency, int timeoutInSecond, long keyExpireSeconds,
						LockCallback<T> lockCallback) throws LockInsideExecutedException, LockCantObtainException {
		Level2Cache level2Cache = holder.getLevel2Cache(region);
		if(!(level2Cache instanceof NullCache)){
			return level2Cache.lock(region,frequency,timeoutInSecond, keyExpireSeconds,lockCallback);
		}else{
			ReentrantLock lock  = mLockMap.computeIfAbsent(region, k -> {return new ReentrantLock();});
			int retryCount = Float.valueOf(timeoutInSecond * 1000 / frequency.getRetryInterval()).intValue();

			for (int i = 0; i < retryCount; i++) {
				boolean flag = lock.tryLock();
				if(flag) {
					try {
						return lockCallback.handleObtainLock();
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						LockInsideExecutedException ie = new LockInsideExecutedException(e);
						return lockCallback.handleException(ie);
					} finally {
						lock.unlock();
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
	}

}
