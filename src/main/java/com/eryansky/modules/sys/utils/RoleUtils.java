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
import com.eryansky.modules.sys.mapper.Role;
import com.eryansky.modules.sys.service.RoleService;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-25
 */
public class RoleUtils {

    private static RoleService roleService = SpringContextHolder.getBean(RoleService.class);

    /**
     * 根据角色ID查找角色名称
     * @param roleId 角色ID
     * @return
     */
    public static String getRoleName(String roleId){
        if(StringUtils.isNotBlank(roleId)){
            Role Role = roleService.get(roleId);
            if(Role != null){
                return Role.getName();
            }
        }
        return null;
    }

    /**
     * @param userId 用户ID
     * @return
     */
    public static List<Role> findRolesByUserId(String userId){
        return roleService.findRolesByUserId(userId);
    }


    /**
     * 根据角色ID查找角色名称集合
     * @param roleIds 角色ID集合
     * @return
     */
    public static String getRoleNames(List<String> roleIds){
        if(Collections3.isNotEmpty(roleIds)){
            List<Role> list = roleService.findRolesByIds(roleIds);
            return ConvertUtils.convertElementPropertyToString(list, "name", ", ");
        }
        return null;
    }
}
