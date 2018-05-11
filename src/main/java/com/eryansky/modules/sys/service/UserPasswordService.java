/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eryansky.modules.sys.mapper.UserPassword;
import com.eryansky.modules.sys.dao.UserPasswordDao;
import com.eryansky.core.orm.mybatis.service.CrudService;

import java.util.List;

/**
 * 用户密码修改记录 service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@Service
@Transactional(readOnly = true)
public class UserPasswordService extends CrudService<UserPasswordDao, UserPassword> {

    @Autowired
    private UserPasswordDao dao;


    public UserPassword getLatestUserPasswordByUserId(String userId){
        List<UserPassword> userPasswords = getUserPasswordsByUserId(userId,1);
        return Collections3.isEmpty(userPasswords) ? null:userPasswords.get(0);
    }

    /**
     * 查询某个用户ID秘密修改记录
     * <br/>根据修改时间 降序排列
     * @param userId 用户ID
     * @param maxSize 最大记录数 为null是查询所有
     * @return
     */
    public List<UserPassword> getUserPasswordsByUserId(String userId, Integer maxSize){
        Page<UserPassword> page = new Page<UserPassword>();
        if(maxSize != null){
            page.setPageSize(maxSize);
        }
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("userId",userId);
        parameter.put(BaseInterceptor.PAGE,page);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        return page.setResult(dao.findByUserId(parameter)).getResult();
    }

    /**
     * 新增修改密码记录
     * @param user
     * @return
     */
    public UserPassword addUserPasswordUpdate(User user){
        UserPassword userPassword = new UserPassword(user.getId(),user.getPassword());
        userPassword.setOriginalPassword(user.getOriginalPassword());
        this.save(userPassword);
        return userPassword;
    }
}
