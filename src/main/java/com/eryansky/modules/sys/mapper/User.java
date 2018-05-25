/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;


import com.eryansky.common.orm.persistence.IUser;
import com.eryansky.common.utils.Pinyin4js;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys._enum.SexType;
import com.eryansky.modules.sys.utils.UserUtils;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 用户表 entity
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@JsonFilter(" ")
public class User extends DataEntity<User> implements IUser {

    public static final String SUPERUSER_ID = "1";
    public static final String FOLDER_USER_PHOTO = "userPhoto";

    /**
     * 登录名
     */
    private String loginName;
    /**
     * 员工编号
     */
    private String code;
    /**
     * 原始密码
     */
    private String originalPassword;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别 {@link SexType}
     */
    private String sex;
    /**
     * 出生日期 格式：yyyy-MM-dd
     */
    private Date birthday;
    /**
     * 头像
     */
    private String photo;
    /**
     * 邮箱地址
     */
    private String email;
    /**
     * 个人邮箱
     */
    private String personEmail;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 办公电话
     */
    private String tel;
    /**
     * QQ
     */
    private String qq;
    /**
     * 微信
     */
    private String weixin;
    /**
     * 住址
     */
    private String address;
    /**
     * 默认组织机构ID
     */
    private String defaultOrganId;
    /**
     * 排序号
     */
    private Integer sort;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 备注
     */
    private String remark;

    private String query;

    public User() {
    }

    public User(String id) {
        super(id);
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    public String getOriginalPassword() {
        return this.originalPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return this.sex;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @JsonFormat(pattern = DATE_FORMAT, timezone = TIMEZONE)
    public Date getBirthday() {
        return this.birthday;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getPersonEmail() {
        return this.personEmail;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTel() {
        return this.tel;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getQq() {
        return this.qq;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setDefaultOrganId(String defaultOrganId) {
        this.defaultOrganId = defaultOrganId;
    }

    public String getDefaultOrganId() {
        return this.defaultOrganId;
    }


    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return this.userType;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getWeixin() {
        return this.weixin;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCompanyId() {
        return UserUtils.getCompanyId(this.id);
    }

    public String getCompanyName() {
        return UserUtils.getCompanyName(this.id);
    }
    public String getDefaultOrganName() {
        return UserUtils.getDefaultOrganName(this.id);
    }

    public boolean isAdmin(){
        return SecurityUtils.isUserAdmin(this.id);
    }

    /**
     * 拼音首字母
     * @return
     */
    public String getNamePinyinHeadChar() {
        if(StringUtils.isNotBlank(name)){
            String str = Pinyin4js.getPinYinHeadChar(name);
            if(str!= null){
                return str.substring(0,1).toUpperCase();
            }
        }
        return name;
    }


    /**
     * 性别描述.
     */
    public String getSexView() {
        SexType ss = SexType.getByValue(sex);
        String str = "";
        if (ss != null) {
            str = ss.getDescription();
        }
        return str;
    }

}
