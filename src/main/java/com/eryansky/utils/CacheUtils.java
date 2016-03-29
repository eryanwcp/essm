/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.utils;

import com.eryansky.common.spring.SpringContextHolder;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Cache工具类
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-5-29
 */
public class CacheUtils {
	
	private static CacheManager cacheManager = SpringContextHolder.getBean("ehCacheManagerFactory");

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
		Element element = getCache(cacheName).get(key);
		return element==null?null:element.getObjectValue();
	}

	public static void put(String cacheName, String key, Object value) {
		Element element = new Element(key, value);
		Cache cache = getCache(cacheName);
		cache.put(element);
		cache.flush();
	}

	public static void remove(String cacheName, String key) {
		Cache cache = getCache(cacheName);
		cache.remove(key);
		cache.flush();
	}

	public static void removeCache(String cacheName) {
		Cache cache = getCache(cacheName);
		cache.removeAll();
		cache.flush();
		cacheManager.removeCache(cacheName);
	}
	
	/**
	 * 获得一个Cache，没有则创建一个。
	 * @param cacheName
	 * @return
	 */
	public static Cache getCache(String cacheName){

		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null){
			cacheManager.addCache(cacheName);
			cache = cacheManager.getCache(cacheName);
			cache.getCacheConfiguration().setEternal(true);
		}
		return cache;
	}

	public static CacheManager getCacheManager() {
		return cacheManager;
	}
	
}
