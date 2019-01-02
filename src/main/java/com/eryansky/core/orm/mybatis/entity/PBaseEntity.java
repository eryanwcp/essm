/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.mybatis.entity;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.persistence.AbstractBaseEntity;
import com.eryansky.common.orm.persistence.IUser;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.io.PropertiesLoader;
import com.eryansky.core.security.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Map;

/**
 * Entity支持类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2019-01-02
 */
public abstract class PBaseEntity<T, PK extends Serializable> extends AbstractBaseEntity<T,PK> {


    public PBaseEntity() {
    }

    public PBaseEntity(PK id) {
        super(id);
    }


    @JsonIgnore
    @XmlTransient
    @Override
    public IUser getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    @Override
    public void prePersist() {
        // 不限制ID为UUID，调用setIsNewRecord()使用自定义ID
        if (!this.isNewRecord){
            if(id instanceof String){
                setId((PK)Identities.uuid2());
            }if(id instanceof Long){
                setId((PK)Identities.uuid3());
            }
        }
    }

    @Override
    public void preUpdate() {

    }

    @JsonIgnore
    @Override
    public boolean getIsNewRecord() {
        return super.getIsNewRecord();
    }

    @JsonIgnore
    @XmlTransient
    @Override
    public Page<T> getEntityPage() {
        return super.getEntityPage();
    }

    @JsonIgnore
    @Override
    public PropertiesLoader getGlobal() {
        return super.getGlobal();
    }

    @JsonIgnore
    @XmlTransient
    @Override
    public Map<String, String> getSqlMap() {
        return super.getSqlMap();
    }

    @JsonIgnore
    @Override
    public String getDbName() {
        return super.getDbName();
    }
}
