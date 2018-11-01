package com.eryansky.fastweixin.company.api.config;

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

import java.io.Serializable;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYAPIConfig extends Observable implements Serializable {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected final Integer       CACHE_TIME      = 7100 * 1000;
    protected final AtomicBoolean tokenRefreshing = new AtomicBoolean(false);
    protected final AtomicBoolean jsRefreshing    = new AtomicBoolean(false);

    protected final String  corpid;
    protected final String  corpsecret;
    protected       boolean enableJsApi;
    private       String  accessToken;
    private       String  jsApiTicket;
    private       long    jsTokenStartTime;
    private       long    weixinTokenStartTime;

    /**
     * 构造方法一，实现同时获取AccessToken。不启用jsApi
     *
     * @param corpid     corpid
     * @param corpSecret corpSecret
     */
    public QYAPIConfig(String corpid, String corpSecret) {
        this(corpid, corpSecret, false);
    }

    /**
     * 构造方法二，实现同时获取AccessToken，启用jsApi
     *
     * @param corpid      corpid
     * @param corpsecret  corpsecret
     * @param enableJsApi enableJsApi
     */
    public QYAPIConfig(String corpid, String corpsecret, boolean enableJsApi) {
        this.corpid = corpid;
        this.corpsecret = corpsecret;
        this.enableJsApi = enableJsApi;
    }

    public String getCorpid() {
        return corpid;
    }

    public String getCorpsecret() {
        return corpsecret;
    }

    public String getAccessToken() {
        long now = System.currentTimeMillis();
        long time = now - this.weixinTokenStartTime;
        try {
            if (time > CACHE_TIME && tokenRefreshing.compareAndSet(false, true)) {
                LOG.debug("准备刷新tokean.........");
                initToken(now);
            }
        } catch (Exception e) {
            LOG.error("刷新token异常", e);
            tokenRefreshing.set(false);
        }
        return accessToken;
    }

    public String getJsApiTicket() {
        if (enableJsApi) {
            long now = System.currentTimeMillis();
            long time = now - this.jsTokenStartTime;
            try {
                if (now - this.jsTokenStartTime > CACHE_TIME && jsRefreshing.compareAndSet(false, true)) {
                    LOG.debug("准备刷新JSTokean..........");
                    getAccessToken();
                    initJSToken(now);
                }
            } catch (Exception e) {
                LOG.error("刷新jsToken异常", e);
                jsRefreshing.set(false);
            }
        } else {
            jsApiTicket = null;
        }
        return jsApiTicket;
    }

    public boolean isEnableJsApi() {
        return enableJsApi;
    }

    public QYAPIConfig setEnableJsApi(boolean enableJsApi) {
        this.enableJsApi = enableJsApi;
        if (!enableJsApi)
            this.jsApiTicket = null;
        return this;
    }

    public QYAPIConfig addHandle(final ApiConfigChangeHandle handle) {
        super.addObserver(handle);
        return this;
    }

    public QYAPIConfig removeHandle(final ApiConfigChangeHandle handle) {
        super.deleteObserver(handle);
        return this;
    }

    public QYAPIConfig removeAllHandle() {
        super.deleteObservers();
        return this;
    }

    private QYAPIConfig initToken(final long refreshTime) {
        LOG.debug("开始初始化access_token..........");
        // 记住原本的事件，用于出错回滚
        final long oldTime = this.weixinTokenStartTime;
        this.weixinTokenStartTime = refreshTime;
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret=" + corpsecret;
        NetWorkCenter.get(url, null, new NetWorkCenter.ResponseCallback() {
            @Override
            public void onResponse(int resultCode, String resultJson) {
                if (HttpStatus.SC_OK == resultCode) {
                    GetTokenResponse response = JSONUtil.toBean(resultJson, GetTokenResponse.class);
                    LOG.debug("获取access_token:{}", response.getAccessToken());
                    if (null == response.getAccessToken()) {
                        // 刷新时间回滚
                        weixinTokenStartTime = oldTime;
                        throw new WeixinException("微信企业号token获取出错，错误信息:" + response.getErrcode() + "," + response.getErrmsg());
                    }
                    accessToken = response.getAccessToken();
                    // 设置通知点
                    setChanged();
                    notifyObservers(new ConfigChangeNotice(corpid, ChangeType.ACCESS_TOKEN, accessToken));
                }
            }
        });
        tokenRefreshing.set(false);
        return this;
    }

    private QYAPIConfig initJSToken(final long refreshTime) {
        LOG.debug("初始化 jsapi_ticket.........");
        // 记住原本的事件，用于出错回滚
        final long oldTime = this.jsTokenStartTime;
        this.jsTokenStartTime = refreshTime;
        String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=" + accessToken;
        NetWorkCenter.get(url, null, new NetWorkCenter.ResponseCallback() {

            @Override
            public void onResponse(int resultCode, String resultJson) {
                if (HttpStatus.SC_OK == resultCode) {
                    GetJsApiTicketResponse response = JSONUtil.toBean(resultJson, GetJsApiTicketResponse.class);
                    LOG.debug("获取jsapi_ticket:{}", response.getTicket());
                    if (StrUtil.isBlank(response.getTicket())) {
                        //刷新时间回滚
                        jsTokenStartTime = oldTime;
                        throw new WeixinException("微信企业号jsToken获取出错，错误信息:" + response.getErrcode() + "," + response.getErrmsg());
                    }
                    jsApiTicket = response.getTicket();
                    //设置通知点
                    setChanged();
                    notifyObservers(new ConfigChangeNotice(corpid, ChangeType.JS_TOKEN, jsApiTicket));
                }
            }
        });
        jsRefreshing.set(false);
        return this;
    }
}
