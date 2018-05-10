/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;


import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.sys._enum.DataScope;
import com.eryansky.modules.sys._enum.RoleType;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * 角色表 entity
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@JsonFilter(" ")
public class Role extends DataEntity<Role> {

    /**
     * 名称
     */
    private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 所属机构ID
     */
    private String organId;
    /**
     * 是否系统角色 系统角色：只有超级管理员可修改 （1/0 是/否）
     */
    private String isSystem;
    /**
     * 是否有效 （1/0 是 否）
     */
    private String isActivity;
    /**
     * 权限类型 {@link RoleType}
     */
    private String roleType;
    /**
     * 数据范围 {@link DataScope}
     */
    private String dataScope;
    /**
     * 备注
     */
    private String remark;

    /**
     * 按明细设置数据范围 授权机构
     */
    private Set<String> organIds = Sets.newHashSet();


    public Role() {
        this.isSystem = YesOrNo.YES.getValue();
        this.isActivity = YesOrNo.YES.getValue();
    }

    public Role(String id) {
        super(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getOrganId() {
        return this.organId;
    }

    public void setIsSystem(String isSystem) {
        this.isSystem = isSystem;
    }

    public String getIsSystem() {
        return this.isSystem;
    }

    public void setIsActivity(String isActivity) {
        this.isActivity = isActivity;
    }

    public String getIsActivity() {
        return this.isActivity;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getRoleType() {
        return this.roleType;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    public String getDataScope() {
        return this.dataScope;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return this.remark;
    }


    public String getDataScopeView() {
        DataScope s = DataScope.getDataScopeByValue(dataScope);
        String str = "";
        if (s != null) {
            str = s.getDescription();
        }
        return str;
    }

    public String getIsSystemView() {
        YesOrNo s = YesOrNo.getYesOrNoByValue(isSystem);
        String str = "";
        if (s != null) {
            str = s.getDescription();
        }
        return str;
    }

    public Set<String> getOrganIds() {
        return organIds;
    }

    public void setOrganIds(Set<String> organIds) {
        this.organIds = organIds;
    }
}
