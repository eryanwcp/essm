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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * API配置类，项目中请保证其为单例
 * 实现观察者模式，用于监控token变化
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-10-31
 */
public class ClusterApiConfig extends ApiConfig {

    private static final Logger LOG             = LoggerFactory.getLogger(ApiConfig.class);
    /**
     * 这里定义token正在刷新的标识，想要达到的目标是当有一个请求来获取token，发现token已经过期（我这里的过期逻辑是比官方提供的早100秒），然后开始刷新token
     * 在刷新的过程里，如果又继续来获取token，会先把旧的token返回，直到刷新结束，之后再来的请求，将获取到新的token
     * 利用AtomicBoolean实现原理：
     * 当请求来的时候，检查token是否已经过期（7100秒）以及标识是否已经是true（表示已经在刷新了，还没刷新完），过期则将此标识设为true，并开始刷新token
     * 在刷新结束前再次进来的请求，由于标识一直是true，而会直接拿到旧的token，由于我们的过期逻辑比官方的早100秒，所以旧的还可以继续用
     * 无论刷新token正在结束还是出现异常，都在最后将标识改回false，表示刷新工作已经结束
     */
    private final AtomicBoolean tokenRefreshing = new AtomicBoolean(false);
    private final        AtomicBoolean jsRefreshing    = new AtomicBoolean(false);

    private final String  appid;
    private final String  secret;
    private       boolean enableJsApi;
    private IAccessTokenCacheService accessTokenCacheService;
    private final Integer CACHE_TIME = 7100 * 1000;

    public ClusterApiConfig(String appid, String secret, IAccessTokenCacheService accessTokenCacheService) {
        this(appid, secret, false,accessTokenCacheService);
    }

    public ClusterApiConfig(String appid, String secret, boolean enableJsApi, IAccessTokenCacheService accessTokenCacheService) {
        super(appid, secret, enableJsApi);
        this.accessTokenCacheService = accessTokenCacheService;
        AccessTokenCache accessTokenCache = accessTokenCacheService.getAccessTokenCache();
        accessTokenCache = accessTokenCache == null ? new AccessTokenCache():accessTokenCache;
        this.appid = appid;
        this.secret = secret;
        this.enableJsApi = enableJsApi;
        long now = System.currentTimeMillis();
        initToken(now,accessTokenCache);
        if (enableJsApi) initJSToken(now,accessTokenCache);
    }


    public String getAppid() {
        return appid;
    }

    public String getSecret() {
        return secret;
    }

    public String getAccessToken() {
        AccessTokenCache accessTokenCache = accessTokenCacheService.getAccessTokenCache();

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
                if(accessTokenCacheService.refreshLock(accessTokenCache)){
                    initToken(now,accessTokenCache);
                }
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
        if (enableJsApi) {
            long now = System.currentTimeMillis();
            long time = now - accessTokenCache.getJsTokenStartTime();
            try {
                //官方给出的超时时间是7200秒，这里用7100秒来做，防止出现已经过期的情况
                if (time > CACHE_TIME && this.jsRefreshing.compareAndSet(false, true)) {
                    if(accessTokenCacheService.refreshJsLock(accessTokenCache)){
                        //getAccessToken();
                        initJSToken(now,accessTokenCache);
                    }

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

    public boolean isEnableJsApi() {
        return enableJsApi;
    }

    public ClusterApiConfig setEnableJsApi(boolean enableJsApi) {
        this.enableJsApi = enableJsApi;
       return this;
    }

    /**
     * 添加配置变化监听器
     *
     * @param handle 监听器
     */
    public ClusterApiConfig addHandle(final ApiConfigChangeHandle handle) {
        super.addObserver(handle);
        return this;
    }

    /**
     * 移除配置变化监听器
     *
     * @param handle 监听器
     */
    public ClusterApiConfig removeHandle(final ApiConfigChangeHandle handle) {
        super.deleteObserver(handle);
        return this;
    }

    /**
     * 移除所有配置变化监听器
     */
    public ClusterApiConfig removeAllHandle() {
        super.deleteObservers();
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
                        //weixinTokenStartTime = oldTime;
                        accessTokenCache.setWeixinTokenStartTime(oldTime);
                        throw new WeixinException("微信公众号token获取出错，错误信息:" + response.getErrcode() + "," + response.getErrmsg());
                    }
                    String accessToken = response.getAccessToken();
                    accessTokenCache.setAccessToken(accessToken);
                    accessTokenCacheService.clearLock();
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
                        throw new WeixinException("微信公众号jsToken获取出错，错误信息:" + response.getErrcode() + "," + response.getErrmsg());
                    }
                    String jsApiTicket = response.getTicket();
                    accessTokenCache.setJsApiTicket(jsApiTicket);
                    accessTokenCacheService.clearJsLock();
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