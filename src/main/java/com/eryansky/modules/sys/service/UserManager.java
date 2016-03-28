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
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.encode.Encrypt;
import com.eryansky.common.utils.encode.Encryption;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityType;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys.entity.*;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.CacheConstants;
import com.eryansky.utils.SelectType;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 用户管理User Service层实现类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-20 上午11:22:13 
 *
 */
@Service
public class UserManager extends EntityManager<User, String> {

    private HibernateDao<User, String> userDao;

    @Autowired
    private OrganManager organManager;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private PostManager postManager;
    @Autowired
    private UserPasswordManager userPasswordManager;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        userDao = new HibernateDao<User, String>(sessionFactory, User.class);
    }

    @Override
    protected HibernateDao<User, String> getEntityDao() {
        return userDao;
    }

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
    public void saveOrUpdate(User entity) throws DaoException,SystemException,ServiceException {
        logger.debug("清空缓存:{}",CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                +","+CacheConstants.ORGAN_USER_TREE_CACHE);
        userDao.saveOrUpdate(entity);
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
    public void merge(User entity) throws DaoException,SystemException,ServiceException {
        logger.debug("清空缓存:{}",CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                +","+CacheConstants.ORGAN_USER_TREE_CACHE);
        userDao.merge(entity);
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
    @Override
    public void saveEntity(User entity) throws DaoException, SystemException, ServiceException {
        logger.debug("清空缓存:{}",CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                +","+CacheConstants.ORGAN_USER_TREE_CACHE);
        super.saveEntity(entity);
    }


    /**
     * 自定义删除方法.
     */
    @CacheEvict(value = {  CacheConstants.ROLE_ALL_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE,
            CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void deleteByIds(List<String> ids) throws DaoException,SystemException,ServiceException {
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
                User user = userDao.get(id);
                if(user != null){
                    //清空关联关系
//                    user.setDefaultOrganId(null);
                    user.setOrgans(null);
                    user.setRoles(null);
                    user.setResources(null);
                    user.setPosts(null);
                    //逻辑删除
                    //手工方式(此处不使用 由注解方式实现逻辑删除)
//					user.setStatus(StatusState.delete.getValue());
                    //注解方式 由注解设置用户状态
                    userDao.delete(user);
                }
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
    public User getCurrentUser() throws DaoException,SystemException,ServiceException{
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        User user = getEntityDao().load(sessionInfo.getUserId());
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
    public User getSuperUser() throws DaoException,SystemException,ServiceException {
        User superUser = userDao.load("1");//超级用户ID为1
        if(superUser == null){
            throw new SystemException("系统未设置超级用户.");
        }
        return superUser;
    }

    public SessionInfo getUser(String loginName){
        Assert.notNull(loginName, "参数[loginName]为空!");
        User user = findUniqueBy("loginName",loginName);
        if(user == null){
            throw new ServiceException("用户["+loginName+"]不存在.");
        }

        return SecurityUtils.userToSessionInfo(user);
    }

    /**
     * 判断用户是否是超级用户
     * @param userId 用户Id
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public boolean isSuperUser(String userId) throws DaoException,SystemException,ServiceException{
        boolean flag = false;
        User user = getEntityDao().load(userId);
        User superUser = getSuperUser();

        if(user != null && user.getId().equals(superUser.getId())){
            flag = true;
        }
        return flag;
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
    public User getUserByLP(String loginName, String password)
            throws DaoException,SystemException,ServiceException {
        Assert.notNull(loginName, "参数[loginName]为空!");
        Assert.notNull(password, "参数[password]为空!");
        Parameter parameter = new Parameter(loginName, password,StatusState.DELETE.getValue());
        List<User> list = userDao.find(
                "from User u where u.loginName = :p1 and u.password = :p2 and u.status <> :p3", parameter);
        return list.isEmpty() ? null:list.get(0);
    }

    /**
     * 根据登录名或姓名、密码查找用户.
     * <br/>排除已删除的用户
     * @param loginNameOrName
     *            登录名或姓名
     * @param password
     *            密码
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    public User getUserByLNP(String loginNameOrName, String password)
            throws DaoException,SystemException,ServiceException {
        Assert.notNull(loginNameOrName, "参数[loginNameOrName]为空!");
        Assert.notNull(password, "参数[password]为空!");
        Parameter parameter = new Parameter(loginNameOrName, loginNameOrName, password, StatusState.DELETE.getValue());
        List<User> list = getEntityDao().find(
                "from User u where (u.loginName = :p1 or u.name = :p2) and u.password = :p3 and u.status <> :p4",
                parameter);
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
    public User getUserByMP(String mobile, String password)
            throws DaoException,SystemException,ServiceException {
        Assert.notNull(mobile, "参数[mobile]为空!");
        Assert.notNull(password, "参数[password]为空!");
        Parameter parameter = new Parameter(mobile, mobile, password, StatusState.DELETE.getValue());
        List<User> list = getEntityDao().find(
                "from User u where (u.loginName = :p1 or u.mobile = :p2) and u.password = :p3 and u.status <> :p4",
                parameter);
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
    public User getUserByLoginName(String loginName)
            throws DaoException,SystemException,ServiceException {
        Assert.notNull(loginName, "参数[loginName]为空!");
        Assert.notNull(loginName, "参数[status]为空!");
        Parameter parameter = new Parameter(loginName, StatusState.DELETE.getValue());
        List<User> list = userDao.find(
                "from User u where u.loginName = :p1 and u.status <> :p2",parameter);
        return list.isEmpty() ? null:list.get(0);
    }

    /**
     * 获得所有可用用户
     * @return
     */
    public List<User> findAllNormal(){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        return userDao.find("from User u where u.status = :p1 order by u.orderNo asc",parameter);
    }

    public List<String> findAllNormalUserIds(){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        return userDao.find("select u.id from User u where u.status = :p1 order by u.orderNo asc",parameter);
    }


    public List<User> findAllNormalWithExclude(List<String> userIds){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from User u where u.status = :p1 ");

        if(Collections3.isNotEmpty(userIds)){
            hql.append(" and u.id not in (:userIds)");
            parameter.put("userIds",userIds);
        }
        hql.append(" order by u.orderNo asc");
        return userDao.find(hql.toString(),parameter);
    }

    /**
     *
     * @param userIds 必须包含的用户
     * @param query 查询条件
     * @return
     */
    public List<User> findWithInclude(List<String> userIds,String query){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from User u where u.status = :p1 ");

        if(Collections3.isEmpty(userIds) && StringUtils.isBlank(query)){
            hql.append(" and 1 <> 1 ");
        }else if(Collections3.isNotEmpty(userIds) && StringUtils.isNotBlank(query)){
            hql.append(" and (u.id in (:userIds) or u.name like :query )");
            parameter.put("userIds",userIds);
            parameter.put("query","%"+query+"%");
        }else{
            if(Collections3.isNotEmpty(userIds)){
                hql.append(" and u.id in (:userIds)");
                parameter.put("userIds",userIds);

            }else if(StringUtils.isNotBlank(query)){
                hql.append(" and u.name like :query ");
                parameter.put("query","%"+query+"%");
            }
        }
        hql.append(" order by u.orderNo asc");
        return getEntityDao().find(hql.toString(),parameter);

    }

    /**
     *
     * @param page
     * @param organId 机构Id
     * @param loginNameOrName 登录名或姓名
     * @param userType 用户类型
     * @return
     */
    public Page<User> findPage(Page<User> page,String organId,String loginNameOrName, Integer userType) {
        Parameter parameter = new Parameter();
        StringBuilder hql = new StringBuilder();
        hql.append("select u from User u where u.status <> :status ");
        parameter.put("status",StatusState.DELETE.getValue());
        if(StringUtils.isNotBlank(organId)){
            hql.append("and u.defaultOrganId in (select o.id from Organ o where o.status = :ostatus and (o.id = :organId or parentIds like :parentIds)) ");
            parameter.put("ostatus",StatusState.NORMAL.getValue());
            parameter.put("organId",organId);
            parameter.put("parentIds", "%," + organId + ",%");
        }
        if(StringUtils.isNotBlank(loginNameOrName)){
            hql.append("and (u.loginName like :loginName or u.name like :name) ");
            parameter.put("loginName","%"+loginNameOrName+"%");
            parameter.put("name","%"+loginNameOrName+"%");
        }
        if(userType != null){
            hql.append("and u.userType = :userType ");
            parameter.put("userType",userType);
        }
        hql.append(" order by u.orderNo asc");

        //设置分页
        page = userDao.findPage(page,hql.toString(),parameter);
        return page;
    }


    public List<User> findUsersByOrgan(String organId, String loginNameOrName, List<String> excludeUserIds) {
        if(StringUtils.isBlank(organId)){
            return new ArrayList<User>(0);
        }

        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuilder hql = new StringBuilder();
        hql.append("from User u where u.status = :p1 ");
        if(Collections3.isNotEmpty(excludeUserIds)){
            hql.append(" and u.id not in :excludeUserIds");
            parameter.put("excludeUserIds", excludeUserIds);
        }
        hql.append(" and u.defaultOrganId in (select o.id from Organ o where o.status = :p1 and (o.id = :organId or o.parentIds like :parentIds)) ");
        parameter.put("organId", organId);
        parameter.put("parentIds", "%," + organId + ",%");
        if (StringUtils.isNotBlank(loginNameOrName)) {
            hql.append(" and  (u.name like :name or u.loginName like :name) ");
            parameter.put("name","%" + loginNameOrName + "%");
        }
        hql.append(" order by u.orderNo asc");
        logger.debug(hql.toString());
        List<User> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }

    public Page<User> findUsersByOrgan(Page<User> page,String organId, String loginNameOrName, List<String> excludeUserIds) {
        if(StringUtils.isBlank(organId)){
            return page;
        }

        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuilder hql = new StringBuilder();
        hql.append("from User u where u.status = :p1 ");
        if(Collections3.isNotEmpty(excludeUserIds)){
            hql.append(" and u.id not in :excludeUserIds");
            parameter.put("excludeUserIds", excludeUserIds);
        }
        hql.append(" and u.defaultOrganId in (select o.id from Organ o where o.status = :p1 and (o.id = :organId or o.parentIds like :parentIds)) ");
        parameter.put("organId", organId);
        parameter.put("parentIds", "%," + organId + ",%");
        if (StringUtils.isNotBlank(loginNameOrName)) {
            hql.append(" and  (u.name like :name or u.loginName like :name) ");
            parameter.put("name","%" + loginNameOrName + "%");
        }
        hql.append(" order by u.orderNo asc");
        logger.debug(hql.toString());
        page = getEntityDao().findPage(page,hql.toString(), parameter);
        return page;
    }

    /**
     * @param organId 机构ID 查询本级以及下级所有机构下的用户
     * @param roleId 角色ID
     * @param loginNameOrName 登录名或姓名
     * @param loginNameOrName
     * @param excludeUserIds 排除的用户ID集合
     * @return
     */
    public List<User> getUsersByOrganOrRole(String organId, String roleId, String loginNameOrName, List<String> excludeUserIds) {
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuilder hql = new StringBuilder();
        hql.append("from User u where u.status = :p1 ");
        if(Collections3.isNotEmpty(excludeUserIds)){
            hql.append(" and u.id not in :excludeUserIds");
            parameter.put("excludeUserIds", excludeUserIds);
        }
        if (StringUtils.isNotBlank(organId)) {
            hql.append(" and u.defaultOrganId in (select o.id from Organ o where o.status = :ostatus and (o.id = :organId or parentIds like :parentIds)) ");
            parameter.put("ostatus",StatusState.NORMAL.getValue());
            parameter.put("organId", organId);
            parameter.put("parentIds", "%," + organId + ",%");
        }
        if (StringUtils.isNotBlank(roleId)) {
            Role role = roleManager.loadById(roleId);
            hql.append(" and :role in elements(u.roles) ");
            parameter.put("role",role);
        }
        if (StringUtils.isNotBlank(loginNameOrName)) {
            hql.append(" and  (u.name like :name or u.loginName like :name) ");
            parameter.put("name","%" + loginNameOrName + "%");
        }
        hql.append(" order by u.orderNo");
        logger.debug(hql.toString());
        List<User> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }

    /**
     * 获取机构用户
     * @param organId
     * @return
     */
    public List<User> findOrganUsers(String organId) {
        Assert.notNull(organId, "参数[organId]为空!");
        Organ organ  = organManager.loadById(organId);
        if(organ == null){
            throw new ServiceException("机构["+organId+"]不存在.");
        }
        List<User> users = organ.getUsers();
        return users;
    }

    /**
     * 得到排序字段的最大值.
     *
     * @return 返回排序字段的最大值
     */
    public Integer getMaxSort() throws DaoException, SystemException,
            ServiceException {
        Iterator<?> iterator = getEntityDao().createQuery(
                "select max(u.orderNo)from User u ").iterate();
        Integer max = 0;
        while (iterator.hasNext()) {
            // Object[] row = (Object[]) iterator.next();
            max = (Integer) iterator.next();
            if (max == null) {
                max = 0;
            }
        }
        return max;
    }

    /**
     * 批量更新用户 机构信息
     * @param userIds 用户Id集合
     * @param organIds 所所机构ID集合
     * @param defaultOrganId 默认机构
     */
    public void updateUserOrgan(List<String> userIds,List<String> organIds, String defaultOrganId){
        if(Collections3.isNotEmpty(userIds)){
            for(String userId:userIds){
                User model = this.loadById(userId);
                if(model == null){
                    throw new ServiceException("用户["+userId+"]不存在.");
                }
                List<Organ> oldOrgans = model.getOrgans();
                //绑定组织机构
                model.setOrgans(null);
                List<Organ> organs = Lists.newArrayList();
                if (Collections3.isNotEmpty(organIds)) {
                    for (String organId : organIds) {
                        Organ organ = organManager.loadById(organId);
                        organs.add(organ);
                        if (Collections3.isNotEmpty(oldOrgans)) {
                            Iterator<Organ> iterator = oldOrgans.iterator();
                            while (iterator.hasNext()) {
                                Organ oldOrgan = iterator.next();
                                if (oldOrgan.getId().equals(organ.getId())) {
                                    iterator.remove();
                                }
                            }

                        }
                    }
                }


                //去除用户已删除机构下的岗位信息
                List<Post> userPosts = model.getPosts();
                if (Collections3.isNotEmpty(oldOrgans)) {//已删除的机构
                    Iterator<Organ> iterator = oldOrgans.iterator();
                    while (iterator.hasNext()) {
                        Organ oldOrgan = iterator.next();
                        List<Post> organPosts = postManager.getOrganPosts(oldOrgan.getId());
                        for (Post organPost : organPosts) {
                            if (Collections3.isNotEmpty(userPosts)) {
                                Iterator<Post> iteratorPost = userPosts.iterator();
                                while (iteratorPost.hasNext()) {
                                    Post userPost = iteratorPost.next();
                                    if (userPost.getId().equals(organPost.getId())) {
                                        iteratorPost.remove();
                                    }
                                }
                            }
                        }
                    }

                }


                model.setOrgans(organs);
                model.setDefaultOrganId(defaultOrganId);

                this.saveOrUpdate(model);
            }
        }
    }


    /**
     * 设置用户岗位 批量
     * @param userIds 用户ID集合
     * @param roleIds 角色ID集合
     */
    public void updateUserRole(List<String> userIds,List<String> roleIds){
        if(Collections3.isNotEmpty(userIds)){
            for(String userId:userIds){
                User model = this.loadById(userId);
                if(model == null){
                    throw new ServiceException("用户["+userId+"]不存在.");
                }
                List<Role> rs = Lists.newArrayList();
                if (Collections3.isNotEmpty(roleIds)) {
                    for (String id : roleIds) {
                        Role role = roleManager.loadById(id);
                        rs.add(role);
                    }
                }

                model.setRoles(rs);
                this.saveOrUpdate(model);
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
    public void updateUserPost(List<String> userIds,List<String> postIds) throws ServiceException{
        if(Collections3.isNotEmpty(userIds)){
            for(String userId:userIds){
                User model = this.loadById(userId);
                if(model == null){
                    throw new ServiceException("用户["+userId+"]不存在.");
                }
                List<Post> ps = Lists.newArrayList();
                if (Collections3.isNotEmpty(postIds)) {
                    for (String id : postIds) {
                        Post post = postManager.loadById(id);
                        if(!this.checkPostForUser(model,post)){
                            throw new ServiceException("用户["+model.getName()+"]不允许设置为岗位["+post.getName()+"],用户所属机构不存在此岗位.");
                        }
                        ps.add(post);
                    }
                }

                model.setPosts(ps);

                this.saveOrUpdate(model);
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
    public void updateUserResource(List<String> userIds,List<String> resourceIds) throws ServiceException{
        if(Collections3.isNotEmpty(userIds)){
            for(String userId:userIds){
                User model = this.loadById(userId);
                if(model == null){
                    throw new ServiceException("用户["+userId+"]不存在.");
                }
                List<Resource> rs = Lists.newArrayList();
                if(Collections3.isNotEmpty(resourceIds)){
                    for (String id : resourceIds) {
                        Resource resource = resourceManager.loadById(id);
                        rs.add(resource);
                    }
                }

                model.setResources(rs);
                this.saveOrUpdate(model);
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
                User model = this.loadById(userId);
                if(model == null){
                    throw new ServiceException("用户["+userId+"]不存在或已被删除.");
                }
                try {
                    model.setOriginalPassword(Encryption.encrypt(password));
                } catch (Exception e) {
                    throw new ServiceException(e);
                }
                model.setPassword(Encrypt.e(password));
                this.saveEntity(model);
                UserUtils.addUserPasswordUpdate(model);
            }
        }else{
            logger.warn("参数[userIds]为空.");
        }
    }

    public boolean checkPostForUser(User user,Post post){
        Validate.notNull(user, "参数[user]为空!");
        Validate.notNull(post, "参数[post]为空!");
        boolean flag = false;
        List<String> userOrganIds = user.getOrganIds();
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
        User upUser = this.loadById(upUserId);
        User downUser = this.loadById(downUserId);
        if (upUser == null) {
            throw new ServiceException("用户[" + upUserId + "]不存在.");
        }
        Integer upUserOrderNo = upUser.getOrderNo();
        Integer downUserOrderNo = downUser.getOrderNo();
        if (upUser.getOrderNo() == null) {
            upUserOrderNo = 1;
        }
        if (downUser == null) {
            throw new ServiceException("用户[" + downUserId + "]不存在.");
        }
        if (downUser.getOrderNo() == null) {
            downUserOrderNo = 1;
        }
        if (upUserOrderNo == downUserOrderNo) {
            if (moveUp) {
                upUser.setOrderNo(upUserOrderNo - 1);
            } else {
                downUser.setOrderNo(downUserOrderNo + 1);
            }
        } else {
            upUser.setOrderNo(downUserOrderNo);
            downUser.setOrderNo(upUserOrderNo);
        }

        this.update(upUser);
        this.update(downUser);
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
                this.update(user);
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
        Parameter parameter = new Parameter(userIds);
        return getEntityDao().find("from User u where u.id in :p1 order by u.orderNo",parameter);
    }

    /**
     * 查询指定结构用户
     * @param organIds
     * @return
     */
    public List<User> findUsersByOrganIds(List<String> organIds) {
        Parameter parameter = new Parameter(organIds);
        return getEntityDao().find("from User u left join u.organs o where o.id in :p1 order by u.orderNo",parameter);
    }

    /**
     * 查询指定结构用户ID
     * @param organIds
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
        Parameter parameter = new Parameter(organIds);
        return getEntityDao().find("select u.id from User u left join u.organs o where o.id in :p1 order by u.orderNo",parameter);
    }




    /**
     * 查询指定机构以及子机构
     * @param organId 机构ID
     * @return
     */
    public List<User> findOwnerAndChildsUsers(String organId,List<String> excludeUserIds){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),organId);
        parameter.put("parentIds","%," + organId + ",%");
        StringBuilder hql = new StringBuilder();
        hql.append("select u from User u left join u.organs o where u.status = :p1")
                .append(" and o.status = :p1  and (o.id = :p2 or o.parentIds like  :parentIds) ")
                .append(" order by u.orderNo asc");

        if(Collections3.isNotEmpty(excludeUserIds)){
            hql.append(" and u.id not in (:userIds)");
            parameter.put("userIds",excludeUserIds);
        }

        List<User> list = getEntityDao().find(hql.toString(), parameter);
        return list;
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
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),organId);
        parameter.put("parentIds","%," + organId + ",%");
        StringBuilder hql = new StringBuilder();
        hql.append("select u.id from User u left join u.organs o where u.status = :p1")
                .append(" and o.status = :p1  and (o.id = :p2 or o.parentIds like  :parentIds) ")
                .append(" order by u.orderNo asc");
        List<String> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }




    public List<String> findChildsUserIds(String organId){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        parameter.put("parentIds","%," + organId + ",%");
        StringBuilder hql = new StringBuilder();
        hql.append("select u.id from User u left join u.organs o where u.status = :p1")
                .append(" and o.status = :p1  and (o.parentIds like  :parentIds) ")
                .append(" order by u.orderNo asc");
        List<String> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }

    /**
     * 注销 空操 可提供切面使用
     */
    public void logout(String userId,SecurityType securityType){
        logger.debug("logout {}、{}",new Object[]{userId,securityType});

    }

    /**
     * 查找机构下的任意一个人
     * @param organId
     * @return
     */
    public User findOrganAnyUser(String organId){
        List<User> list = this.findOwnerAndChildsUsers(organId);
        for(User user:list){
            if(user.getCompanyId().equals(organId)){
                return user;
            }
        }
        return list.isEmpty() ? null:list.get(0);
    }
}
