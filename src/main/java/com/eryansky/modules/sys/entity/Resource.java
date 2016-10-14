/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.entity;

import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.eryansky.modules.sys._enum.ResourceType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

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
import java.util.ArrayList;
import java.util.List;

/**
 * 受保护的资源菜案Resource.
 * Author 尔演&Eryan eryanwcp@gmail.com
 * Date 2013-3-21 上午12:27:49
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_sys_resource")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Delete(propertyName = "status", type = PropertyType.S)
//jackson标记不生成json对象的属性
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class Resource extends DataEntity<Resource> {

    /**
     * 资源名称
     */
    private String name;
    /**
     * 资源编码
     */
    private String code;
    /**
     * 资源url地址
     */
    private String url;
    /**
     * 排序
     */
    private Integer orderNo;
    /**
     * 图标
     */
    private String iconCls;
    /**
     * 应用程序图标地址
     */
    private String icon;
    /**
     * 父级Resource
     */
    private Resource parent;
    /**
     * 上级ID 以","分割 第一级ID,第二级ID,...
     */
    private String parentIds;
    /**
     * 标记url
     */
    private String markUrl;
    /**
     * 资源类型 {@link ResourceType}
     */
    private String type = ResourceType.menu.getValue();
    /**
     * 有序的关联对象集合
     */
    private List<Role> roles = Lists.newArrayList();
    /**
     * 有序的关联对象集合
     */
    private List<User> users = Lists.newArrayList();
    /**
     * 子Resource集合
     */
    private List<Resource> subResources = Lists.newArrayList();

    public Resource() {
        super();
    }

    public Resource(String id) {
        super(id);
    }

    public Resource(String id, String name) {
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

    @NotBlank(message = "{resource_name.notblank}")
    @Length(max = 20, message = "{resource_name.length}")
    @Column(length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 36)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(length = 64)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(length = 512)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @Column(length = 255)
    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    @Column(length = 255)
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    @Column(length = 2048)
    public String getMarkUrl() {
        return markUrl;
    }

    public void setMarkUrl(String markUrl) {
        this.markUrl = markUrl;
    }

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "parent_id")
    @NotFound(action = NotFoundAction.IGNORE)
    public Resource getParent() {
        return parent;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    /**
     * 设置上级节点
     */
    public void syncParentIds() {
        StringBuffer sb = new StringBuffer();
        Resource parent = this.getParent();
        if(parent != null){
            sb.append(parent.getParentIds())
                    .append(parent.getId()).append(",");
        }else{
            sb.append("0,");
        }

//        List<String> pIds = Lists.newArrayList();
//        while (parent != null && StringUtils.isNotBlank(parent.getId())) {
//            pIds.add(parent.getId());
//            parent = parent.getParentResource();
//        }
//        Collections.reverse(pIds);
//        for(String id:pIds){
//            sb.append(id).append(",");
//        }

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
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_role_resource", joinColumns = {@JoinColumn(name = "resource_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    @Where(clause = "status = "+ STATUS_NORMAL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "t_sys_user_resource", joinColumns = {@JoinColumn(name = "resource_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = {CascadeType.REMOVE})
    @Where(clause = "status = "+ STATUS_NORMAL)
    @OrderBy("orderNo asc")
    public List<Resource> getSubResources() {
        return subResources;
    }

    public void setSubResources(List<Resource> subResources) {
        this.subResources = subResources;
    }

    @JsonIgnore
    @Transient
    public List<Resource> getNavigation() {
        ArrayList<Resource> arrayList = new ArrayList<Resource>();
        Resource resource = this;
        arrayList.add(resource);
        while (null != resource.parent) {
            resource = resource.parent;
            arrayList.add(0, resource);
        }
        return arrayList;
    }

    @Transient
    public String get_parentId() {
        if (parent != null) {
            return parent.getId();
        }
        return null;
    }

    /**
     * 资源类型描述
     */
    @Transient
    public String getTypeView() {
        ResourceType r = ResourceType.getByValue(type);
        String str = "";
        if (r != null) {
            str = r.getDescription();
        }
        return str;
    }

}