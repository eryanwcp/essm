/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

import com.eryansky.common.persistence.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * session登录用户对象.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-24 下午2:53:59
 */
@SuppressWarnings("serial")
public class SessionInfo implements Serializable {

    /**
     * sessionID
     */
    private String id;
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
     * 角色ID集合
     */
    private List<String> roleIds;
    /**
     * 角色名称组合
     */
    private String roleNames;
    /**
     * 部门ID
     */
    private String loginOrganId;
    /**
     * 系统登录部门编码
     */
    private String loginOrganSysCode;
    /**
     * 系统登录部门名称
     */
    private String loginOrganName;
    /**
     * 用户属组织机构名称 以","分割
     */
    private String organNames;

    /**
     * 用户岗位
     */
    private List<String> postIds;
    /**
     * 用户岗位名称
     */
    private String postNames;

    /**
     * 登录时间
     */
    private Date loginTime = Calendar.getInstance().getTime();
    /**
     * 授权角色
     */
    private List<PermissonRole> permissonRoles = new ArrayList<PermissonRole>(0);
    /**
     * 授权权限（菜单/功能）
     */
    private List<Permisson> permissons = new ArrayList<Permisson>(0);


    public SessionInfo() {
    }

    /**
     * sessionID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置 sessionID
     */
    public void setId(String id) {
        this.id = id;
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
     * 角色名称组合
     */
    public String getRoleNames() {
        return roleNames;
    }

    /**
     * 设置 角色名称组合
     */
    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
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
     * 登录时间
     */
    // 设定JSON序列化时的日期格式
    @JsonFormat(pattern = AbstractBaseEntity.DATE_TIME_FORMAT, timezone = AbstractBaseEntity.TIMEZONE)
    public Date getLoginTime() {
        return loginTime;
    }

    /**
     * 设置登录时间
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
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

    /**
     * 组织机构名称
     *
     * @return
     */
    public String getOrganNames() {
        return organNames;
    }

    /**
     * 设置组织机构名称
     */
    public void setOrganNames(String organNames) {
        this.organNames = organNames;
    }

    public List<String> getPostIds() {
        return postIds;
    }

    public void setPostIds(List<String> postIds) {
        this.postIds = postIds;
    }

    public String getPostNames() {
        return postNames;
    }

    public void setPostNames(String postNames) {
        this.postNames = postNames;
    }
    /**
     * 是否是超级管理员
     *
     * @return
     */
    public boolean isSuperUser() {
        return SecurityUtils.isCurrentUserAdmin();
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
