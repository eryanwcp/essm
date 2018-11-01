package com.eryansky.fastweixin.cluster;

import java.io.Serializable;

/**
 * Token 缓存
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-10-31
 */
public class AccessTokenCache implements Serializable {

    public static final String CACHE_NAME = "wechat_access_token_cache";

    public static String KEY_ACCESS_TOKEN_CACHE = "accessTokenKey";


    private String accessToken;
    private String jsApiTicket;

    private long jsTokenStartTime;
    private long weixinTokenStartTime;

    public AccessTokenCache() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getJsApiTicket() {
        return jsApiTicket;
    }

    public void setJsApiTicket(String jsApiTicket) {
        this.jsApiTicket = jsApiTicket;
    }

    public long getJsTokenStartTime() {
        return jsTokenStartTime;
    }

    public void setJsTokenStartTime(long jsTokenStartTime) {
        this.jsTokenStartTime = jsTokenStartTime;
    }

    public long getWeixinTokenStartTime() {
        return weixinTokenStartTime;
    }

    public void setWeixinTokenStartTime(long weixinTokenStartTime) {
        this.weixinTokenStartTime = weixinTokenStartTime;
    }
}
