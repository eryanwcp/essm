package com.eryansky.core.security;

import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Lists;
import com.eryansky.utils.CacheUtils;
import net.sf.ehcache.Cache;

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

	public List<SessionInfo> getSessionInfoData() {
		List<SessionInfo> sessionInfoList = Lists.newArrayList();
		Cache cache = CacheUtils.getCache(CACHE_SESSION);
		if (Collections3.isNotEmpty(cache.getKeys())) {
			for(Object key:cache.getKeys()){
				SessionInfo sessionInfo = (SessionInfo) cache.get(key).getObjectValue();
				sessionInfoList.add(sessionInfo);
			}
		}
		return sessionInfoList;
	}
}