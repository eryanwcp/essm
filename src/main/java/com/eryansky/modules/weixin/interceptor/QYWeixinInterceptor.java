/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.weixin.interceptor;

import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.weixin.utils.QYWeixinUtils;
import com.eryansky.modules.weixin.utils.WeixinUtils;
import com.eryansky.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 微信公众号自动跳转Oauth2认证
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-08-06
 */
public class QYWeixinInterceptor extends HandlerInterceptorAdapter {


    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //登录用户
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(sessionInfo != null){
            return true;
        }
        String requestUrl = request.getRequestURI();
        requestUrl = requestUrl.substring(request.getContextPath().length()).replaceAll("//","/");
        if(!WeixinUtils.isWeixinBrowser(request)){//非微信客户端
            return true;
        }


        String resultURL = QYWeixinUtils.getOauth2URL(AppConstants.getAppURL()+requestUrl);
        logger.warn("[{}]自动跳转[{}]",new Object[]{request.getSession().getId(),requestUrl});
        response.sendRedirect(resultURL);
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        if(e != null){

        }
    }


}
