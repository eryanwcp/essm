/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.eryansky.modules.sys.entity.Role;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.utils.CacheConstants;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 角色Role管理 Service层实现类.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:15:26
 */
@Service
public class RoleManager extends EntityManager<Role, String> {

    @Autowired
    private UserManager userManager;

    private HibernateDao<Role, String> roleDao;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        roleDao = new HibernateDao<Role, String>(sessionFactory,
                Role.class);
    }

    @Override
    protected HibernateDao<Role, String> getEntityDao() {
        return roleDao;
    }

    /**
     * 删除角色.
     * <br>删除角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    @Override
    public void deleteByIds(List<String> ids) throws DaoException,
            SystemException, ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.ROLE_ALL_CACHE
                +","+CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE);
        super.deleteByIds(ids);
    }
    /**
     * 新增或修改角色.
     * <br>修改角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    public void saveOrUpdate(Role entity) throws DaoException,SystemException,ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.ROLE_ALL_CACHE
                +","+CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE);
        Assert.notNull(entity, "参数[entity]为空!");
        roleDao.saveOrUpdate(entity);
        logger.warn("保存色Role:{}",entity.getId());
    }

    /**
     * 新增或修改角色.
     * <br>修改角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    public void merge(Role entity) throws DaoException,SystemException,ServiceException {
        Assert.notNull(entity, "参数[entity]为空!");
        roleDao.merge(entity);
        logger.warn("保存色Role:{}",entity.getId());
    }

    /**
     * 新增或修改角色.
     * @param entity
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE},allEntries = true)
    @Override
    public void saveEntity(Role entity) throws DaoException, SystemException, ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.ROLE_ALL_CACHE
                +","+CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE);
        super.saveEntity(entity);
    }

    /**
     * 根据角色编码查找
     * @param code 角色编
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public Role getByCode(String code) throws DaoException,SystemException,ServiceException {
        return this.findUniqueBy("code",code);
    }

    /**
     * 查找所有
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @Cacheable(value = { CacheConstants.ROLE_ALL_CACHE})
    public List<Role> getAll() throws DaoException,SystemException,ServiceException {
        List<Role> list = super.getAll();
        logger.debug("缓存:{}",CacheConstants.ROLE_ALL_CACHE);
        return list;
    }

    /**
     * 查找机构角色
     * @param organId 机构ID
     * @return
     */
    public List<Role> findRolesByOrganId(String organId) {
        Parameter parameter = new Parameter(Role.STATUS_NORMAL,organId);
        return getEntityDao().find("select r from Role r where r.status = :p1 and (r.organId = :p2) ",parameter);
    }


    /**
     * 查找机构角色以及系统角色
     * @param organId 机构ID
     * @return
     */
    public List<Role> findOrganRolesAndSystemRoles(String organId) {
        Parameter parameter = new Parameter(Role.STATUS_NORMAL,organId, YesOrNo.YES.getValue());
        return getEntityDao().find("select r from Role r where r.status = :p1 and (r.organId = :p2 or r.isSystem = :p3) ",parameter);
    }

    /**
     * 根据ID查找
     * @param roleIds 角色ID集合
     * @return
     */
    public List<Role> findRolesByIds(List<String> roleIds) {
        Parameter parameter = new Parameter(roleIds);
        return getEntityDao().find("from Role r where r.id in (:p1)",parameter);
    }


    /**
     * 查找角色用户
     * @param roleId 角色ID
     * @return
     */
    public List<User> findRoleUsers(String roleId) {
        Parameter parameter = new Parameter(roleId);
        return getEntityDao().find("select u from User u left join u.roles ur where ur.id = :p1",parameter);
    }

    /**
     * 角色用户（分页查询）
     * @param page
     * @param roleId
     * @param name
     * @return
     */
    public Page<User> findPageRoleUsers(Page<User> page,String roleId,String name) {
        Parameter parameter = new Parameter(roleId);
        StringBuffer hql = new StringBuffer();
        hql.append("select u from User u left join u.roles ur where ur.id = :p1");
        if(StringUtils.isNotBlank(name)){
            hql.append(" and u.name like :name");
            parameter.put("name","%"+name+"%");
        }
        return userManager.findPage(page, hql.toString(), parameter);
    }

}
