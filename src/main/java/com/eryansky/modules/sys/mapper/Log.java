/**
 * Copyright (c) 2012-2013 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;

import com.eryansky.common.persistence.AbstractBaseEntity;
import com.eryansky.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.sys._enum.LogType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.utils.UserUtils;

import java.util.Date;
import java.util.Map;

/**
 * 系统操作日志Entity
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date: 13-11-27 下午9:18
 */
public class Log extends DataEntity<Log> {

    /**
     * 操作日志类型
     *
     * @see com.eryansky.modules.sys._enum.LogType
     */
    private String type;

    /**
     * 用户IP地址
     */
    private String ip;
    /**
     * 设备类型 {@link eu.bitwalker.useragentutils.DeviceType}
     */
    private String deviceType;
    /**
     * 客户端（浏览器）类型 {@link eu.bitwalker.useragentutils.Browser}
     */
    private String browserType;
    /**
     * 客户端
     */
    private String userAgent;

    /**
     * 登录名
     */
    private String userId;
    /**
     * 操作开始时间
     */
    private Date operTime;
    /**
     * 操作耗时ms
     */
    private String actionTime;


    private String title;

    /**
     * 模块
     */
    private String module;
    /**
     * 操作方法
     */
    private String action;
    /**
     * 备注
     */
    private String remark;
    /**
     * 异常信息
     */
    private String exception;

    public Log() {
        super();
    }

    public Log(String id) {
        super(id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonFormat(pattern = AbstractBaseEntity.DATE_TIME_FORMAT, timezone = AbstractBaseEntity.TIMEZONE)
    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }


    public String getBrowserType() {
        return browserType;
    }

    public void setBrowserType(String browserType) {
        this.browserType = browserType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @JsonIgnore
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonIgnore
    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }


    /**
     * 设置请求参数
     *
     * @param paramMap
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setParams(Map paramMap) {
        if (paramMap == null) {
            return;
        }
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String[]> param : ((Map<String, String[]>) paramMap).entrySet()) {
            params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
            String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
            params.append(StringUtils.abbr(StringUtils.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
        }
        this.remark = params.toString();
    }

    private String userName;
    /**
     * 用户姓名
     *
     * @return
     */
    public String getUserName() {
//        if(StringUtils.isNotBlank(userId)){
//            return UserUtils.getUserName(this.userId);
//        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserOfficeName(){
        User user = UserUtils.getUser(this.getUserId());
        if(user != null){
            return user.getOfficeName();
        }
        return null;
    }

    public String getUserCompanyName(){
        User user = UserUtils.getUser(this.getUserId());
        if(user != null){
            return user.getCompanyName();
        }
        return null;
    }

    /**
     * 日志类类型 View
     */
    public String getTypeView() {
        LogType s = LogType.getByValue(type);
        String str = "";
        if (s != null) {
            str = s.getDescription();
        }
        return str;
    }
}