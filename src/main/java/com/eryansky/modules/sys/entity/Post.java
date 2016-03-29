/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.entity;

import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.eryansky.modules.sys.utils.OrganUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * 岗位 entity
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-06-09 14:04
 */
@Entity
@Table(name = "t_sys_post")
// jackson标记不生成json对象的属性
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
@JsonFilter(" ")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Delete(propertyName = "status", type = PropertyType.S)
public class Post extends DataEntity<Post> {

    /**
     * 岗位名称
     */
    private String name;
    /**
     * 岗位编码
     */
    private String code;
    /**
     * 备注
     */
    private String remark;

    /**
     * 所属部门
     */
    private String organId;

    /**
     * 岗位用户
     */
    private List<User> users = Lists.newArrayList();
    /**
     * 附属机构
     */
    private List<String> organIds = Lists.newArrayList();

    public Post() {
    }

    @Column(length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 64)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(length = 36)
    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_user_post", joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    @Where(clause = "status = "+ STATUS_NORMAL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @ElementCollection
    @CollectionTable(name = "t_sys_post_organ", joinColumns = {@JoinColumn(name = "post_id")})
    @Column(name = "organ_id",length = 36)
    public List<String> getOrganIds() {
        return organIds;
    }

    public void setOrganIds(List<String> organIds) {
        this.organIds = organIds;
    }

    /**
     * 附属机构名称
     * @return
     */
    @Transient
    public String getOrganIdsNames() {
        return OrganUtils.getOrganNames(organIds);
    }

    /**
     * 机构名称 VIEW
     * @return
     */
    @Transient
    public String getOrganName() {
        if(StringUtils.isNotBlank(this.organId)){
            return OrganUtils.getOrganName(organId);
        }
        return null;
    }

    @Transient
    public List<String> getUserIds() {
        if(Collections3.isNotEmpty(users)){
            return ConvertUtils.convertElementPropertyToList(users, "id");
        }
        return Lists.newArrayList();
    }

    /**
     * 岗位用户名称 VIEW 多个之间以"，"分割
     * @return
     */
    @Transient
    public String getUserNames() {
        return ConvertUtils.convertElementPropertyToString(users, "name", ",");
    }



}