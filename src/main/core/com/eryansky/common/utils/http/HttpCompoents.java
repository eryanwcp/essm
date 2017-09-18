package com.eryansky.common.utils.http;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于HttpClient HTTP客户端组件
 * <br/>1、支持重试机制(3次).
 * 2、同一实例自动维护Cookie信息.
 * 3、支持gzip压缩
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-12-14
 */
public class HttpCompoents {

    private static Logger logger = LoggerFactory.getLogger(HttpCompoents.class);

    /**
     * 连接超时时间 可以配到配置文件 （单位毫秒）
     */
    private static int MAX_TIME_OUT = 30*1000;
    /**
     * 设置整个连接池最大连接数
     */
    private static int MAX_CONN = 2048;
    /**
     * 设置单个路由默认连接数
     */
    private static int SINGLE_ROUTE_MAX_CONN = 300;

    /**
     * 连接丢失后,重试次数
     */
    private static int MAX_EXECUT_COUNT = 3;

    private static HttpCompoents instance;
    private final String _DEFLAUT_CHARSET = "utf-8";
    private CloseableHttpClient httpClient;
    /**
     * 设置超时，毫秒级别
     */
    private RequestConfig requestConfig = null;
    /**
     * 连接池
     */
    private PoolingHttpClientConnectionManager connectionManager;
    /**
     * Cookie存储
     */
    private BasicCookieStore cookieStore = new BasicCookieStore();;

    {
        try {
            // 保持连接时长
            ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {
                @Override
                public long getKeepAliveDuration(HttpResponse response,
                                                 HttpContext context) {
                    long keepAlive = super.getKeepAliveDuration(response,
                            context);
                    if (keepAlive == -1) {// 如果服务器没有设置keep-alive这个参数，我们就把它设置成5秒
                        keepAlive = 5*1000;
                    }
                    return keepAlive;
                }
            };
            // 重试机制
            HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
                public boolean retryRequest(IOException exception,
                                            int executionCount, HttpContext context) {
                    if (executionCount >= MAX_EXECUT_COUNT) {// 如果已经重试了3次，就放弃
                        return false;
                    }
                    if (exception instanceof InterruptedIOException) {// 超时
                        return false;
                    }
                    if (exception instanceof UnknownHostException) {// 目标服务器不可达
                        return false;
                    }
                    if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                        return false;
                    }
                    if (exception instanceof SSLException) {// ssl握手异常
                        return false;
                    }
                    HttpClientContext clientContext = HttpClientContext
                            .adapt(context);
                    HttpRequest request = clientContext.getRequest();
                    boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                    if (idempotent) {// 如果请求是幂等的，就再次尝试
                        return true;
                    }
                    return false;
                }
            };

            CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
                public CookieSpec create(HttpContext context) {

                    return new BrowserCompatSpec() {
                        @Override
                        public void validate(Cookie cookie, CookieOrigin origin)
                                throws MalformedCookieException {
                            // Oh, I am easy
                        }
                    };
                }

            };
            Registry<CookieSpecProvider> r = RegistryBuilder
                    .<CookieSpecProvider> create()
                    .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                    .register(CookieSpecs.BROWSER_COMPATIBILITY,
                            new BrowserCompatSpecFactory())
                    .register("easy", easySpecProvider).build();

            requestConfig = RequestConfig.custom()
                    .setCookieSpec("easy")
                    .setSocketTimeout(MAX_TIME_OUT)
                    .setConnectTimeout(MAX_TIME_OUT)
                    .setConnectionRequestTimeout(MAX_TIME_OUT)
                    .setRedirectsEnabled(true).build();

            SSLContext sslContext;
            sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                    new TrustStrategy() {
                        // 信任所有
                        public boolean isTrusted(X509Certificate[] chain,
                                                 String authType) throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            // 创建SSLSocketFactory
            // 定义socket工厂类 指定协议（Http、Https）
            Registry registry = RegistryBuilder.create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)//SSLConnectionSocketFactory.getSocketFactory()
                    .build();

            connectionManager = new PoolingHttpClientConnectionManager(registry);
            connectionManager.setMaxTotal(MAX_CONN);// 连接池最大并发连接数
            connectionManager.setDefaultMaxPerRoute(SINGLE_ROUTE_MAX_CONN);// 单路由最大并发数


            httpClient = HttpClients.custom()
                    .setKeepAliveStrategy(keepAliveStrat)
                    .setRetryHandler(retryHandler)
                    .setDefaultCookieSpecRegistry(r)
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultCookieStore(cookieStore)
                    .setConnectionManager(connectionManager)
                    .addInterceptorFirst(new HttpRequestInterceptor() {
                        //增加gzip压缩请求
                        public void process(
                                final HttpRequest request,
                                final HttpContext context) throws HttpException, IOException {
                            if (!request.containsHeader("Accept-Encoding")) {
                                request.addHeader("Accept-Encoding", "gzip");
                            }
                        }
                    })
                    .addInterceptorFirst(new HttpResponseInterceptor() {
                        //拦截器：返回增加gzip解压
                        public void process(final HttpResponse response,
                                            final HttpContext context) throws HttpException, IOException {
                            HttpEntity entity = response.getEntity();
                            if (entity != null) {
                                Header ceheader = entity.getContentEncoding();
                                if (ceheader != null) {
                                    HeaderElement[] codecs = ceheader.getElements();
                                    for (int i = 0; i < codecs.length; i++) {
                                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                                            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    })
//                    .setSslcontext(sslContext)
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @see #getInstance()
     */
    private HttpCompoents() {
    }

    /**
     * @param requestConfig
     * @param httpClient
     */
    private HttpCompoents(RequestConfig requestConfig,CloseableHttpClient httpClient) {
        this.requestConfig = requestConfig;
        this.httpClient = httpClient;
    }

    /**
     * 创建新实例
     *
     * @return
     */
    public static synchronized HttpCompoents newInstance() {
        return new HttpCompoents();
    }

    /**
     * 创建新实例
     * @param requestConfig
     * @param httpClient
     * @return
     */
    public static synchronized HttpCompoents newInstance(RequestConfig requestConfig,CloseableHttpClient httpClient) {
        return new HttpCompoents(requestConfig,httpClient);
    }

    /**
     * 单例
     *
     * @return
     */
    public static synchronized HttpCompoents getInstance() {
        if (instance == null) {
            instance = new HttpCompoents();
        }
        return instance;
    }

    /**
     * 设置超时
     *
     * @param socketTimeOut （单位：毫秒）
     * @param connectTimeOut （单位：毫秒）
     */
    public void setTimeOut(int socketTimeOut, int connectTimeOut) {
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeOut)
                .setConnectTimeout(connectTimeOut).build();
    }

    /**
     * GET请求
     *
     * @param url 请求地址
     * @return
     */
    public String get(String url) {
        return get(url, _DEFLAUT_CHARSET);
    }

    /**
     * GET请求
     *
     * @param url     请求地址
     * @param charset 编码
     * @return
     */
    public String get(String url, String charset) {
        return get(url, null, charset);
    }

    /**
     * GET请求
     *
     * @param url     请求地址
     * @param headers 自定义Header
     * @param charset 编码
     * @return
     */
    public String get(String url, Map<String, String> headers, String charset) {
        String useCharset = charset;
        if (charset == null) {
            useCharset = _DEFLAUT_CHARSET;
        }
        HttpGet httpGet = new HttpGet(url);
        if (headers != null) {
            for (String key : headers.keySet()) {
                httpGet.setHeader(key, headers.get(key));
            }
        }
        httpGet.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, useCharset);
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * POST请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     */
    public String post(String url, Map<String, String> params) {
        return post(url, params, null, null);
    }

    /**
     * POST请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 自定义Header
     * @return
     */
    public String post(String url, Map<String, String> params,
                       Map<String, String> headers) {
        return post(url, params, headers, null);
    }

    /**
     * POST请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 编码
     * @return
     */
    public String post(String url, Map<String, String> params, String charset) {
        return post(url, params, null, charset);
    }

    /**
     * POST请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 自定义Header
     * @param charset 编码
     * @return
     */
    public String post(String url, Map<String, String> params,
                       Map<String, String> headers, String charset) {
        String useCharset = charset;
        if (charset == null) {
            useCharset = _DEFLAUT_CHARSET;
        }
        try {
            HttpPost httpPost = new HttpPost(url);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpPost.setHeader(key, headers.get(key));
                }
            }
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if (params != null) {
                for (String key : params.keySet()) {
                    nvps.add(new BasicNameValuePair(key, params.get(key)));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, Charset.forName(useCharset)));
            }
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, useCharset);
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 新增Cooke信息
     * @see BasicClientCookie
     *
     * @return
     */
    public void addCookie(Cookie cookie) {
        cookieStore.addCookie(cookie);
    }

    /**
     * 获取Cooke信息
     *
     * @param key
     * @return
     */
    public String getCookie(String key) {
        List<Cookie> cookies = getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(key)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 获取所有Cookie信息
     *
     * @return
     */
    public List<Cookie> getCookies() {
        List<Cookie> cookies = cookieStore.getCookies();
        return cookies;
    }


    /**
     * 打印Cookie信息
     */
    public void printCookies() {
        List<Cookie> cookies = getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                logger.info(c.getName() + " :" + c.getValue());
                logger.info("	domain:" + c.getDomain());
                logger.info("	expires:" + c.getExpiryDate());
                logger.info("	path:" + c.getPath());
                logger.info("	version:" + c.getVersion());
            }
        }
    }


    /**
     * GET请求 FluentAPI
     *
     * @param url
     * @return
     */
    public Response getResponse(String url) {
        return getResponse(url, null);
    }

    /**
     * GET请求 FluentAPI
     *
     * @param url     请求地址
     * @param headers 自定义Header
     * @return
     */
    public Response getResponse(String url, Map<String, String> headers) {
        Executor executor = getExecutor();
        try {
            Request request = Request.Get(url);
            if (headers != null) {
                for (String key : headers.keySet()) {
                    request.setHeader(key, headers.get(key));
                }
            }
            return executor.execute(request);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }



    /**
     * 获取执行器 FluentAPI
     *
     * @return
     */
    public Executor getExecutor() {
        Executor executor = Executor.newInstance(httpClient);
        return executor;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public PoolingHttpClientConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }
}