/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.modules.disk.entity.UserStorage;
import com.eryansky.utils.AppConstants;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户云盘存储空间配置 管理
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-24
 */
@Service
public class UserStorageManager extends EntityManager<UserStorage, String> {

    private HibernateDao<UserStorage, String> userStorageDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        userStorageDao = new HibernateDao<UserStorage, String>(sessionFactory, UserStorage.class);
    }

    @Override
    protected HibernateDao<UserStorage, String> getEntityDao() {
        return userStorageDao;
    }

    /**
     * 查找用户云盘存储空间配置信息
     * @param userId 用户ID
     * @return
     */
    public UserStorage getUserStorage(String userId){
        Validate.notNull(userId, "参数[userId]不能为null.");
        StringBuffer hql = new StringBuffer();
        hql.append("from UserStorage e where e.userId = :p1");
        Parameter parameter = new Parameter(userId);
        List<UserStorage> list =  getEntityDao().find(hql.toString(),parameter);
        return list.isEmpty() ? null:list.get(0);
    }

    /**
     * 查找用户可用存储字节数
     * @param userId 用户ID
     * @return
     */
    public long getUserAvaiableStorage(String userId) {
        Validate.notNull(userId, "参数[userId]不能为null.");
        UserStorage userStorage = getUserStorage(userId);
        int diskUserLimitSize = AppConstants.getDiskUserLimitSize().intValue();
        if (userStorage != null && userStorage.getLimitSize() != null) {
            diskUserLimitSize = userStorage.getLimitSize();
        }
        return Long.valueOf(diskUserLimitSize) * 1024L * 1024L;
    }
}
