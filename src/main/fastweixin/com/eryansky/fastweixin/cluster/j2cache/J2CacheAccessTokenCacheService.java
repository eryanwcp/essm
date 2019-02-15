package com.eryansky.fastweixin.cluster.j2cache;

import com.eryansky.fastweixin.cluster.AccessTokenCache;
import com.eryansky.fastweixin.cluster.IAccessTokenCacheService;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.CacheObject;
import com.eryansky.j2cache.J2Cache;

/**
 * Token缓存 J2Cache实现
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-10-31
 */
public class J2CacheAccessTokenCacheService implements IAccessTokenCacheService {

    private String region = AccessTokenCache.CACHE_NAME;
    private String prefix = "";
    /**
     * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
     */
    private static final class Static {
        private static CacheChannel cache = J2Cache.getChannel();
    }

    public J2CacheAccessTokenCacheService() {
    }

    public J2CacheAccessTokenCacheService(String region) {
        this.region = region;
    }


    public J2CacheAccessTokenCacheService(String region, String prefix) {
        this.region = region == null || "".equals(region.trim()) ? AccessTokenCache.CACHE_NAME:region;
        this.prefix = prefix;
    }

    @Override
    public AccessTokenCache getAccessTokenCache() {
        CacheObject cacheObject = Static.cache.get(region, this.prefix + AccessTokenCache.KEY_ACCESS_TOKEN_CACHE);
        if (cacheObject != null) {
            return (AccessTokenCache) cacheObject.getValue();
        }
        return null;
    }

    @Override
    public void putAccessTokenCache(AccessTokenCache accessTokenCache) {
        Static.cache.set(region, this.prefix + AccessTokenCache.KEY_ACCESS_TOKEN_CACHE, accessTokenCache);
    }

}
