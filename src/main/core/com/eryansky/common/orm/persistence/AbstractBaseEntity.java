/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.persistence;

import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.SysConstants;
import com.eryansky.common.utils.io.PropertiesLoader;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * 抽象基类
 * @param <T>
 * @param <PK>
 */
public abstract class AbstractBaseEntity<T, PK extends Serializable> implements Serializable{

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String TIME_SHORT_FORMAT = "HH:mm";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_SHORT_FORMAT = "yyyy-MM-dd HH:mm";

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";

    public static final String TIMEZONE = "GMT+08:00";

    // 显示/隐藏
    public static final String SHOW = "1";
    public static final String HIDE = "0";

    // 是/否
    public static final String YES = "1";
    public static final String NO = "0";

    /**
     * 实体编号（唯一标识）
     */
    protected PK id;
    /**
     * 当前用户
     */
    protected IUser currentUser;
    /**
     * 数据库类型
     */
    protected String dbName;

    /**
     * 当前实体分页对象
     */
    protected Page<T> entityPage;

    /**
     * 自定义SQL（SQL标识，SQL内容）
     */
    protected Map<String, String> sqlMap;

    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     */
    protected boolean isNewRecord = false;

    public AbstractBaseEntity() {
    }

    public AbstractBaseEntity(PK id) {
        this.id = id;
    }

    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }

    public abstract IUser getCurrentUser();

    public void setCurrentUser(IUser currentUser) {
        this.currentUser = currentUser;
    }

    public Page<T> getEntityPage() {
        if (entityPage == null) {
            entityPage = new Page<T>();
        }
        return entityPage;
    }

    public Page<T> setEntityPage(Page<T> entityPage) {
        this.entityPage = entityPage;
        return entityPage;
    }



    public Map<String, String> getSqlMap() {
        if (sqlMap == null) {
            sqlMap = Maps.newHashMap();
        }
        return sqlMap;
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    /**
     * 插入之前执行方法，子类实现
     */
    public abstract void prePersist();

    /**
     * 更新之前执行方法，子类实现
     */
    public abstract void preUpdate();

    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     *
     * @return
     */
    public boolean getIsNewRecord() {
        boolean flag = (id == null);
        if(id != null && id instanceof String){
            flag = StringUtils.isBlank((String) id);
        }

        return isNewRecord || flag;
    }

    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     */
    public void setIsNewRecord(boolean isNewRecord) {
        this.isNewRecord = isNewRecord;
    }

    /**
     * 全局变量对象
     */
    public PropertiesLoader getGlobal() {
        return SysConstants.getAppConfig();
    }

    /**
     * 获取数据库名称
     */
    public String getDbName() {
        if(StringUtils.isBlank(dbName)){
            return SysConstants.getJdbcType();
        }
        return dbName;
    }

    /**
     * 设置数据库类型
     * @param dbName
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        AbstractBaseEntity that = (AbstractBaseEntity) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    @Override
    public String toString() {
        return JsonMapper.getInstance().toJson(this);
    }

}

