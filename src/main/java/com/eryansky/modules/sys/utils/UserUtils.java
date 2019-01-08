/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
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
import com.eryansky.modules.sys.service.*;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-25
 */
public class UserUtils {

     /**
     * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
     */
    public static final class Static {
         private static UserService userService = SpringContextHolder.getBean(UserService.class);
         private static UserPasswordService userPasswordService = SpringContextHolder.getBean(UserPasswordService.class);
     }

    /**
     * 根据userId查找用户
     * @param userId 用户ID
     * @return
     */
    public static User getUser(String userId){
        if(StringUtils.isNotBlank(userId)) {
            return Static.userService.get(userId);
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
            return Static.userService.getUserByLoginName(loginName);
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
     * 根据userId查找用户所属单位ID
     * @param userId 用户ID
     * @return
     */
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

    /**
     * 根据userId查找用户所属单位ID
     * @param userId 用户ID
     * @return
     */
    public static String getHomeCompanyId(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(userId);
        if(organExtend != null){
            return organExtend.getHomeCompanyId();
        }
        return null;
    }

    /**
     * 根据userId查找用户所属单位编码
     * @param userId 用户ID
     * @return
     */
    public static String getCompanyCode(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(userId);
        if(organExtend != null){
            return organExtend.getCompanyCode();
        }
        return null;
    }

    /**
     * 根据userId查找用户所属单位编码
     * @param userId 用户ID
     * @return
     */
    public static String getHomeCompanyCode(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(userId);
        if(organExtend != null){
            return organExtend.getHomeCompanyCode();
        }
        return null;
    }

    /**
     * 根据userId查找用户所属单位编码
     * @param userId 用户ID
     * @return
     */
    public static String getOrganSysCode(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(userId);
        if(organExtend != null){
            return organExtend.getSysCode();
        }
        return null;
    }

    /**
     * 根据userId查找用户所属单位名称
     * @param userId 用户ID
     * @return
     */
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

    /**
     * 根据userId查找用户所属机构ID
     * @param userId 用户ID
     * @return
     */
    public static String getDefaultOrganId(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        User user = getUser(userId);
        if(user != null){
            return user.getDefaultOrganId();
        }
        return null;
    }

    /**
     * 根据userId查找用户所属机构名称
     * @param userId 用户ID
     * @return
     */
    public static String getDefaultOrganName(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        OrganExtend organExtend = OrganUtils.getOrganExtendByUserId(userId);
        if(organExtend != null){
            return organExtend.getName();
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
            List<User> list = Static.userService.findUsersByIds(userIds);
            return ConvertUtils.convertElementPropertyToString(list, "name", ",");
        }
        return null;
    }

    /**
     * 得到超级用户
     * @return
     */
    public static User getSuperUser() {
        User superUser = Static.userService.getSuperUser();
        return superUser;
    }

    /**
     * 更新用户密码
     * @param user 用户
     * @return
     */
    public static UserPassword addUserPasswordUpdate(User user){
        UserPassword userPassword = new UserPassword(user.getId(),user.getPassword());
        userPassword.setOriginalPassword(user.getOriginalPassword());
        Static.userPasswordService.save(userPassword);
        return userPassword;
    }

    /**
     * 更新用户密码
     * @param userId 用户
     * @param password 密码
     * @param originalPassword 原始密码
     * @return
     */
    public static UserPassword addUserPasswordUpdate(String userId,String password,String originalPassword){
        UserPassword userPassword = new UserPassword(userId,password);
        userPassword.setOriginalPassword(originalPassword);
        Static.userPasswordService.save(userPassword);
        return userPassword;
    }


    /**
     * 修改用户密码 批量
     * @param userIds 用户ID集合
     * @param password 密码(未加密)
     */
    public static void updateUserPassword(List<String> userIds,String password){
        Static.userService.updateUserPassword(userIds,password);
    }

}
