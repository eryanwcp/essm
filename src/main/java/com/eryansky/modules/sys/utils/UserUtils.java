/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.entity.UserPassword;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.modules.sys.service.UserPasswordManager;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-25
 */
public class UserUtils {

    private static UserManager userManager = SpringContextHolder.getBean(UserManager.class);
    private static UserPasswordManager userPasswordManager = SpringContextHolder.getBean(UserPasswordManager.class);

    /**
     * 根据userId查找用户姓名
     * @param userId 用户ID
     * @return
     */
    public static User getUser(String userId){
        if(StringUtils.isNotBlank(userId)) {
            User user = userManager.loadById(userId);
            return user;
        }
        return null;
    }

    /**
     * 根据loginName查找用户
     * @param loginName 用户账号
     * @return
     */
    public static User getUserByLoginName(String loginName){
        if(StringUtils.isNotBlank(loginName)) {
            User user = userManager.getUserByLoginName(loginName);
            return user;
        }
        return null;
    }

    /**
     * 根据userId查找用户姓名
     * @param userId 用户ID
     * @return
     */
    public static String getUserName(String userId){
        User user = getUser(userId);
        if(user != null){
            return user.getName();
        }
        return null;
    }

    /**
     * 根据userId查找用户登录名
     * @param userId 用户ID
     * @return
     */
    public static String getLoginName(String userId){
        User user = getUser(userId);
        if(user != null){
            return user.getLoginName();
        }
        return null;
    }

    /**
     * 根据userId查找用户姓名
     * @param userIds 用户ID集合
     * @return
     */
    public static String getUserNames(List<String> userIds){
        if(Collections3.isNotEmpty(userIds)){
            List<User> list = userManager.findUsersByIds(userIds);
            return ConvertUtils.convertElementPropertyToString(list, "name", ",");
        }
        return null;
    }

    public static UserPassword addUserPasswordUpdate(User user){
        UserPassword userPassword = new UserPassword(user.getId(),user.getPassword());
        userPassword.setOriginalPassword(user.getOriginalPassword());
        userPasswordManager.save(userPassword);
        return userPassword;
    }

    public static UserPassword addUserPasswordUpdate(String userId,String password,String originalPassword){
        UserPassword userPassword = new UserPassword(userId,password);
        userPassword.setOriginalPassword(originalPassword);
        userPasswordManager.save(userPassword);
        return userPassword;
    }


    /**
     * 修改用户密码 批量
     * @param userIds 用户ID集合
     * @param password 密码(未加密)
     */
    public static void updateUserPassword(List<String> userIds,String password){
        userManager.updateUserPassword(userIds,password);
    }

}
