package com.eryansky.fastweixin.cluster.j2cache;

import com.eryansky.fastweixin.cluster.AccessTokenCache;
import com.eryansky.fastweixin.cluster.IAccessTokenCacheService;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.CacheObject;
import com.eryansky.j2cache.J2Cache;

/**
 * Token缓存 J2Cache实现
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-10-31
 */
public class J2CacheAccessTokenCacheService implements IAccessTokenCacheService {

    private CacheChannel cache = J2Cache.getChannel();
    private String region = AccessTokenCache.CACHE_NAME;
    private String preFix = "";

    public J2CacheAccessTokenCacheService() {
    }

    public J2CacheAccessTokenCacheService(String preFix) {
        this.preFix = preFix;
    }

    public J2CacheAccessTokenCacheService(String region,String preFix) {
        this.region = region;
        this.preFix = preFix;
    }

    @Override
    public synchronized boolean refreshLock(AccessTokenCache accessTokenCache) {
        CacheObject cacheObject = cache.get(region, this.preFix + AccessTokenCache.KEY_WEIXIN_TOKEN_STARTTIME);
        if (cacheObject == null) {
            cache.set(region, this.preFix + AccessTokenCache.KEY_WEIXIN_TOKEN_STARTTIME, true);
            return true;
        }
        return false;
    }

    public synchronized void clearLock() {
        cache.evict(region, this.preFix + AccessTokenCache.KEY_WEIXIN_TOKEN_STARTTIME);
    }

    @Override
    public boolean refreshJsLock(AccessTokenCache accessTokenCache) {
        CacheObject cacheObject = cache.get(region, this.preFix + AccessTokenCache.KEY_JS_TOKEN_STARTTIME);
        if (cacheObject == null) {
            cache.set(region, this.preFix + AccessTokenCache.KEY_JS_TOKEN_STARTTIME, true);
            return true;
        }
        return false;
    }

    public synchronized void clearJsLock() {
        cache.evict(region, this.preFix + AccessTokenCache.KEY_JS_TOKEN_STARTTIME);
    }

    @Override
    public AccessTokenCache getAccessTokenCache() {
        CacheObject cacheObject = cache.get(region, this.preFix + AccessTokenCache.KEY_ACCESS_TOKEN_CACHE);
        if (cacheObject != null) {
            return (AccessTokenCache) cacheObject.getValue();
        }
        return null;
    }

    @Override
    public void putAccessTokenCache(AccessTokenCache accessTokenCache) {
        cache.set(region, this.preFix + AccessTokenCache.KEY_ACCESS_TOKEN_CACHE, accessTokenCache);
    }

}
