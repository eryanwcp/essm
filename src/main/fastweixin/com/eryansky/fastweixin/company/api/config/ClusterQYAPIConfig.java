package com.eryansky.fastweixin.company.api.config;

import com.eryansky.fastweixin.cluster.AccessTokenCache;
import com.eryansky.fastweixin.cluster.IAccessTokenCacheService;
import com.eryansky.fastweixin.api.config.ChangeType;
import com.eryansky.fastweixin.api.config.ConfigChangeNotice;
import com.eryansky.fastweixin.api.response.GetJsApiTicketResponse;
import com.eryansky.fastweixin.api.response.GetTokenResponse;
import com.eryansky.fastweixin.exception.WeixinException;
import com.eryansky.fastweixin.handle.ApiConfigChangeHandle;
import com.eryansky.fastweixin.util.JSONUtil;
import com.eryansky.fastweixin.util.NetWorkCenter;
import com.eryansky.fastweixin.util.StrUtil;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 企业微信号配置 支持集群
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-10-31
 */
public final class ClusterQYAPIConfig extends QYAPIConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterQYAPIConfig.class);

    private final Integer       CACHE_TIME      = 7100000;
    private final AtomicBoolean tokenRefreshing = new AtomicBoolean(false);
    private final AtomicBoolean jsRefreshing    = new AtomicBoolean(false);

    private       boolean enableJsApi;

    private IAccessTokenCacheService accessTokenCacheService;

    /**
     * 构造方法一，实现同时获取AccessToken。不启用jsApi
     *
     * @param corpid     corpid
     * @param corpSecret corpSecret
     * @param accessTokenCacheService 集群缓存实现接口
     */
    public ClusterQYAPIConfig(String corpid, String corpSecret,IAccessTokenCacheService accessTokenCacheService) {
        this(corpid, corpSecret, false,accessTokenCacheService);
    }

    /**
     * 构造方法二，实现同时获取AccessToken，启用jsApi
     *
     * @param corpid      corpid
     * @param corpsecret  corpsecret
     * @param enableJsApi enableJsApi
     * @param accessTokenCacheService 集群缓存实现接口
     */
    public ClusterQYAPIConfig(String corpid, String corpsecret, boolean enableJsApi,IAccessTokenCacheService accessTokenCacheService) {
        super(corpid,corpsecret,enableJsApi);
        this.accessTokenCacheService = accessTokenCacheService;
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
            if (time > CACHE_TIME && tokenRefreshing.compareAndSet(false, true)) {
                LOG.debug("准备刷新tokean.........");
                initToken(now,accessTokenCache);
            }
        } catch (Exception e) {
            LOG.error("刷新token异常", e);
            tokenRefreshing.set(false);
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
                if (time > CACHE_TIME && jsRefreshing.compareAndSet(false, true)) {
                    LOG.debug("准备刷新JSTokean..........");
                    initJSToken(now,accessTokenCache);
                }
            } catch (Exception e) {
                LOG.error("刷新jsToken异常", e);
                jsRefreshing.set(false);
            }
        } else {
            return null;
        }
        return accessTokenCache.getJsApiTicket();
    }

    public boolean isEnableJsApi() {
        return enableJsApi;
    }

    public ClusterQYAPIConfig setEnableJsApi(boolean enableJsApi) {
        this.enableJsApi = enableJsApi;
        return this;
    }

    public ClusterQYAPIConfig addHandle(final ApiConfigChangeHandle handle) {
        super.addObserver(handle);
        return this;
    }

    public ClusterQYAPIConfig removeHandle(final ApiConfigChangeHandle handle) {
        super.deleteObserver(handle);
        return this;
    }

    public ClusterQYAPIConfig removeAllHandle() {
        super.deleteObservers();
        return this;
    }

    private ClusterQYAPIConfig initToken(final long refreshTime, final AccessTokenCache accessTokenCache) {
        LOG.debug("开始初始化access_token..........");
        // 记住原本的事件，用于出错回滚
        final long oldTime = accessTokenCache.getWeixinTokenStartTime();
        accessTokenCache.setWeixinTokenStartTime(refreshTime);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + getCorpid() + "&corpsecret=" + getCorpsecret();
        NetWorkCenter.get(url, null, new NetWorkCenter.ResponseCallback() {
            @Override
            public void onResponse(int resultCode, String resultJson) {
                if (HttpStatus.SC_OK == resultCode) {
                    GetTokenResponse response = JSONUtil.toBean(resultJson, GetTokenResponse.class);
                    LOG.debug("获取access_token:{}", response.getAccessToken());
                    if (null == response.getAccessToken()) {
                        // 刷新时间回滚
                        accessTokenCache.setWeixinTokenStartTime(oldTime);
                        accessTokenCacheService.putAccessTokenCache(accessTokenCache);
                        throw new WeixinException("微信企业号token获取出错，错误信息:" + response.getErrcode() + "," + response.getErrmsg());
                    }
                    String accessToken = response.getAccessToken();
                    accessTokenCache.setAccessToken(accessToken);
                    accessTokenCacheService.clearLock();
                    accessTokenCacheService.putAccessTokenCache(accessTokenCache);
                    // 设置通知点
                    setChanged();
                    notifyObservers(new ConfigChangeNotice(getCorpid(), ChangeType.ACCESS_TOKEN, accessToken));
                }
            }
        });
        tokenRefreshing.set(false);
        return this;
    }

    private ClusterQYAPIConfig initJSToken(final long refreshTime, final AccessTokenCache accessTokenCache) {
        LOG.debug("初始化 jsapi_ticket.........");
        // 记住原本的事件，用于出错回滚
        final long oldTime = accessTokenCache.getWeixinTokenStartTime();
        accessTokenCache.setJsTokenStartTime(refreshTime);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=" + getAccessToken();
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
                        throw new WeixinException("微信企业号jsToken获取出错，错误信息:" + response.getErrcode() + "," + response.getErrmsg());
                    }
                    String jsApiTicket = response.getTicket();
                    accessTokenCache.setJsApiTicket(jsApiTicket);
                    accessTokenCacheService.clearJsLock();
                    accessTokenCacheService.putAccessTokenCache(accessTokenCache);
                    //设置通知点
                    setChanged();
                    notifyObservers(new ConfigChangeNotice(getCorpid(), ChangeType.JS_TOKEN, jsApiTicket));
                }
            }
        });
        jsRefreshing.set(false);
        return this;
    }
}
