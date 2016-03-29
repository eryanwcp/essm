/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Result;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.http.HttpCompoents;
import com.eryansky.common.web.filter.CustomHttpServletRequestWrapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.AppUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 代理访问服务
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-12-14
 */
@RequiresUser(required = false)
@Controller
@RequestMapping(value = "${adminPath}/proxy")
public class ProxyController extends SimpleController {

    /**
     * 代理访问
     * @param nativeWebRequest
     * @param contentUrl 远程URL
     * @throws IOException
     */
    @RequestMapping(value = {""})
    public void getProxy(NativeWebRequest nativeWebRequest,String contentUrl) throws Exception {

        CustomHttpServletRequestWrapper request = nativeWebRequest.getNativeRequest(CustomHttpServletRequestWrapper.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        HttpCompoents httpCompoents = HttpCompoents.getInstance();//获取当前实例 可自动维护Cookie信息
        String param = AppUtils.joinParasWithEncodedValue(WebUtils.getParametersStartingWith(request, null));//请求参数
        String url = contentUrl + "?" + param;
        logger.debug("proxy url：{}",url);
        Response remoteResponse = httpCompoents.getResponse(url);
        try {
            // 判断返回值
            if (remoteResponse == null) {
                String errorMsg = "代理访问异常：" + contentUrl;
                logger.error(errorMsg);
                if (WebUtils.isAjaxRequest(request)) {
                    WebUtils.renderJson(response, Result.errorResult().setObj(errorMsg));
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, errorMsg);
                }
                return;
            }
            HttpResponse httpResponse = remoteResponse.returnResponse();
            HttpEntity entity = httpResponse.getEntity();
            // 判断返回值
            if (httpResponse.getStatusLine().getStatusCode() >= 400) {
                String errorMsg = "代理访问异常：" + contentUrl;
                logger.error(errorMsg);
                logger.error(httpResponse.getStatusLine().getStatusCode()+"");
                logger.error(EntityUtils.toString(entity,"utf-8"));
                if (WebUtils.isAjaxRequest(request)) {
                    WebUtils.renderJson(response, Result.errorResult().setObj(errorMsg));
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, errorMsg);
                }
                return;
            }


            // 设置Header
            response.setContentType(entity.getContentType().getValue());
            if (entity.getContentLength() > 0) {
                response.setContentLength((int) entity.getContentLength());
            }
            // 输出内容
            InputStream input = entity.getContent();
            OutputStream output = response.getOutputStream();
            // 基于byte数组读取InputStream并直接写入OutputStream, 数组默认大小为4k.
            IOUtils.copy(input, output);
            output.flush();
        } finally {
        }
    }


    /**
     * 代理访问
     * @param nativeWebRequest
     * @throws IOException
     */
    @RequestMapping(value = {"**"})
    public ModelAndView proxy(NativeWebRequest nativeWebRequest) throws Exception {
        CustomHttpServletRequestWrapper request = nativeWebRequest.getNativeRequest(CustomHttpServletRequestWrapper.class);
        String requestUrl = request.getRequestURI();

        String contentUrl = StringUtils.substringAfterLast(requestUrl, AppConstants.getAdminPath() + "/proxy/");
        String param = AppUtils.joinParasWithEncodedValue(WebUtils.getParametersStartingWith(request,null));//请求参数
        String url = contentUrl + "?" + param;
        logger.debug("proxy url：{}", url);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        HttpCompoents httpCompoents = HttpCompoents.getInstance();//获取当前实例 可自动维护Cookie信息
        Response remoteResponse = httpCompoents.getResponse(url);
        try {
            // 判断返回值
            if (remoteResponse == null) {
                String errorMsg = "代理访问异常：" + contentUrl;
                logger.error(errorMsg);
                if (WebUtils.isAjaxRequest(request)) {
                    WebUtils.renderJson(response, Result.errorResult().setObj(errorMsg));
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, errorMsg);
                }
                return null;
            }
            HttpResponse httpResponse = remoteResponse.returnResponse();
            HttpEntity entity = httpResponse.getEntity();
            // 判断返回值
            if (httpResponse.getStatusLine().getStatusCode() >= 400) {
                String errorMsg = "代理访问异常：" + contentUrl;
                logger.error(errorMsg);
                logger.error(httpResponse.getStatusLine().getStatusCode()+"");
                logger.error(EntityUtils.toString(entity,"utf-8"));
                if (WebUtils.isAjaxRequest(request)) {
                    WebUtils.renderJson(response, Result.errorResult().setObj(errorMsg));
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, errorMsg);
                }
                return null;
            }


            // 设置Header
            response.setContentType(entity.getContentType().getValue());
            if (entity.getContentLength() > 0) {
                response.setContentLength((int) entity.getContentLength());
            }
            // 输出内容
            InputStream input = entity.getContent();
            OutputStream output = response.getOutputStream();
            // 基于byte数组读取InputStream并直接写入OutputStream, 数组默认大小为4k.
            IOUtils.copy(input, output);
            output.flush();
        } catch (IOException e){
            e.printStackTrace();
        }finally {
        }
        return null;
    }

}