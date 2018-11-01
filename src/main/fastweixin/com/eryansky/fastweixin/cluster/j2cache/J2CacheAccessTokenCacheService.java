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

    public J2CacheAccessTokenCacheService() {
    }

    public J2CacheAccessTokenCacheService(String region) {
        this.region = region;
    }


    @Override
    public AccessTokenCache getAccessTokenCache() {
        CacheObject cacheObject = cache.get(region, AccessTokenCache.KEY_ACCESS_TOKEN_CACHE);
        if (cacheObject != null) {
            return (AccessTokenCache) cacheObject.getValue();
        }
        return null;
    }

    @Override
    public void putAccessTokenCache(AccessTokenCache accessTokenCache) {
        cache.set(region, AccessTokenCache.KEY_ACCESS_TOKEN_CACHE, accessTokenCache);
    }

}
