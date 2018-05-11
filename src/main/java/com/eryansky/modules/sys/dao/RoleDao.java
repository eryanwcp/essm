/**
*  Copyright (c) 2012-2018 http://www.eryansky.com
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*/
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.entity.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.persistence.CrudDao;

import com.eryansky.modules.sys.mapper.Role;

import java.util.List;

/**
 * 角色表
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@MyBatisDao
public interface RoleDao extends CrudDao<Role> {

    List<Role> findRolesByIds(Parameter parameter);

    List<Role> findOrganRolesAndSystemRoles(Parameter parameter);

    Role getByCode(Parameter parameter);

    List<Role> findRolesByOrganId(Parameter parameter);

    List<Role> findRolesByUserId(Parameter parameter);
    List<String> findRoleIdsByUserId(Parameter parameter);

    /**
     * 删除角色机构关联信息
     * @param parameter id:角色ID
     */
    int deleteRoleOrgansByRoleId(Parameter parameter);

    /**
     * 插入角色机构关联信息
     * @param parameter id:角色ID ids:机构IDS
     */
    int insertRoleOrgans(Parameter parameter);



    /**
     * 删除角色用户关联信息
     * @param parameter id:角色ID
     */
    int deleteRoleUsersByRoleId(Parameter parameter);

    /**
     * 删除角色用户关联信息
     * @param parameter id:角色ID ids:用户IDS
     */
    int deleteRoleUsersByRoleIdANDUserIds(Parameter parameter);

    /**
     * 插入角色用户关联信息
     * @param parameter id:角色ID ids:用户IDS
     */
    int insertRoleUsers(Parameter parameter);



    /**
     * 删除角色资源关联信息
     * @param parameter id:角色ID
     */
    int deleteRoleResourcesByRoleId(Parameter parameter);

    /**
     * 插入角色资源关联信息
     * @param parameter id:角色ID ids:资源IDS
     */
    int insertRoleResources(Parameter parameter);



}
