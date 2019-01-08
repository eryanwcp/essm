/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.CacheObject;
import com.eryansky.j2cache.spring.J2CacheCacheManger;
import com.eryansky.listener.SystemInitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Cache工具类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-5-29
 */
@SuppressWarnings("unchecked")
public class CacheUtils {

	private static final Logger logger = LoggerFactory.getLogger(SystemInitListener.class);

	private static final String SYS_CACHE = "sysCache";

	/**
	 * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
	 */
	private static final class Static {
		private static J2CacheCacheManger cacheManager = SpringContextHolder.getBean("j2CacheCacheManger");
	}

	public static <T> T get(String key) {
		return get(SYS_CACHE, key);
	}


	public static <T> T get(String region, String key) {
		CacheObject cacheObject = Static.cacheManager.getCacheChannel().get(region,key);
		if(cacheObject != null && logger.isDebugEnabled()){
			logger.debug(key+":"+cacheObject.getLevel());
		}
		return cacheObject==null?null:(T)cacheObject.getValue();
	}

	public static <T> T get(String region, Collection<String> keys) {
		java.util.Map<String,CacheObject> map = Static.cacheManager.getCacheChannel().get(region,keys);
		return (T)map.values().stream().filter(x -> x!=null && x.getValue() != null).map(CacheObject::getValue).collect(Collectors.toList());
	}

	public static void put(String key, Object value) {
		put(SYS_CACHE, key, value);
	}



	public static void put(String region, String key, Object value) {
		Static.cacheManager.getCacheChannel().set(region,key,value);
	}

	public static void remove(String key) {
		remove(SYS_CACHE, key);
	}

	public static void remove(String region, String key) {
		Static.cacheManager.getCacheChannel().evict(region,key);
	}

	public static void clearCache(String region) {
		Static.cacheManager.getCacheChannel().clear(region);
	}

	public static void removeCache(String region) {
		Static.cacheManager.getCacheChannel().removeRegion(region);
	}

	public static Collection<String> keys(String region) {
		return Static.cacheManager.getCacheChannel().keys(region);
	}

	public static Collection<String> regionNames() {
		Collection<CacheChannel.Region> regions = Static.cacheManager.getCacheChannel().regions();
		return regions.stream().map(CacheChannel.Region::getName).collect(Collectors.toList());
	}

	public static Collection<CacheChannel.Region> regions() {
		return Static.cacheManager.getCacheChannel().regions();
	}

	public static CacheChannel getCacheChannel() {
		return Static.cacheManager.getCacheChannel();
	}

}
