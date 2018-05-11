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
import com.eryansky.modules.sys.mapper.OrganExtend;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.mapper.UserPassword;
import com.eryansky.modules.sys.service.UserPasswordService;
import com.eryansky.modules.sys.service.UserService;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-25
 */
public class UserUtils {

    private static UserService userService = SpringContextHolder.getBean(UserService.class);
    private static UserPasswordService userPasswordService = SpringContextHolder.getBean(UserPasswordService.class);

    /**
     * 根据userId查找用户姓名
     * @param userId 用户ID
     * @return
     */
    public static User getUser(String userId){
        if(StringUtils.isNotBlank(userId)) {
            User user = userService.get(userId);
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
            User user = userService.getUserByLoginName(loginName);
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


    public static String getCompanyId(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(userId);
        if(organExtend != null){
            return organExtend.getCompanyId();
        }
        return null;
    }

    public static String getCompanyCode(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        OrganExtend company = OrganUtils.getCompanyByUserId(userId);
        if(company != null){
            return company.getCode();
        }
        return null;
    }

    public static String getCompanyName(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        OrganExtend company = OrganUtils.getCompanyByUserId(userId);
        if(company != null){
            return company.getName();
        }
        return null;
    }


    public static String getDefaultOrganId(String userId){
        User user = getUser(userId);
        if(user != null){
            return user.getDefaultOrganId();
        }
        return null;
    }

    public static String getDefaultOrganName(String userId){
        OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(userId);
        if(organExtend != null){
            return organExtend.getName();
        }
        return null;
    }

    public static UserPassword addUserPasswordUpdate(User user){
        UserPassword userPassword = new UserPassword(user.getId(),user.getPassword());
        userPassword.setOriginalPassword(user.getOriginalPassword());
        userPasswordService.save(userPassword);
        return userPassword;
    }

    public static UserPassword addUserPasswordUpdate(String userId,String password,String originalPassword){
        UserPassword userPassword = new UserPassword(userId,password);
        userPassword.setOriginalPassword(originalPassword);
        userPasswordService.save(userPassword);
        return userPassword;
    }


    /**
     * 修改用户密码 批量
     * @param userIds 用户ID集合
     * @param password 密码(未加密)
     */
    public static void updateUserPassword(List<String> userIds,String password){
        userService.updateUserPassword(userIds,password);
    }

}
