/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.eryansky.utils.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.eryansky.modules.sys.mapper.Role;
import com.eryansky.modules.sys.dao.RoleDao;
import com.eryansky.core.orm.mybatis.service.CrudService;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

/**
 * 角色表 service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@Service
public class RoleService extends CrudService<RoleDao, Role> {

    @Autowired
    private RoleDao dao;

    /**
     * 删除角色.
     * <br>删除角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    public void deleteByIds(Collection<String> ids) {
        if(Collections3.isNotEmpty(ids)){
            for(String id :ids){
                deleteById(id);
            }
        }else{
            logger.warn("参数[ids]为空.");
        }
    }

    /**
     * 删除角色.
     * <br>删除角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    public void deleteById(String id) {
        logger.debug("清空缓存:{}", CacheConstants.ROLE_ALL_CACHE
                +","+CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE);
        super.delete(new Role(id));
    }

    /**
     * 新增或修改角色.
     * <br>修改角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    public void saveRole(Role entity) {
        logger.debug("清空缓存:{}", CacheConstants.ROLE_ALL_CACHE
                +","+CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE);
        Assert.notNull(entity, "参数[entity]为空!");
        super.save(entity);
        saveRoleOrgans(entity.getId(),entity.getOrganIds());
    }

    /**
     * 根据角色编码查找
     * @param code 角色编
     * @return
     */
    public Role getByCode(String code) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("code",code);
        return dao.getByCode(parameter);
    }

    /**
     * 查找所有
     * @return
     */
    @Cacheable(value = { CacheConstants.ROLE_ALL_CACHE})
    public List<Role> findAll() {
        List<Role> list = findList(new Role());
        logger.debug("缓存:{}",CacheConstants.ROLE_ALL_CACHE);
        return list;
    }

    /**
     * 查找机构角色
     * @param organId 机构ID
     * @return
     */
    public List<Role> findRolesByOrganId(String organId) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findRolesByOrganId(parameter);
    }

    /**
     * 查找用户角色
     * @param userId 用户ID
     * @return
     */
    public List<Role> findRolesByUserId(String userId) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("userId",userId);
        return dao.findRolesByUserId(parameter);
    }


    /**
     * 查找用户角色IDS
     * @param userId 用户ID
     * @return
     */
    public List<String> findRoleIdsByUserId(String userId) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("userId",userId);
        return dao.findRoleIdsByUserId(parameter);
    }


    /**
     * 查找机构角色以及系统角色
     * @param organId 机构ID
     * @return
     */
    public List<Role> findOrganRolesAndSystemRoles(String organId) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("organId",organId);
        parameter.put("isSystem", YesOrNo.YES.getValue());
        return dao.findOrganRolesAndSystemRoles(parameter);
    }

    /**
     * 根据ID查找
     * @param roleIds 角色ID集合
     * @return
     */
    public List<Role> findRolesByIds(List<String> roleIds) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("ids",roleIds);
        return dao.findRolesByIds(parameter);
    }


    /**
     * 保存角色机构关联信息
     * 保存之前先删除原有
     * @param id 角色ID
     * @param ids 机构IDS
     */
    public void saveRoleOrgans(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deleteRoleOrgansByRoleId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertRoleOrgans(parameter);
        }
    }

    /**
     * 查找角色关联机构
     * @param id
     */
    public List<String> findRoleOrganIds(String id){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        return dao.findRoleOrganIds(parameter);
    }




    /**
     * 保存角色用户关联信息
     * 保存之前先删除原有
     * @param id 角色ID
     * @param ids 用户IDS
     */
    public void saveRoleUsers(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deleteRoleUsersByRoleId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertRoleUsers(parameter);
        }
    }

    /**
     * 插入指定角色用户关联信息
     * @param id 角色ID
     * @param ids 用户IDS
     */
    public void insertRoleUsers(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        if(Collections3.isNotEmpty(ids)){
            dao.insertRoleUsers(parameter);
        }
    }

    /**
     * 删除指定角色用户关联信息
     * @param id 角色ID
     * @param ids 用户IDS
     */
    public void deleteRoleUsersByRoleIdANDUserIds(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        if(Collections3.isNotEmpty(ids)){
            dao.deleteRoleUsersByRoleIdANDUserIds(parameter);
        }
    }



    /**
     * 保存角色资源关联信息
     * 保存之前先删除原有
     * @param id 岗位ID
     * @param ids 资源IDS
     */
    public void saveRoleResources(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deleteRoleResourcesByRoleId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertRoleResources(parameter);
        }
    }



}
