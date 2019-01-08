/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.weixin.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.fastweixin.api.CustomAPI;
import com.eryansky.fastweixin.api.OauthAPI;
import com.eryansky.fastweixin.api.config.ApiConfig;
import com.eryansky.fastweixin.api.enums.OauthScope;
import com.eryansky.fastweixin.api.enums.ResultType;
import com.eryansky.fastweixin.company.api.config.QYAPIConfig;
import com.eryansky.fastweixin.message.TextMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信工具类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-07-16
 */
public class WeixinUtils {

    private static Logger logger = LoggerFactory.getLogger(WeixinUtils.class);

    /**
     * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
     */
    public static final class Static {
        private static ApiConfig apiConfig = SpringContextHolder.getBean(ApiConfig.class);

    }
    private WeixinUtils(){}

    public static void main(String[] args) {
    }

    /**
     * 判断是否是微信客户端
     * @param request
     * @return
     */
    public static boolean isWeixinBrowser(HttpServletRequest request){
        String userAgent = UserAgentUtils.getHTTPUserAgent(request);
        return StringUtils.isNotBlank(userAgent) && StringUtils.contains(userAgent,"MicroMessenger");
//        return UserAgentUtils.isMQQBrowser(request);
    }

    /**
     * 获取Oauth回调URL
     * @param toURL 需要跳转的URL地址
     * @return
     */
    public static String getOauth2URL(String toURL){
        OauthAPI oauthAPI = new OauthAPI(Static.apiConfig);
        return oauthAPI.getOauthPageUrl(toURL, OauthScope.SNSAPI_BASE,null);
    }


    /**
     * 发送文本消息
     * @param oppenid 用户账号
     * @param content 消息
     * @return
     */
    public static boolean sendTextMsg(String oppenid, String content){
        return sendTextMsg(oppenid,content,null);
    }

    /**
     * 发送文本消息
     * @param openid 微信公众号ID
     * @param content 消息
     * @param linkUrl 消息链接地址
     * @return
     */
    public static boolean sendTextMsg(String openid, String content,String linkUrl){
        if(!WeixinConstants.isTipMessage()){
//            logger.warn("消息未发送，未开启微信消息提醒！请在config.properties配置文件中配置参数[weixin.tipMessage=true]");
            return false;
        }
        CustomAPI customAPI = new CustomAPI(Static.apiConfig);
        TextMsg textMsg = new TextMsg();

        StringBuffer msg = new StringBuffer();
        if(StringUtils.isBlank(linkUrl)){
            msg.append(StringUtils.replaceHtml(content));
        }else{
            msg.append("<a href=\"")
                    .append(linkUrl)
                    .append("\">")
                    .append(StringUtils.replaceHtml(content))
                    .append("</a>");
        }
        textMsg.setContent(msg.toString());
        textMsg.setToUserName(openid);
        ResultType resultType = customAPI.sendCustomMessage(openid,textMsg);
        if(!ResultType.SUCCESS.equals(resultType)){
            logger.error(resultType.toString());
            return false;
        }
        return true;
    }
}
