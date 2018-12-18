package com.eryansky.core.security;

import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Lists;
import com.eryansky.utils.CacheUtils;
import org.springframework.cache.Cache;

import java.util.Collection;
import java.util.List;

/**
 * 应用Session上下文
 */
public class ApplicationSessionContext {

	private static ApplicationSessionContext instance;
	public final static String CACHE_SESSION = "sessionCache";

	private ApplicationSessionContext() {
	}

	public static ApplicationSessionContext getInstance() {
		if (instance == null) {
			instance = new ApplicationSessionContext();
		}
		return instance;
	}

	public synchronized void addSession(SessionInfo sessionInfo) {
		if (sessionInfo != null) {
			CacheUtils.put(CACHE_SESSION,sessionInfo.getId(),sessionInfo);
		}
	}

	public synchronized void removeSession(String sessionId) {
		if (sessionId != null) {
			CacheUtils.remove(CACHE_SESSION, sessionId);
		}
	}

	public synchronized SessionInfo getSession(String sessionId) {
		if (sessionId == null) return null;
		return (SessionInfo) CacheUtils.get(CACHE_SESSION,sessionId);
	}

	public List<SessionInfo> findSessionInfoData() {
		Collection<String> keys = CacheUtils.keys(CACHE_SESSION);
		return findSessionInfoData(keys);
	}

	public List<SessionInfo> findSessionInfoData(Collection<String> keys) {
		return CacheUtils.get(CACHE_SESSION,keys);
	}

	public Collection<String> findSessionInfoKeys() {
		return CacheUtils.keys(CACHE_SESSION);
	}


	public synchronized void addSession(String cacheName, String key, Object o) {
		if (o != null) {
			CacheUtils.put(cacheName, key, o);
		}
	}

	public synchronized void removeSession(String cacheName, String key) {
		if (key != null) {
			CacheUtils.remove(cacheName, key);
		}
	}

	public <T> T getSession(String cacheName, String key) {
		if (key == null) return null;
		return (T) CacheUtils.get(cacheName, key);
	}

	public List<Object> findSessionData(String cacheName) {
		Collection<String> keys = CacheUtils.keys(cacheName);
		return CacheUtils.get(CACHE_SESSION,keys);
	}

}