/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;


import com.eryansky.common.model.TreeNode;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.TreeEntity;
import com.eryansky.modules.sys._enum.OrganType;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 机构
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@JsonFilter(" ")
public class Organ extends TreeEntity<Organ> {

    /**
     * 简称
     */
    private String shortName;
    /**
     * 机构类型 {@link OrganType}
     */
    private String type;
    /**
     * 机构编码
     */
    private String code;
    /**
     * 机构系统编码
     */
    private String sysCode;
    /**
     * 地址
     */
    private String address;
    /**
     * 电话号码
     */
    private String mobile;
    /**
     * 电话号码
     */
    private String phone;
    /**
     * 传真
     */
    private String fax;
    /**
     * 机构负责人ID
     */
    private String managerUserId;
    /**
     * 副主管
     */
    private String deputyManagerUserId;
    /**
     * 分管领导
     */
    private String superManagerUserId;
    /**
     * 区域ID
     */
    private String areaId;
    /**
     * 备注
     */
    private String remark;

    public Organ() {
    }

    public Organ(String id) {
        super(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    @Override
    public Organ getParent() {
        return parent;
    }

    @Override
    public void setParent(Organ parent) {
        this.parent = parent;
    }

    public String getName() {
        return this.name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    public String getSysCode() {
        return this.sysCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFax() {
        return this.fax;
    }

    public void setManagerUserId(String managerUserId) {
        this.managerUserId = managerUserId;
    }

    public String getManagerUserId() {
        return this.managerUserId;
    }

    public void setDeputyManagerUserId(String deputyManagerUserId) {
        this.deputyManagerUserId = deputyManagerUserId;
    }

    public String getDeputyManagerUserId() {
        return this.deputyManagerUserId;
    }

    public void setSuperManagerUserId(String superManagerUserId) {
        this.superManagerUserId = superManagerUserId;
    }

    public String getSuperManagerUserId() {
        return this.superManagerUserId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaId() {
        return this.areaId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String getParentId() {
        return super.getParentId();
    }

    @JsonProperty(value = "_parentId")
    public String get_parentId() {
        String id = null;
        if (parent != null){
            id = parent.getId().equals("0") ? null:parent.getId();
        }
        return id;
    }

    /**
     * 主管名称
     *
     * @return
     */
    public String getManagerUserName() {
        return UserUtils.getUserName(managerUserId);
    }


    /**
     * 分管领导名称
     *
     * @return
     */
    public String getSuperManagerUserName() {
        return UserUtils.getUserName(superManagerUserId);
    }


    /**
     * Treegrid 关闭状态设置
     *
     * @return
     */
    public String getState() {
        return OrganUtils.hasChild(id) ? TreeNode.STATE_CLOASED:TreeNode.STATE_OPEN;
    }


    /**
     * 机构类新显示.
     */
    public String getTypeView() {
        OrganType ss = OrganType.getByValue(type);
        if (ss != null) {
            return ss.getDescription();
        }
        return type;
    }
}
