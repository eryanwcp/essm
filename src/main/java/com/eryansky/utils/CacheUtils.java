/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.CacheObject;
import com.eryansky.j2cache.spring.J2CacheCacheManger;
import com.eryansky.listener.SystemInitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Cache工具类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-5-29
 */
public class CacheUtils {

	private static final Logger logger = LoggerFactory.getLogger(SystemInitListener.class);
	
	private static J2CacheCacheManger cacheManager = SpringContextHolder.getBean("j2CacheCacheManger");

	private static final String SYS_CACHE = "sysCache";

	public static Object get(String key) {
		return get(SYS_CACHE, key);
	}

	public static void put(String key, Object value) {
		put(SYS_CACHE, key, value);
	}

	public static void remove(String key) {
		remove(SYS_CACHE, key);
	}
	
	public static Object get(String cacheName, String key) {
		CacheObject cacheObject = cacheManager.getCacheChannel().get(cacheName,key);
		if(cacheObject != null && logger.isDebugEnabled()){
			logger.info(key+":"+cacheObject.getLevel());
		}
		return cacheObject==null?null:cacheObject.getValue();
	}

	public static void put(String cacheName, String key, Object value) {
		cacheManager.getCacheChannel().set(cacheName,key,value);
	}

	public static void remove(String cacheName, String key) {
		cacheManager.getCacheChannel().evict(cacheName,key);
	}

	public static void removeCache(String cacheName) {
		cacheManager.getCacheChannel().clear(cacheName);
	}

	public static Collection<String> keys(String cacheName) {
		return cacheManager.getCacheChannel().keys(cacheName);
	}

	public static Collection<String> cacheNames() {
		Collection<CacheChannel.Region> regions = cacheManager.getCacheChannel().regions();
		return Collections3.extractToList(regions,"name");
	}


}
