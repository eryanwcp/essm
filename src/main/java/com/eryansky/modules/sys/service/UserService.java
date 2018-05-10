/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.encode.Encryption;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.security.SecurityType;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.mapper.Post;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.CacheConstants;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.dao.UserDao;
import com.eryansky.core.orm.mybatis.service.CrudService;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 用户表 service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@Service
@Transactional(readOnly = true)
public class UserService extends CrudService<UserDao, User> {

    @Autowired
    private UserDao dao;

    @Autowired
    private OrganService organService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserPasswordService userPasswordService;



    /**
     * 清空缓存 非Manager调用
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void clearCache() {
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                + "," + CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                + "," + CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                + "," + CacheConstants.ORGAN_USER_TREE_CACHE);
    }

    /**
     * 新增或修改角色.
     * <br>修改角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void saveUser(User entity) {
        logger.debug("清空缓存:{}",CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                +","+CacheConstants.ORGAN_USER_TREE_CACHE);
        super.save(entity);
        //保存用户机构信息
//        List<String> oldOrganIds = organService.findOrganIdsByUserId(entity.getId());
        Set<String> organIds = Sets.newHashSet();
        if(StringUtils.isNotBlank(entity.getDefaultOrganId())){
            organIds.add(entity.getDefaultOrganId());
        }
        saveUserOrgans(entity.getId(),organIds);
    }

    /**
     * 新增或修改角色.
     * <br>修改角色的时候 会给角色重新授权菜单 更新导航菜单缓存.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void save(User entity) {
        logger.debug("清空缓存:{}",CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                +","+CacheConstants.ORGAN_USER_TREE_CACHE);
        super.save(entity);
    }


    /**
     * 自定义删除方法.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void deleteByIds(List<String> ids) {
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                + "," + CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                + "," + CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                + "," + CacheConstants.ORGAN_USER_TREE_CACHE);
        if(!Collections3.isEmpty(ids)){
            for(String id :ids){
                User superUser = this.getSuperUser();
                if (id.equals(superUser.getId())) {
                    throw new SystemException("不允许删除超级用户!");
                }
                User user = get(id);
                dao.delete(user);
            }
        }else{
            logger.warn("参数[ids]为空.");
        }
    }

    /**
     * 得到当前登录用户.
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public User getCurrentUser() {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        User user = dao.get(sessionInfo.getUserId());
        return user;
    }

    /**
     * 得到超级用户.
     *
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public User getSuperUser() {
        User superUser = dao.get(User.SUPERUSER_ID);//超级用户ID为1
        if(superUser == null){
            throw new SystemException("系统未设置超级用户.");
        }
        return superUser;
    }


    /**
     * 判断用户是否是超级用户
     * @param userId 用户Id
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public boolean isSuperUser(String userId){
        User superUser = getSuperUser();
        return userId.equals(superUser.getId());
    }

    /**
     * 根据登录名、密码查找用户.
     * <br/>排除已删除的用户
     * @param loginName
     *            登录名
     * @param password
     *            密码
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    public User getUserByLP(String loginName, String password){
        Assert.notNull(loginName, "参数[loginName]为空!");
        Assert.notNull(password, "参数[password]为空!");
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put("loginName",loginName);
        parameter.put("password",password);
        List<User> list = dao.findLoginUser(parameter);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 根据手机号和密码验证
     * @param mobile
     * @param password
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public User getUserByMP(String mobile, String password){
        Assert.notNull(mobile, "参数[mobile]为空!");
        Assert.notNull(password, "参数[password]为空!");
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put("loginName",mobile);
        parameter.put("password",password);
        List<User> list = dao.findLoginUser(parameter);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 根据登录名查找.
     * <br>注：排除已删除的对象
     * @param loginName 登录名
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    public User getUserByLoginName(String loginName){
        Assert.notNull(loginName, "参数[loginName]为空!");
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put("loginName",loginName);
        return dao.getUserByLoginName(parameter);
    }

    /**
     * 获得所有可用用户
     * @return
     */
    public List<User> findAllNormal(){
        User entity = new User();
        return dao.findAllList(entity);
    }

    public List<String> findAllNormalUserIds(){
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        return dao.findAllNormalUserIds(parameter);
    }


    public List<User> findAllNormalWithExclude(List<String> userIds){
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put("userIds",userIds);
        return dao.findAllNormalWithExclude(parameter);
    }

    /**
     *
     * @param userIds 必须包含的用户
     * @param query 查询条件
     * @return
     */
    public List<User> findWithInclude(List<String> userIds, String query){
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,new User().getDbName());
        parameter.put("userIds",userIds);
        parameter.put("query",query);
        return dao.findWithInclude(parameter);

    }

    /**
     *
     * @param page
     * @param organId 机构Id
     * @param query 关键字
     * @param userType 用户类型
     * @return
     */
    public Page<User> findPage(Page<User> page, String organId, String query, String userType) {
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,new User().getDbName());
        parameter.put(BaseInterceptor.PAGE,page);
        parameter.put("organId",organId);
        parameter.put("query", query);
        parameter.put("userType",userType);
        return page.setResult(dao.findQuery(parameter));
    }


    /**
     * 根据机构查询用户信息
     * @param organId 机构Id
     * @param query 关键字
     * @param excludeUserIds 排除的用户IDS
     * @return
     */
    public List<User> findUsersByOrgan(String organId, String query, List<String> excludeUserIds) {
        if(StringUtils.isBlank(organId)){
            return new ArrayList<User>(0);
        }

        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,new User().getDbName());
        parameter.put("organId",organId);
        parameter.put("query",query);
        parameter.put("excludeUserIds",excludeUserIds);
        return dao.findUsersByOrgan(parameter);
    }

    public Page<User> findUsersByOrgan(Page<User> page, String organId, String loginNameOrName, List<String> excludeUserIds) {
        if(StringUtils.isBlank(organId)){
            return page;
        }
        return page.setResult(findUsersByOrgan(organId,loginNameOrName,excludeUserIds));
    }


    /**
     * 获取机构用户
     * @param organId
     * @return
     */
    public List<User> findOrganUsers(String organId) {
        Assert.notNull(organId, "参数[organId]为空!");
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findOrganUsers(parameter);
    }


    /**
     * 获取机构用户
     * @param organId
     * @return
     */
    public List<User> findOrganDefaultUsers(String organId) {
        Assert.notNull(organId, "参数[organId]为空!");
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findOrganDefaultUsers(parameter);
    }





    /**
     * 得到排序字段的最大值.
     *
     * @return 返回排序字段的最大值
     */
    public Integer getMaxSort() {
        Integer max = dao.getMaxSort();
        return max == null ? 0:max;
    }

    /**
     * 批量更新用户 机构信息
     * @param userIds 用户Id集合
     * @param organIds 所所机构ID集合
     * @param defaultOrganId 默认机构
     */
    public void updateUserOrgan(Set<String> userIds,Set<String> organIds, String defaultOrganId){
        if(Collections3.isNotEmpty(userIds)){
            for(String userId:userIds){
                User model = this.get(userId);
                if(model == null){
                    throw new ServiceException("用户["+userId+"]不存在.");
                }
                //保存用户机构信息
                saveUserOrgans(userId,organIds);
                //去除用户已删除机构下的岗位关联信息
                deleteNotInUserOrgansPostsByUserId(userId,organIds);
                //设置默认部门
                model.setDefaultOrganId(defaultOrganId);
                this.save(model);
            }
        }
    }


    /**
     * 设置用户岗位 批量
     * @param userIds 用户ID集合
     * @param roleIds 角色ID集合
     */
    public void updateUserRole(Set<String> userIds,Set<String> roleIds){
        if(Collections3.isNotEmpty(userIds)){
            for(String userId:userIds){
                saveUserRoles(userId,roleIds);
            }
        }else{
            logger.warn("参数[userIds]为空.");
        }
    }

    /**
     * 设置用户岗位 批量
     * @param userIds 用户ID集合
     * @param postIds 岗位ID集合
     */
    public void updateUserPost(Set<String> userIds,Set<String> postIds) throws ServiceException{
        if(Collections3.isNotEmpty(userIds)){
            for(String userId:userIds){
                String organId = UserUtils.getDefaultOrganId(userId);
                saveUserOrganPosts(userId,organId,postIds);
            }
        }else{
            logger.warn("参数[userIds]为空.");
        }
    }

    /**
     * 设置用户岗位 批量
     * @param userIds 用户ID集合
     * @param resourceIds 资源ID集合
     */
    public void updateUserResource(Set<String> userIds,Set<String> resourceIds) throws ServiceException{
        if(Collections3.isNotEmpty(userIds)){
            for(String userId:userIds){
                saveUserResources(userId,resourceIds);
            }
        }else{
            logger.warn("参数[userIds]为空.");
        }
    }

    /**
     * 修改用户密码 批量
     * @param userIds 用户ID集合
     * @param password 密码(未加密)
     */
    public void updateUserPassword(List<String> userIds,String password) throws ServiceException{
        if(Collections3.isNotEmpty(userIds)){
            for(String userId:userIds){
                User model = this.get(userId);
                if(model == null){
                    throw new ServiceException("用户["+userId+"]不存在或已被删除.");
                }
                try {
                    model.setOriginalPassword(Encryption.encrypt(password));
                } catch (Exception e) {
                    throw new ServiceException(e);
                }
                model.setPassword(Encrypt.e(password));
                this.save(model);
                UserUtils.addUserPasswordUpdate(model);
            }
        }else{
            logger.warn("参数[userIds]为空.");
        }
    }

    public boolean checkPostForUser(User user, Post post){
        Validate.notNull(user, "参数[user]为空!");
        Validate.notNull(post, "参数[post]为空!");
        boolean flag = false;
        List<String> userOrganIds = organService.findOrganIdsByUserId(user.getId());
        if(Collections3.isNotEmpty(userOrganIds) && userOrganIds.contains(post.getOrganId())){
            flag = true;
        }
        return flag;
    }

    /**
     * 排序号交换
     * @param upUserId
     * @param downUserId
     * @param moveUp 是否上移 是；true 否（下移）：false
     */
    public void changeOrderNo(String upUserId, String downUserId, boolean moveUp) {
        Validate.notNull(upUserId, "参数[upUserId]不能为null!");
        Validate.notNull(downUserId, "参数[downUserId]不能为null!");
        User upUser = this.get(upUserId);
        User downUser = this.get(downUserId);
        if (upUser == null) {
            throw new ServiceException("用户[" + upUserId + "]不存在.");
        }
        Integer upUserOrderNo = upUser.getSort();
        Integer downUserOrderNo = downUser.getSort();
        if (upUser.getSort() == null) {
            upUserOrderNo = 1;
        }
        if (downUser == null) {
            throw new ServiceException("用户[" + downUserId + "]不存在.");
        }
        if (downUser.getSort() == null) {
            downUserOrderNo = 1;
        }
        if (upUserOrderNo == downUserOrderNo) {
            if (moveUp) {
                upUser.setSort(upUserOrderNo - 1);
            } else {
                downUser.setSort(downUserOrderNo + 1);
            }
        } else {
            upUser.setSort(downUserOrderNo);
            downUser.setSort(upUserOrderNo);
        }

        this.save(upUser);
        this.save(downUser);
    }

    /**
     * 锁定用户 批量
     * @param userIds 用户ID集合
     */
    public void lockUsers(List<String> userIds,String status){
        if(Collections3.isNotEmpty(userIds)){
            List<User> list = findUsersByIds(userIds);
            for(User user:list){
                user.setStatus(status);
                this.save(user);
            }
        }else{
            logger.warn("参数[userIds]为空.");
        }
    }

    /**
     * 根据ID查找
     * @param userIds 用户ID集合
     * @return
     */
    public List<User> findUsersByIds(List<String> userIds) {
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put("ids",userIds);
        return dao.findUsersByIds(parameter);
    }

    /**
     * 查询指定结构用户
     * @param organIds
     * @return
     */
    public List<User> findUsersByOrganIds(List<String> organIds) {
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put("organIds",organIds);
        return dao.findUsersByOrganIds(parameter);
    }

    /**
     * 查询指定结构用户ID
     * @param organId
     * @return
     */
    public List<String> findUserIdsByOrganId(String organId) {
        List<String> list = new ArrayList<String>(1);
        list.add(organId);
        return findUserIdsByOrganIds(list);
    }

    /**
     * 查询指定结构用户ID
     * @param organIds
     * @return
     */
    public List<String> findUserIdsByOrganIds(List<String> organIds) {
        if(Collections3.isEmpty(organIds)){
            return null;
        }
        Parameter parameter = new Parameter();
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put("organIds",organIds);
        return dao.findUsersIdsByOrganIds(parameter);
    }




    /**
     * 查询指定机构以及子机构
     * @param organId 机构ID
     * @return
     */
    public List<User> findOwnerAndChildsUsers(String organId, List<String> excludeUserIds){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,new User().getDbName());
        parameter.put("organId",organId);
        parameter.put("excludeUserIds",excludeUserIds);
        return dao.findOwnerAndChildsUsers(parameter);
    }

    /**
     * 查询指定机构以及子机构
     * @param organId 机构ID
     * @return
     */
    public List<User> findOwnerAndChildsUsers(String organId){
        return findOwnerAndChildsUsers(organId,null);
    }

    public List<String> findOwnerAndChildsUserIds(String organId){
        Parameter parameter = new Parameter();
        parameter.put(Organ.FIELD_STATUS,Organ.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findOwnerAndChildsUsersIds(parameter);
    }


    /**
     * 角色用户（分页查询）
     * @param page
     * @param roleId
     * @param query
     * @return
     */
    public Page<User> findPageRoleUsers(Page<User> page, String roleId, String query) {
        Parameter parameter = new Parameter();
        parameter.put(BaseInterceptor.DB_NAME,new User().getDbName());
        parameter.put(User.FIELD_STATUS,User.STATUS_NORMAL);
        parameter.put(BaseInterceptor.PAGE,page);
        parameter.put("roleId",roleId);
        parameter.put("query",query);
        return page.setResult(dao.findUsersByRole(parameter));
    }

    /**
     * 根据角色查询
     * @param roleId 角色ID
     * @return
     */
    public List<User> findUsersByRoleId(String roleId){
        Parameter parameter = new Parameter();
        parameter.put(Organ.FIELD_STATUS,Organ.STATUS_NORMAL);
        parameter.put("roleId",roleId);
        return dao.findUsersByRoleId(parameter);
    }

    /**
     * 根据角色查询
     * @param roleId 角色ID
     * @return
     */
    public List<String> findUserIdsByRoleId(String roleId){
        Parameter parameter = new Parameter();
        parameter.put(Organ.FIELD_STATUS,Organ.STATUS_NORMAL);
        parameter.put("roleId",roleId);
        return dao.findUserIdsByRoleId(parameter);
    }


    /**
     * 根据岗位查询
     * @param postId 岗位ID
     * @return
     */
    public List<User> findUsersByPostId(String postId){
        Parameter parameter = new Parameter();
        parameter.put(Organ.FIELD_STATUS,Organ.STATUS_NORMAL);
        parameter.put("postId",postId);
        return dao.findUsersByPostId(parameter);
    }

    /**
     * 根据岗位查询
     * @param postId 岗位
     * @return
     */
    public List<String> findUserIdsByPostId(String postId){
        Parameter parameter = new Parameter();
        parameter.put(Organ.FIELD_STATUS,Organ.STATUS_NORMAL);
        parameter.put("postId",postId);
        return dao.findUserIdsByPostId(parameter);
    }

    /**
     * 根据岗位查询
     * @param postId 岗位ID
     * @param organId 机构ID
     * @return
     */
    public List<User> findUsersByPostIdAndOrganId(String postId,String organId){
        Parameter parameter = new Parameter();
        parameter.put(Organ.FIELD_STATUS,Organ.STATUS_NORMAL);
        parameter.put("postId",postId);
        parameter.put("organId",organId);
        return dao.findUsersByPostIdAndOrganId(parameter);
    }

    /**
     * 根据岗位查询
     * @param postId 岗位
     * @param organId 机构ID
     * @return
     */
    public List<String> findUserIdsByPostIdAndOrganId(String postId,String organId){
        Parameter parameter = new Parameter();
        parameter.put(Organ.FIELD_STATUS,Organ.STATUS_NORMAL);
        parameter.put("postId",postId);
        parameter.put("organId",organId);
        return dao.findUserIdsByPostIdAndOrganId(parameter);
    }





    /**
     * 注销 空操 可提供切面使用
     */
    public void logout(String userId,SecurityType securityType){
        logger.debug("logout {}、{}",new Object[]{userId,securityType});

    }


    /**
     * 保存用户机构关联信息
     * 保存之前先删除原有
     * @param id 用户ID
     * @param ids 机构IDS
     */
    @Transactional(readOnly = false)
    public void saveUserOrgans(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deleteUserOrgansByUserId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertUserOrgans(parameter);
        }
    }

    /**
     * 保存用户岗位关联信息
     * 保存之前先删除原有
     * @param id 用户ID
     * @param ids 岗位IDS
     */
    @Transactional(readOnly = false)
    public void saveUserOrganPosts(String id,String organId, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("organId",organId);
        parameter.put("ids",ids);
        dao.deleteUserPostsByUserIdAndOrganId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertUserPosts(parameter);
        }
    }

    /**
     * 删除用户不在这些部门下的用户岗位信息
     * 保存之前先删除原有
     * @param id 用户ID
     * @param ids 机构IDS
     */
    @Transactional(readOnly = false)
    public void deleteNotInUserOrgansPostsByUserId(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        if(Collections3.isNotEmpty(ids)){
            dao.deleteNotInUserOrgansPostsByUserId(parameter);
        }
    }






    /**
     * 保存用户角色关联信息
     * 保存之前先删除原有
     * @param id 用户ID
     * @param ids 角色IDS
     */
    @Transactional(readOnly = false)
    public void saveUserRoles(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deleteUserRolesByUserId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertUserRoles(parameter);
        }
    }


    /**
     * 保存用户资源关联信息
     * 保存之前先删除原有
     * @param id 用户ID
     * @param ids 资源IDS
     */
    @Transactional(readOnly = false)
    public void saveUserResources(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deleteUserResourcesByUserId(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertUserResources(parameter);
        }
    }

}
