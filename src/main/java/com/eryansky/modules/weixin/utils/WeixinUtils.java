/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.weixin.utils;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.fastweixin.company.api.QYMessageAPI;
import com.eryansky.fastweixin.company.api.config.QYAPIConfig;
import com.eryansky.fastweixin.company.api.response.GetQYSendMessageResponse;
import com.eryansky.fastweixin.company.message.QYTextMsg;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微信工具类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-18 
 */
public class WeixinUtils {

    private static Logger logger = LoggerFactory.getLogger(WeixinUtils.class);
    private static QYAPIConfig qyapiConfig = SpringContextHolder.getBean(QYAPIConfig.class);

    private WeixinUtils(){}

    public static void main(String[] args) {
        String toURL = "http://localhost/essm/m/libary";
        System.out.println(WeixinUtils.getOauth2URL(toURL));
    }

    /**
     * 获取Oauth回调URL
     * @param toURL 需要跳转的URL地址
     * @return
     */
    public static String getOauth2URL(String toURL){
        return getOauth2URL(WeixinConstants.getCropId(), toURL);
    }

    /**
     * 获取Oauth回调URL
     * @param cropID 企业号ID
     * @param toURL 需要跳转的URL地址
     * @return
     */
    public static String getOauth2URL(String cropID, String toURL){
        StringBuilder url = new StringBuilder();
        url.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=")
                .append(cropID)
                .append("&redirect_uri=")
                .append(toURL)
                .append("&response_type=code&scope=snsapi_base&state=1#wechat_redirect");
        return url.toString();
    }

    /**
     * 发送文本消息
     * @param loginName 用户账号
     * @param content 消息
     * @return
     */
    public static boolean sendTextMsg(String loginName, String content){
        return sendTextMsg(loginName,content,null);
    }
    /**
     * 发送文本消息
     * @param loginName 用户账号
     * @param content 消息
     * @param linkUrl 消息链接地址
     * @return
     */
    public static boolean sendTextMsg(String loginName, String content,String linkUrl){
        return sendTextMsg(null,loginName,content,linkUrl);
    }

    /**
     * 发送文本消息
     * @param agentId 微信应用ID
     * @param loginName 用户账号
     * @param content 消息
     * @param linkUrl 消息链接地址
     * @return
     */
    public static boolean sendTextMsg(String agentId,String loginName, String content,String linkUrl){
        if(!WeixinConstants.isTipMessage()){
//            logger.warn("消息未发送，未开启微信消息提醒！请在config.properties配置文件中配置参数[weixin.tipMessage=true]");
            return false;
        }
        QYMessageAPI qyMessageAPI = new QYMessageAPI(qyapiConfig);
        QYTextMsg qyTextMsg = new QYTextMsg();
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
        qyTextMsg.setConetnt(msg.toString());
        String _agentId = agentId;
        if(StringUtils.isBlank(_agentId)){
            _agentId = WeixinConstants.getAgentId();
        }
        qyTextMsg.setAgentId(_agentId);
        qyTextMsg.setToUser(loginName);
        GetQYSendMessageResponse qySendMessageResponse = qyMessageAPI.send(qyTextMsg);
        if(logger.isDebugEnabled()){
            logger.debug(JsonMapper.getInstance().toJson(qySendMessageResponse));
        }
        return true;
    }
}
