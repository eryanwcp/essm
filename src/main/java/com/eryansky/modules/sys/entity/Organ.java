/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.entity;


import com.eryansky.common.model.TreeNode;
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
import com.eryansky.modules.sys._enum.OrganType;
import com.eryansky.modules.sys.utils.UserUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Collections;
import java.util.List;

/**
 * 组织组机构实体类.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_sys_organ")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Delete(propertyName = "status", type = PropertyType.S)
@JsonFilter(" ")
//jackson标记不生成json对象的属性
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class Organ extends DataEntity<Organ> {

    /**
     * 机构名称
     */
    private String name;
    /**
     * 机构简称
     */
    private String shortName;
    /**
     * 机构编码
     */
    private String code;
    /**
     * 机构系统编码
     */
    private String sysCode;
    /**
     * 机构类型 {@link OrganType}
     */
    private Integer type ;
    /**
     * 地址
     */
    private String address;
    /**
     * 父级组织机构
     */
    private Organ parent;
    /**
     * 上级ID 以","分割 第一级ID,第二级ID,...
     */
    private String parentIds;
    /**
     * 子级组织机构
     */
    private List<Organ> subOrgans = Lists.newArrayList();
    /**
     * 分管领导
     */
    private String superManagerUserId;
    /**
     * 机构负责人
     */
    private String managerUserId;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 电话号码
     */
    private String phone;
    /**
     * 传真号
     */
    private String fax;
    /**
     * 排序
     */
    private Integer orderNo;
    /**
     * 机构用户
     */
    private List<User> users = Lists.newArrayList();


    public Organ() {
        super();
    }

    public Organ(String id) {
        super(id);
    }

    public Organ(String id, String name) {
        super(id);
        this.name = name;
    }

    @PrePersist
    @Override
    public void prePersist() {
        super.prePersist();
        syncParentIds();
    }

    @PreUpdate
    @Override
    public void preUpdate() {
        super.preUpdate();
        syncParentIds();
    }

    @Column(nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(length = 64)
    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    @Column(length = 64)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 2)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(length = 36)
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(length = 64)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(length = 64)
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @Column(length = 36)
    public String getManagerUserId() {
        return managerUserId;
    }

    public void setManagerUserId(String managerUserId) {
        this.managerUserId = managerUserId;
    }

    /**
     * 分管领导ID
     * @return
     */
    @Column(length = 36)
    public String getSuperManagerUserId() {
        return superManagerUserId;
    }

    public void setSuperManagerUserId(String superManagerUserId) {
        this.superManagerUserId = superManagerUserId;
    }


    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_user_organ", joinColumns = {@JoinColumn(name = "organ_id")}, inverseJoinColumns = {@JoinColumn(name = "user_id")})
    @Fetch(FetchMode.SUBSELECT)
    @Where(clause = "status = "+ STATUS_NORMAL)
    @OrderBy("orderNo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }


    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "parent_id")
    @NotFound(action = NotFoundAction.IGNORE)
    public Organ getParent() {
        return parent;
    }

    public void setParent(Organ parent) {
        this.parent = parent;
    }

    /**
     * 设置上级节点
     */
    public void syncParentIds() {
        StringBuffer sb = new StringBuffer();
        Organ parent = this.getParent();
        if(parent != null){
            sb.append(parent.getParentIds())
                .append(parent.getId()).append(",");
        }else{
            sb.append("0,");
        }

        this.parentIds = sb.toString();
    }

    /**
     * 设置上级节点 (全部匹配)
     */
    public void syncParentIds2() {
        StringBuffer sb = new StringBuffer();
        sb.append("0,");
        Organ _parent = this.getParent();
        List<String> pIds = Lists.newArrayList();
        while (_parent != null && StringUtils.isNotBlank(_parent.getId())) {
            pIds.add(_parent.getId());
            _parent = _parent.getParent();
        }
        Collections.reverse(pIds);
        for(String id:pIds){
            sb.append(id).append(",");
        }

        this.parentIds = sb.toString();
    }

    @Column(length = 2000)
    public String getParentIds() {
        return this.parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = {CascadeType.REMOVE})
    @Where(clause = "status <> "+ STATUS_DELETE)
    @OrderBy("orderNo")
    public List<Organ> getSubOrgans() {
        return subOrgans;
    }

    public void setSubOrgans(List<Organ> subOrgans) {
        this.subOrgans = subOrgans;
    }

    @Transient
    public String get_parentId() {
        if (parent != null) {
            return parent.getId();
        }
        return null;
    }

    /**
     * 机构类型 显示
     */
    @Transient
    public String getTypeView() {
        OrganType r = OrganType.getOrganType(type);
        String str = "";
        if (r != null) {
            str = r.getDescription();
        }
        return str;
    }

    /**
     * 组织机构拥有的用户id字符串，多个用户id以","分割
     *
     * @return
     */
    @Transient
    @SuppressWarnings("unchecked")
    public List<String> getUserIds() {
        if (Collections3.isNotEmpty(users)) {
            return ConvertUtils.convertElementPropertyToList(users, "id");
        }
        return null;
    }

    /**
     * 组织机构拥有的用户姓名字符串，多个用户登录名以","分割
     *
     * @return
     */
    @Transient
    public String getUserNames() {
        return ConvertUtils.convertElementPropertyToString(users, "name", ", ");
    }



    /**
     * 部门主管姓名
     * @return
     */
    @Transient
    public String getManagerUserName() {
        if (Collections3.isNotEmpty(users)) {
            for (User user : users) {
                if (managerUserId != null && user.getId().equals(managerUserId)) {
                    return user.getName();
                }
            }
        }

        return null;
    }


    /**
     * 分管领导名称
     * @return
     */
    @Transient
    public String getSuperManagerUserName() {
        return UserUtils.getUserName(superManagerUserId);
    }


    /**
     * 得到机构等级 0,1...
     *
     * @return
     */
    @Transient
    public Integer getGrade() {
        int grade = 0;
        Organ parent = this.getParent();
        while (parent != null) {
            grade++;
            parent = parent.getParent();
        }
        return grade;
    }


    /**
     * 机构下默认用户
     * @return
     */
    @JsonIgnore
    @Transient
    public List<User> getDefautUsers() {
        List<User> list = Lists.newArrayList();
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getDefaultOrganId() != null && user.getDefaultOrganId().equals(id)) {
                list.add(user);
            }
        }
        return list;
    }

    /**
     * Treegrid 关闭状态设置
     * @return
     */
    @Transient
    public String getState(){
        if(Collections3.isNotEmpty(this.getSubOrgans())){
            return TreeNode.STATE_CLOASED;
        }
        return TreeNode.STATE_OPEN;
    }

    /**
     * 所在部门
     * @return
     */
    @JsonIgnore
    @Transient
    public Organ getOffice() {
        Organ currentOrgan = this;
        while (currentOrgan != null && !OrganType.department.getValue().equals(currentOrgan.getType())) {
            currentOrgan = currentOrgan.getParent();
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
        return this.getId();
    }

    /**
     * 所在机构
     * @return
     */
    @JsonIgnore
    @Transient
    public Organ getCompany() {
        Organ currentOrgan = this;
        while (currentOrgan != null && !OrganType.organ.getValue().equals(currentOrgan.getType())) {
            currentOrgan = currentOrgan.getParent();
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
        return this.id;
    }
}