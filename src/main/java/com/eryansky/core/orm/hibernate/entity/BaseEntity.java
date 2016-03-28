/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.hibernate.entity;

import com.eryansky.common.orm.Page;
import com.eryansky.common.persistence.AbstractBaseEntity;
import com.eryansky.common.persistence.IUser;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.io.PropertiesLoader;
import com.eryansky.core.security.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Map;

/**
 * 自定义UUID基础实体
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-07-06 
 */
@MappedSuperclass
public abstract class BaseEntity<T> extends AbstractBaseEntity<T,String>{


    public BaseEntity() {
    }

    public BaseEntity(String id) {
        super(id);
    }

    @Id
    @Column(updatable = false, length = 36)
    @Override
    public String getId() {
        return super.getId();
    }

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public IUser getCurrentUser() {
        if(currentUser == null){
            currentUser = SecurityUtils.getCurrentUser();
        }
        return currentUser;
    }


    @Transient
    @JsonIgnore
    @Override
    public boolean getIsNewRecord() {
        return super.getIsNewRecord();
    }

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public Page<T> getEntityPage() {
        return super.getEntityPage();
    }

    @Transient
    @JsonIgnore
    @XmlTransient
    @Override
    public Map<String, String> getSqlMap() {
        return super.getSqlMap();
    }

    @Transient
    @JsonIgnore
    @Override
    public PropertiesLoader getGlobal() {
        return super.getGlobal();
    }

    @Transient
    @JsonIgnore
    @Override
    public String getDbName() {
        return super.getDbName();
    }

    @PrePersist
    @Override
    public void prePersist() {
        // 不限制ID为UUID，调用setIsNewRecord()使用自定义ID
        if (!this.isNewRecord){
            setId(Identities.uuid2());
        }
    }

    @PreUpdate
    @Override
    public void preUpdate() {
    }
}
