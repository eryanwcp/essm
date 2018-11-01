package com.eryansky.fastweixin.cluster;

/**
 * AccessToken 刷新接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-10-31
 */
public interface IAccessTokenCacheService {

    boolean refreshLock(AccessTokenCache accessTokenCache);

    void clearLock();

    boolean refreshJsLock(AccessTokenCache accessTokenCache);

    void clearJsLock();

    AccessTokenCache getAccessTokenCache();

    void putAccessTokenCache(AccessTokenCache accessTokenCache);
}
