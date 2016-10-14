/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.entity;

import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.eryansky.common.persistence.IUser;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.Pinyin4js;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.eryansky.core.security.SecurityConstants;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys._enum.OrganType;
import com.eryansky.modules.sys._enum.SexType;
import com.eryansky.modules.sys.utils.OrganUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户管理User.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-21 上午12:28:04
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_sys_user")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//jackson标记不生成json对象的属性 
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
//逻辑删除注解标记 propertyName:字段名 value:删除标记的值（使用默认值"1"） type:属性类型
@Delete(propertyName = "status", type = PropertyType.S)
@JsonFilter(" ")
public class User extends DataEntity<User> implements IUser {

    /**
     * 员工编号
     */
    private String code;
    /**
     * 登录用户
     */
    private String loginName;
    /**
     * 原始密码（加密处理）
     */
    private String originalPassword;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 用户类别
     */
    private String userType;

    /**
     * 用户姓名
     */
    private String name;
    /**
     * 性别 女(0) 男(1) 保密(2) 默认：保密
     */
    private String sex = SexType.secrecy.getValue();
    /**
     * 用户出生日期
     */
    private Date birthday;

    /**
     * 头像
     */
    private String photo;

    /**
     * 邮件 以 ","分割
     */
    private String email;
    /**
     * 个人邮箱
     */
    private String personEmail;
    /**
     * QQ
     */
    private String qq;
    /**
     * 微信号
     */
    private String weixin;
    /**
     * 住址
     */
    private String address;
    /**
     * 住宅电话 以 ","分割
     */
    private String tel;
    /**
     * 手机号 以 ","分割
     */
    private String mobile;
    /**
     * 有序的关联对象集合
     */
    private List<Role> roles = Lists.newArrayList();

    /**
     * 资源 有序的关联对象集合
     */
    private List<Resource> resources = Lists.newArrayList();

    /**
     * 默认组织机构
     */
    private String defaultOrganId;

    /**
     * 组织机构
     */
    private List<Organ> organs = Lists.newArrayList();

    /**
     * 排序
     */
    private Integer orderNo;
    /**
     * 备注
     */
    private String remark;

    /**
     * 用户岗位信息
     */
    private List<Post> posts = Lists.newArrayList();

    public User() {
        super();
    }

    public User(String id) {
        super(id);
    }

    @Column(length = 36)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Column(length = 64, nullable = false)
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }


    @JsonIgnore
    // 多对多定义
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    // 中间表定义,表名采用默认命名规则
    @JoinTable(name = "t_sys_user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    // Fecth策略定义
//   @Fetch(FetchMode.SUBSELECT)
    @Where(clause = "status = "+ STATUS_NORMAL)
    // 集合按id排序.
    @OrderBy("id")
    @NotFound(action = NotFoundAction.IGNORE)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    @Column(length = 128)
    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    @Column(length = 128)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(length = 36)
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Column(length = 36)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    @JsonFormat(pattern = DATE_FORMAT, timezone = TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }


    @Column(length = 1024)
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    @Column(length = 64)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(length = 64)
    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    @Column(length = 36)
    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    @Column(length = 64)
    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    @Column(length = 255)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(length = 36)
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }


    @Column(length = 36)
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_user_resource", joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "resource_id")})
    @Where(clause = "status = "+ STATUS_NORMAL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Column(length = 36)
    public String getDefaultOrganId() {
        return defaultOrganId;
    }

    public void setDefaultOrganId(String defaultOrganId) {
        this.defaultOrganId = defaultOrganId;
    }

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_user_organ", joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "organ_id")})
    @Where(clause = "status = "+ STATUS_NORMAL)
    @OrderBy("orderNo asc")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<Organ> getOrgans() {
        return organs;
    }

    public void setOrgans(List<Organ> organs) {
        this.organs = organs;
    }


    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_user_post", joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "post_id")})
    @Where(clause = "status = "+ STATUS_NORMAL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }


    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @Column(length = 1024)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    @Transient
    public String getDefaultOrganName() {
        String doName = null;
        if (defaultOrganId != null) {
            Organ organ = OrganUtils.getOrgan(defaultOrganId);
            if(organ != null){
                doName = organ.getName();
            }
        }
        return doName;
    }

    @Transient
    public String getDefaultOrganSysCode() {
        String sysCode = null;
        if (defaultOrganId != null) {
            Organ organ = OrganUtils.getOrgan(defaultOrganId);
            if(organ != null){
                sysCode = organ.getSysCode();
            }
        }
        return sysCode;
    }

    @Transient
    public String getDefaultOrganCode() {
        String code = null;
        if (defaultOrganId != null) {
            Organ organ = getDefaultOrgan();
            if(organ != null){
                code = organ.getCode();
            }
        }
        return code;
    }


    @Transient
    public List<String> getResourceIds() {
        if (!Collections3.isEmpty(resources)) {
            return ConvertUtils.convertElementPropertyToList(resources, "id");
        }
        return null;
    }

    /**
     * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
     * <br>如果是超级管理员 直接返回 "超级管理员" AppConstants.ROLE_SUPERADMIN
     */
    @Transient
    // 非持久化属性.
    public String getRoleNames() {
        if (SecurityUtils.isUserAdmin(this.getId())) {
            return SecurityConstants.ROLE_SUPERADMIN;
        }
        return ConvertUtils.convertElementPropertyToString(roles, "name",
                ", ");
    }

    @SuppressWarnings("unchecked")
    @Transient
    public List<String> getRoleIds() {
        if (!Collections3.isEmpty(roles)) {
            return  ConvertUtils.convertElementPropertyToList(roles, "id");
        }
        return null;
    }

    @Transient
    public List<String> getOrganIds() {
        if (!Collections3.isEmpty(organs)) {
            return ConvertUtils.convertElementPropertyToList(organs, "id");
        }
        return new ArrayList<String>(0);
    }

    @Transient
    public String getOrganNames() {
        return ConvertUtils.convertElementPropertyToString(organs, "name", ", ");
    }


    /**
     * 性别描述.
     */
    @Transient
    public String getSexView() {
        SexType ss = SexType.getByValue(sex);
        String str = "";
        if (ss != null) {
            str = ss.getDescription();
        }
        return str;
    }


    /**
     * 所在部门
     * @return
     */
    @JsonIgnore
    @Transient
    public Organ getDefaultOrgan() {
        return OrganUtils.getOrgan(defaultOrganId);
    }

    /**
     * 所在部门
     * @return
     */
    @JsonIgnore
    @Transient
    public Organ getOffice() {
        Organ currentOrgan = OrganUtils.getOrgan(defaultOrganId);
        while (currentOrgan != null && !OrganType.department.getValue().equals(currentOrgan.getType()) && !OrganType.organ.getValue().equals(currentOrgan.getType())) {
            Organ parent = currentOrgan.getParent();
            if(parent != null){
                currentOrgan = parent;
            }else{
                break;
            }
        }
        return currentOrgan;
    }


    /**
     * 所在部门
     * @return
     */
    @Transient
    public String getOfficeId() {
        Organ office = getOffice();
        if(office != null){
            return office.getId();
        }
        return defaultOrganId;
    }

    /**
     * 所属部门名称
     * @return
     */
    @Transient
    public String getOfficeName() {
        return OrganUtils.getOrganName(getOfficeId());
    }


    /**
     * 所在机构
     * @return
     */
    @JsonIgnore
    @Transient
    public Organ getCompany() {
        Organ currentOrgan = OrganUtils.getOrgan(defaultOrganId);
        while (currentOrgan != null && !OrganType.organ.getValue().equals(currentOrgan.getType())) {
            Organ parent = currentOrgan.getParent();
            if(parent != null){
                currentOrgan = parent;
            }else{
                break;
            }
        }
        return currentOrgan;
    }

    /**
     * 所在机构ID
     * @return
     */
    @Transient
    public String getCompanyId() {
        Organ organ = getCompany();
        if(organ != null){
            return organ.getId();
        }
        return defaultOrganId;
    }

    /**
     * 单位名称
     * @return
     */
    @Transient
    public String getCompanyName() {
        return OrganUtils.getOrganName(getCompanyId());
    }

    /**
     * 所在顶级机构
     * @return
     */
    @JsonIgnore
    @Transient
    public Organ getRootCompany() {
        Organ company = getCompany();
        Organ parent = company.getParent();
        while (parent != null) {
            if(OrganType.organ.getValue().equals(parent.getType())){
                company = parent;
            }
            parent = company.getParent();
        }
        return company;
    }

    /**
     * 顶级机构ID
     * @return
     */
    @Transient
    public String getRootCompanyId() {
        Organ organ = getRootCompany();
        if(organ != null){
            return organ.getId();
        }
        return defaultOrganId;
    }

    /**
     * 顶级机构名称
     * @return
     */
    @Transient
    public String getRootCompanyName() {
        Organ organ = getRootCompany();
        if(organ != null){
            return organ.getName();
        }
        return getDefaultOrganName();
    }


    /**
     * 得到排序编码 组织机构系统编码 最长
     *
     * @return
     */
    @Transient
    public String getSortCode() {
        String code = "";
        List<Organ> organs = this.getOrgans();
        if (!Collections3.isEmpty(organs)) {
            for (Organ organ : organs) {
                if (StringUtils.isNotBlank(code)) {
                    if (code.length() < organ.getSysCode().length()) {
                        code = organ.getSysCode();
                    }
                } else {
                    code = organ.getSysCode();
                }
            }
        }
        return code;
    }

    /**
     * 用户岗位名称 VIEW 多个之间以","分割
     *
     * @return
     */
    @Transient
    public String getPostNames() {
        return ConvertUtils.convertElementPropertyToString(posts, "name", ",");
    }


    @Transient
    public List<String> getPostIds() {
        if (Collections3.isNotEmpty(posts)) {
            return ConvertUtils.convertElementPropertyToList(posts, "id");
        }
        return Lists.newArrayList();
    }

    @Transient
    public boolean isAdmin(){
        return SecurityUtils.isUserAdmin(this.id);
    }

    /**
     * 拼音首字母
     * @return
     */
    @Transient
    public String getNamePinyinHeadChar() {
        if(StringUtils.isNotBlank(name)){
            String str = Pinyin4js.getPinYinHeadChar(name);
            if(str!= null){
                return str.substring(0,1).toUpperCase();
            }
        }
        return name;
    }
}
