/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

import com.eryansky.common.orm.persistence.AbstractBaseEntity;
import com.eryansky.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.bitwalker.useragentutils.DeviceType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * session登录用户对象.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-24 下午2:53:59
 */
@JsonFilter(" ")
@SuppressWarnings("serial")
public class SessionInfo implements Serializable {

    /**
     * ID
     */
    private String id;
    /**
     * sessionID
     */
    private String sessionId;
    /**
     * token
     */
    private String token;
    /**
     * 服务主机
     */
    private String host;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 登录姓名
     */
    private String name;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 客户端IP
     */
    private String ip;
    /**
     * 设备类型 {@link com.eryansky.core.security._enum.DeviceType}
     */
    private String sysTemDeviceType;
    /**
     * 设备类型 {@link DeviceType}
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
     * 角色ID集合
     */
    private List<String> roleIds;
    /**
     * 部门ID
     */
    private String loginOrganId;
    /**
     * 系统登录部门编码
     */
    private String loginOrganSysCode;
    /**
     * 机构ID
     */
    private String loginCompanyId;
    /**
     * 登录所在机构
     */
    private String loginCompanyCode;
    /**
     * 系统登录部门名称
     */
    private String loginOrganName;
    /**
     * 用户岗位
     */
    private List<String> postCodes = new ArrayList<String>(0);

    /**
     * 登录时间
     */
    private Date loginTime = new Date();
    /**
     * 最后访问时间
     */
    private Date updateTime = new Date();
    /**
     * 经度
     */
    private BigDecimal longitude;
    /**
     * 纬度
     */
    private BigDecimal latitude;
    /**
     * 精确度
     */
    private BigDecimal accuracy;
    /**
     * 授权角色
     */
    private List<PermissonRole> permissonRoles = new ArrayList<PermissonRole>(0);
    /**
     * 授权权限（菜单/功能）
     */
    private List<Permisson> permissons = new ArrayList<Permisson>(0);

    /**
     * APP客户端版本
     */
    private String appVersion;
    /**
     * 登录的设备编号
     */
    private String deviceCode;


    public SessionInfo() {
    }

    /**
     * ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * sessionID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 设置 sessionID
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 服务主机
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置 服务主机
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 登录名
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * 设置 登录名
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * 登录姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 登录姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 用户类型
     */
    public String getUserType() {
        return userType;
    }

    /**
     * 设置 用户类型
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 客户端IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置 客户端IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowserType() {
        return browserType;
    }

    public void setBrowserType(String browserType) {
        this.browserType = browserType;
    }

    public String getSysTemDeviceType() {
        return sysTemDeviceType;
    }

    public void setSysTemDeviceType(String sysTemDeviceType) {
        this.sysTemDeviceType = sysTemDeviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 角色ID集合
     */
    public List<String> getRoleIds() {
        return roleIds;
    }

    /**
     * 设置 角色ID集合
     */
    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * 设置登录时间
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * 登录时间
     */
    // 设定JSON序列化时的日期格式
    @JsonFormat(pattern = AbstractBaseEntity.DATE_TIME_FORMAT, timezone = AbstractBaseEntity.TIMEZONE)
    public Date getLoginTime() {
        return loginTime;
    }

    @JsonFormat(pattern = AbstractBaseEntity.DATE_TIME_FORMAT, timezone = AbstractBaseEntity.TIMEZONE)
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(BigDecimal accuracy) {
        this.accuracy = accuracy;
    }



    public String getLoginOrganId() {
        return loginOrganId;
    }

    public void setLoginOrganId(String loginOrganId) {
        this.loginOrganId = loginOrganId;
    }


    public String getLoginOrganSysCode() {
        return loginOrganSysCode;
    }

    public void setLoginOrganSysCode(String loginOrganSysCode) {
        this.loginOrganSysCode = loginOrganSysCode;
    }

    public String getLoginCompanyId() {
        return loginCompanyId;
    }

    public void setLoginCompanyId(String loginCompanyId) {
        this.loginCompanyId = loginCompanyId;
    }

    public String getLoginCompanyCode() {
        return loginCompanyCode;
    }

    public void setLoginCompanyCode(String loginCompanyCode) {
        this.loginCompanyCode = loginCompanyCode;
    }


    /**
     * 默认登录组织机构名称
     *
     * @return
     */
    public String getLoginOrganName() {
        return loginOrganName;
    }

    /**
     * 设置默认登录组织机构名称
     */
    public void setLoginOrganName(String loginOrganName) {
        this.loginOrganName = loginOrganName;
    }

    public List<String> getPostCodes() {
        return postCodes;
    }

    public void setPostCodes(List<String> postCodes) {
        this.postCodes = postCodes;
    }

    /**
     * 是否是超级管理员
     *
     * @return
     */
    public boolean isSuperUser() {
        return SecurityUtils.isUserAdmin(this.getUserId());
    }

    @JsonIgnore
    public List<PermissonRole> getPermissonRoles() {
        return permissonRoles;
    }

    public SessionInfo setPermissonRoles(List<PermissonRole> permissonRoles) {
        this.permissonRoles = permissonRoles;
        return this;
    }


    public SessionInfo addPermissonRoles(PermissonRole permissonRole) {
        this.permissonRoles.add(permissonRole);
        return this;
    }
    @JsonIgnore
    public List<Permisson> getPermissons() {
        return permissons;
    }

    public SessionInfo setPermissons(List<Permisson> permissons) {
        this.permissons = permissons;
        return this;
    }

    public SessionInfo addPermissons(Permisson permisson) {
        this.permissons.add(permisson);
        return this;
    }

    public boolean isMobileLogin(){
        if(StringUtils.isBlank(deviceType)){
            return false;
        }
        DeviceType _deviceType = DeviceType.valueOf(deviceType);
        return DeviceType.MOBILE.equals(_deviceType) || DeviceType.TABLET.equals(_deviceType);
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }


    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean isAuthenticated() {
        return true;
    }
    public String getPrincipal() {
        return name;
    }

}
