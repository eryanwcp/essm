/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.weixin.utils;

import com.eryansky.utils.AppConstants;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-11-30 
 */
public class WeixinConstants extends AppConstants {

    /**
     * 微信OpenID
     * @return
     */
    public static String getOpenId() {
        String code = "weixin.openID";
        return getConfigValue(code);
    }

    /**
     * 微信TOKEN
     * @return
     */
    public static String getToken() {
        String code = "weixin.token";
        return getConfigValue(code,"myToken");
    }

    /**
     * 微信AppId
     * @return
     */
    public static String getAppId() {
        String code = "weixin.appId";
        return getConfigValue(code);
    }


    /**
     * 微信secret
     * @return
     */
    public static String getSecret() {
        String code = "weixin.secret";
        return getConfigValue(code);
    }

    /**
     * 微信AESKey
     * @return
     */
    public static String getAESKey() {
        String code = "weixin.AESKey";
        return getConfigValue(code);
    }

    /**
     * 微信CropId
     * @return
     */
    public static String getCropId() {
        String code = "weixin.cropId";
        return getConfigValue(code);
    }

    /**
     * 微信CorpSecret
     * @return
     */
    public static String getCorpSecret() {
        String code = "weixin.corpSecret";
        return getConfigValue(code);
    }

    /**
     * 微信CorpSecret
     * @return
     */
    public static boolean getEnableJsApi() {
        String code = "weixin.enableJsApi";
        String value = getConfigValue(code);
        return "true".equals(value) || "1".equals(value);

    }

    /**
     * 应用ID
     * @return
     */
    public static String getAgentId() {
        String code = "weixin.agentId";
        return getConfigValue(code);
    }

    /**
     * 微信消息应用ID
     * @return
     */
    public static String getWeixinMessageAgentId() {
        String code = "weixin.message.agentId";
        return getConfigValue(code);
    }




    /**
     * 是否提醒微信消息
     * @return
     */
    public static boolean isTipMessage() {
        String code = "weixin.tipMessage";
        String value = getConfigValue(code);
        return "true".equals(value) || "1".equals(value);
    }

}
