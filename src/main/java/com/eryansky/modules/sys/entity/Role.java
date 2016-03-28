/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.entity;

import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * 角色
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-21 上午12:27:56 
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_sys_role")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Delete(propertyName = "status", type = PropertyType.S)
//jackson标记不生成json对象的属性 
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" , "handler","fieldHandler"})
public class Role extends DataEntity<Role> {

    /**
     * 所属部门
     */
    private String organId;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色编码（英文）
     */
    private String code;
    /**
     * 描述
     */
    private String remark;
    /**
     * 关联的资源
     */
    private List<Resource> resources = Lists.newArrayList();

    /**
     * 是否系统角色 系统角色：只有超级管理员可修改 （1/0 是/否）
     */
    private String isSystem = YesOrNo.YES.getValue();
    /**
     * 是否有效 （1/0 是 否）
     */
    private String isActivity = YesOrNo.YES.getValue();
    /**
     * 权限类型 {@link com.eryansky.modules.sys._enum.RoleType}
     */
    private String roleType;
    /**
     * 数据范围 {@link DataScope}
     */
    private String dataScope = DataScope.SELF.getValue();// 数据范围

    /**
     * 按明细设置数据范围
     */
    private List<Organ> organs = Lists.newArrayList();

    /**
     * 关联的用户
     */
    private List<User> users = Lists.newArrayList();

    public Role() {
        super();
    }

    public Role(String id) {
        super(id);
    }

    public Role(String id, String name) {
        super(id);
        this.name = name;
    }

    @Column(length = 36)
    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    @Column(length = 100,nullable = false)
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

    @Column(length = 1)
    public String getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(String isSystem) {
        this.isSystem = isSystem;
    }
    @Column(length = 1)
    public String getIsActivity() {
        return isActivity;
    }

    public void setIsActivity(String isActivity) {
        this.isActivity = isActivity;
    }
    @Column(length = 1)
    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }
    @Column(length = 1)
    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    @Column(length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_role_resource", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "resource_id") })
//    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("orderNo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    // 中间表定义,表名采用默认命名规则
    @JoinTable(name = "t_sys_user_role", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
    @Where(clause = "status = " + STATUS_NORMAL)
    @OrderBy("orderNo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }


    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    // 中间表定义,表名采用默认命名规则
    @JoinTable(name = "t_sys_role_organ", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
    @Where(clause = "status = "+ STATUS_NORMAL)
    @OrderBy("orderNo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<Organ> getOrgans() {
        return organs;
    }

    public void setOrgans(List<Organ> organs) {
        this.organs = organs;
    }

    /**
     * 角色拥有的资源id字符串集合
     *
     * @return
     */
    @Transient
    @SuppressWarnings("unchecked")
    public List<String> getResourceIds() {
        if (!Collections3.isEmpty(resources)) {
            return ConvertUtils.convertElementPropertyToList(resources, "id");
        }
        return null;
    }


    /**
     * 角色拥有的资源字符串,多个之间以","分割
     *
     * @return
     */
    @Transient
    public String getResourceNames() {
        List<Resource> ms = Lists.newArrayList();
        for(Resource m: resources){
            if(m.getStatus().equals(StatusState.NORMAL.getValue())){
                ms.add(m);
            }
        }
        return ConvertUtils.convertElementPropertyToString(ms, "name",
                ", ");
    }

    @Transient
    public List<String> getUserIds() {
        if (!Collections3.isEmpty(users)) {
            return ConvertUtils.convertElementPropertyToList(users, "id");
        }
        return null;
    }

    /**
     * 用户拥有的角色字符串,多个之间以","分割
     *
     * @return
     */
    @Transient
    public String getUserNames() {
        return ConvertUtils.convertElementPropertyToString(users,
                "loginName", ", ");
    }




    @Override
    public String toString() {
        return JsonMapper.getInstance().toJson(this);
    }
}
