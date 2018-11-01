package com.eryansky.fastweixin.api.config;

import com.eryansky.fastweixin.cluster.AccessTokenCache;
import com.eryansky.fastweixin.cluster.IAccessTokenCacheService;
import com.eryansky.fastweixin.api.response.GetJsApiTicketResponse;
import com.eryansky.fastweixin.api.response.GetTokenResponse;
import com.eryansky.fastweixin.exception.WeixinException;
import com.eryansky.fastweixin.handle.ApiConfigChangeHandle;
import com.eryansky.fastweixin.util.JSONUtil;
import com.eryansky.fastweixin.util.NetWorkCenter;
import com.eryansky.fastweixin.util.StrUtil;
import org.apache.http.HttpStatus;

/**
 * API配置类，项目中请保证其为单例
 * 实现观察者模式，用于监控token变化
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-10-31
 */
public class ClusterApiConfig extends ApiConfig {


    private IAccessTokenCacheService accessTokenCacheService;

    public ClusterApiConfig(String appid, String secret, IAccessTokenCacheService accessTokenCacheService) {
        this(appid, secret, false,accessTokenCacheService);
    }

    public ClusterApiConfig(String appid, String secret, boolean enableJsApi, IAccessTokenCacheService accessTokenCacheService) {
        super(appid, secret, enableJsApi);
        if(accessTokenCacheService == null){
            throw new WeixinException("参数[accessTokenCacheService]不能为null");
        }
        this.accessTokenCacheService = accessTokenCacheService;
        //初始化
        AccessTokenCache accessTokenCache = accessTokenCacheService.getAccessTokenCache();
        accessTokenCache = accessTokenCache == null ? new AccessTokenCache():accessTokenCache;
        long now = System.currentTimeMillis();
        initToken(now,accessTokenCache);
        if (enableJsApi) initJSToken(now,accessTokenCache);
    }


    public String getAccessToken() {
        AccessTokenCache accessTokenCache = accessTokenCacheService.getAccessTokenCache();
        accessTokenCache = accessTokenCache == null ? new AccessTokenCache():accessTokenCache;
        long now = System.currentTimeMillis();
        long time = now - accessTokenCache.getWeixinTokenStartTime();
        try {
            /*
             * 判断优先顺序：
             * 1.官方给出的超时时间是7200秒，这里用7100秒来做，防止出现已经过期的情况
             * 2.刷新标识判断，如果已经在刷新了，则也直接跳过，避免多次重复刷新，如果没有在刷新，则开始刷新
             */
            if (time > CACHE_TIME && this.tokenRefreshing.compareAndSet(false, true)) {
                LOG.debug("准备刷新token.............");
                initToken(now,accessTokenCache);
            }
        } catch (Exception e) {
            LOG.warn("刷新Token出错.", e);
            //刷新工作出现有异常，将标识设置回false
            this.tokenRefreshing.set(false);
        }
        return accessTokenCache.getAccessToken();
    }

    public String getJsApiTicket() {
        AccessTokenCache accessTokenCache = accessTokenCacheService.getAccessTokenCache();
        accessTokenCache = accessTokenCache == null ? new AccessTokenCache():accessTokenCache;
        if (enableJsApi) {
            long now = System.currentTimeMillis();
            long time = now - accessTokenCache.getJsTokenStartTime();
            try {
                //官方给出的超时时间是7200秒，这里用7100秒来做，防止出现已经过期的情况
                if (time > CACHE_TIME && this.jsRefreshing.compareAndSet(false, true)) {
                    initJSToken(now,accessTokenCache);
                }
            } catch (Exception e) {
                LOG.warn("刷新jsTicket出错.", e);
                //刷新工作出现有异常，将标识设置回false
                this.jsRefreshing.set(false);
            }
        } else {
            return null;
        }
        return accessTokenCache.getJsApiTicket();
    }

    public ClusterApiConfig setEnableJsApi(boolean enableJsApi) {
        this.enableJsApi = enableJsApi;
        if (!enableJsApi){
            AccessTokenCache accessTokenCache = accessTokenCacheService.getAccessTokenCache();
            accessTokenCache = accessTokenCache == null ? new AccessTokenCache():accessTokenCache;
            accessTokenCache.setJsApiTicket(null);
            accessTokenCacheService.putAccessTokenCache(accessTokenCache);
        }
       return this;
    }


    /**
     * 初始化微信配置，即第一次获取access_token
     *
     * @param refreshTime 刷新时间
     */
    private void initToken(final long refreshTime, final AccessTokenCache accessTokenCache) {
        LOG.debug("开始初始化access_token........");
        //记住原本的时间，用于出错回滚
        final long oldTime = accessTokenCache.getWeixinTokenStartTime();
        accessTokenCache.setWeixinTokenStartTime(refreshTime);
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + this.appid + "&secret=" + this.secret;
        NetWorkCenter.get(url, null, new NetWorkCenter.ResponseCallback() {
            @Override
            public void onResponse(int resultCode, String resultJson) {
                if (HttpStatus.SC_OK == resultCode) {
                    GetTokenResponse response = JSONUtil.toBean(resultJson, GetTokenResponse.class);
                    LOG.debug("获取access_token:{}", response.getAccessToken());
                    if (null == response.getAccessToken()) {
                        //刷新时间回滚
                        accessTokenCache.setWeixinTokenStartTime(oldTime);
                        accessTokenCacheService.putAccessTokenCache(accessTokenCache);
                        throw new WeixinException("微信公众号token获取出错，错误信息:" + response.getErrcode() + "," + response.getErrmsg());
                    }
                    String accessToken = response.getAccessToken();
                    accessTokenCache.setAccessToken(accessToken);
                    accessTokenCacheService.putAccessTokenCache(accessTokenCache);
                    //设置通知点
                    setChanged();
                    notifyObservers(new ConfigChangeNotice(appid, ChangeType.ACCESS_TOKEN, accessToken));
                }
            }
        });
        //刷新工作做完，将标识设置回false
        this.tokenRefreshing.set(false);
    }

    /**
     * 初始化微信JS-SDK，获取JS-SDK token
     *
     * @param refreshTime 刷新时间
     */
    private void initJSToken(final long refreshTime, final AccessTokenCache accessTokenCache) {
        LOG.debug("初始化 jsapi_ticket........");
        //记住原本的时间，用于出错回滚
        final long oldTime = accessTokenCache.getJsTokenStartTime();
        accessTokenCache.setJsTokenStartTime(refreshTime);
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + getAccessToken() + "&type=jsapi";
        NetWorkCenter.get(url, null, new NetWorkCenter.ResponseCallback() {
            @Override
            public void onResponse(int resultCode, String resultJson) {
                if (HttpStatus.SC_OK == resultCode) {
                    GetJsApiTicketResponse response = JSONUtil.toBean(resultJson, GetJsApiTicketResponse.class);
                    LOG.debug("获取jsapi_ticket:{}", response.getTicket());
                    if (StrUtil.isBlank(response.getTicket())) {
                        //刷新时间回滚
                        accessTokenCache.setJsTokenStartTime(oldTime);
                        accessTokenCacheService.putAccessTokenCache(accessTokenCache);
                        throw new WeixinException("微信公众号jsToken获取出错，错误信息:" + response.getErrcode() + "," + response.getErrmsg());
                    }
                    String jsApiTicket = response.getTicket();
                    accessTokenCache.setJsApiTicket(jsApiTicket);
                    accessTokenCacheService.putAccessTokenCache(accessTokenCache);
                    //设置通知点
                    setChanged();
                    notifyObservers(new ConfigChangeNotice(appid, ChangeType.JS_TOKEN, jsApiTicket));
                }
            }
        });
        //刷新工作做完，将标识设置回false
        this.jsRefreshing.set(false);
    }
}