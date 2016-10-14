/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Menu;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Sets;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.entity.Resource;
import com.eryansky.utils.CacheConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 资源Resource管理 Service层实现类.
 * <br>树形资源使用缓存 当保存、删除操作时清除缓存
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-11 下午4:26:46
 */
@Service
public class ResourceManager extends EntityManager<Resource, String> {


    @Autowired
    private UserManager userManager;

    private HibernateDao<Resource, String> resourceDao;// 默认的泛型DAO成员变量.

    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        resourceDao = new HibernateDao<Resource, String>(sessionFactory, Resource.class);
    }

    @Override
    protected HibernateDao<Resource, String> getEntityDao() {
        return resourceDao;
    }

    /**
     * 保存或修改.
     */
    //清除缓存
    @CacheEvict(value = { CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE},allEntries = true)
    public void saveOrUpdate(Resource entity) throws DaoException, SystemException,
            ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE);
        Assert.notNull(entity, "参数[entity]为空!");
        resourceDao.saveOrUpdate(entity);
    }

    /**
     * 保存或修改.
     */
    //清除缓存
    @CacheEvict(value = { CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE},allEntries = true)
    public void merge(Resource entity) throws DaoException, SystemException,
            ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE);
        Assert.notNull(entity, "参数[entity]为空!");
        resourceDao.merge(entity);
    }

    @CacheEvict(value = { CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE},allEntries = true)
    @Override
    public void saveEntity(Resource entity) throws DaoException, SystemException, ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                + "," + CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                + "," + CacheConstants.RESOURCE_USER_MENU_TREE_CACHE);
        super.saveEntity(entity);
    }

    /**
     * 自定义保存资源.
     * <br/>说明：如果保存的资源类型为“功能” 则将所有子资源都设置为“功能”类型
     * @param entity 资源对象
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @CacheEvict(value = { CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE},allEntries = true)
    public void saveResource(Resource entity) throws DaoException, SystemException, ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE);
        Assert.notNull(entity,"参数[entity]为空!");
        this.saveEntity(entity);
        if(entity.getType() !=null && ResourceType.function.getValue().equals(entity.getType())){
            List<Resource> ownerAndChilds = this.findOwnerAndChilds(entity.getId());
            Iterator<Resource> iterator = ownerAndChilds.iterator();
            while(iterator.hasNext()){
                Resource subResource = iterator.next();
                subResource.setType(ResourceType.function.getValue());
                this.update(subResource);
            }
        }
    }


    /**
     * 自定义删除方法.
     */
    //清除缓存
    @CacheEvict(value = { CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE,
            CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE,
            CacheConstants.RESOURCE_USER_MENU_TREE_CACHE},allEntries = true)
    public void deleteByIds(List<String> ids) throws DaoException, SystemException,
            ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE
                +","+CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE
                +","+CacheConstants.RESOURCE_USER_MENU_TREE_CACHE);
        if(!Collections3.isEmpty(ids)){
            for(String id :ids){
                Resource resource = getEntityDao().load(id);
                resource.setRoles(null);
                resource.setUsers(null);
                this.delete(resource);
            }
        }else{
            logger.warn("参数[ids]为空.");
        }

    }

    /**
     * 查找本机以及下级数据
     * @param id
     * @return
     */
    public List<Resource> findOwnerAndChilds(String id){
        return this.findOwnerAndChilds(id,null);
    }
    /**
     * 查找本机以及下级数据（下级所有数据）
     * @param id
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<Resource> findOwnerAndChilds(String id, List<Integer> resourceTypes){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),id);
        Resource resource = this.loadById(id);
        StringBuilder hql = new StringBuilder();
        hql.append("from Resource r where r.status = :p1  and (r.id = :p2 ");
        if(resource != null){
            hql.append(" or r.parentIds like  :parentIds ");
            parameter.put("parentIds","%," + id + ",%");
        }
        hql.append(" ) ");
        if(resourceTypes != null){
            hql.append(" and r.type in (:resourceTypes)");
            parameter.put("resourceTypes",resourceTypes);
        }
        hql.append(" order by r.orderNo asc");
        List<Resource> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }

    /**
     *  查找下级数据（仅下级数据）
     * @param id
     * @return
     */
    public List<Resource> findChilds(String id){
        return this.findChilds(id, null);
    }

    /**
     * 查找下级数据 （仅下级数据）
     * @param id 父级ID
     * @param status 传null则使用默认值 默认值:StatusState.NORMAL.getValue() {@link StatusState}
     * @return
     */
    public List<Resource> findChilds(String id, String status){
        //默认值 正常
        if(status == null){
            status = StatusState.NORMAL.getValue();
        }
        StringBuilder sb = new StringBuilder();
        Parameter parameter = new Parameter();
        sb.append("from Resource r where r.status = :status  ");
        parameter.put("status", status);
        sb.append(" and r.parent");
        if (id == null) {
            sb.append(" is null ");
        } else {
            sb.append(".id = :parentId ");
            parameter.put("parentId", id);
        }
        sb.append(" order by r.orderNo asc");

        List<Resource> list = getEntityDao().find(sb.toString(), parameter);
        return list;
    }

    /**
     * 根据资源编码获取对象
     * @param resourceCode 资源编码
     * @return
     */
    public Resource getByCode(String resourceCode) {
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),resourceCode);
        StringBuffer hql = new StringBuffer();
        hql.append("from Resource r where r.status = :p1 and r.code = :p2 ");
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<Resource> list = query.list();
        return list.isEmpty() ? null:list.get(0);
    }

    /**
     * 查找用户导航菜单资源
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findAppAndMenuResourcesByUserId(String userId){
        List<String> resourceTypes = Lists.newArrayList();
        resourceTypes.add(ResourceType.app.getValue());
        resourceTypes.add(ResourceType.menu.getValue());
        return  findResourcesByUserId(userId, resourceTypes);
    }

    /**
     * 查找用户导航菜单资源
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findAppResourcesByUserId(String userId){
        List<String> resourceTypes = Lists.newArrayList();
        resourceTypes.add(ResourceType.app.getValue());
        return  findResourcesByUserId(userId, resourceTypes);
    }

    /**
     * 查找用户导航菜单资源
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findMenuResourcesByUserId(String userId){
        List<String> resourceTypes = Lists.newArrayList();
        resourceTypes.add(ResourceType.menu.getValue());
        return  findResourcesByUserId(userId, resourceTypes);
    }

    /**
     * 查找用户资源
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findResourcesByUserId(String userId){
        return findResourcesByUserId(userId, null);
    }

    /**
     * 查找用户资源
     * @param userId 用户ID
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<Resource> findResourcesByUserId(String userId, List<String> resourceTypes){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),userId);
        StringBuffer hql = new StringBuffer();
        hql.append("select r from Resource r left join r.users u where r.status = :p1 and u.id = :p2 ");
        if(resourceTypes != null){
            hql.append(" and r.type in (:resourceTypes)");
            parameter.put("resourceTypes",resourceTypes);
        }
        hql.append(" or r.id in (");
        hql.append("select urrs.id from User u left join u.roles urs left join urs.resources urrs where urrs.status = :p1 and u.id = :p2 ");
        if(resourceTypes != null){
            hql.append(" and urrs.type in (:resourceTypes)");
            parameter.put("resourceTypes",resourceTypes);
        }
        hql.append(" order by urrs.orderNo asc");
        hql.append(" )");

        hql.append(" order by r.orderNo asc");


        List<Resource> list =  getEntityDao().distinct(getEntityDao().createQuery(hql.toString(),parameter)).list();
//        Collections.sort(list, new Comparator<Resource>() {
//            @Override
//            public int compare(Resource o1, Resource o2) {
//                if (o1.getOrderNo() != null && o2.getOrderNo() != null) {
//                    return o1.getOrderNo().compareTo(o2.getOrderNo());
//                }
//                return 0;
//            }
//        });
        return list;
    }

    /**
     * 用户导航菜单 （权限控制）
     * @param userId 用户ID
     * @return
     */
    @Cacheable(value = { CacheConstants.RESOURCE_USER_MENU_TREE_CACHE})
    public List<TreeNode> findNavTreeNodeWithPermissions(String userId){
        List<Resource> list = null;
        if (SecurityUtils.isUserAdmin(userId)) {// 超级用户
            list = this.findAppAndMenuResources();
        } else {
            list = this.findAppAndMenuResourcesByUserId(userId);
        }

        logger.debug("缓存:{}", CacheConstants.RESOURCE_USER_MENU_TREE_CACHE + " 参数：userId=" + userId);
        return resourcesToTreeNode(list);
    }

    /**
     * 查找用户菜单
     * @param userId 用户ID
     * @return
     */
    public List<Menu> findNavMenuWithPermissions(String userId){
        List<Resource> list = null;
        if (SecurityUtils.isUserAdmin(userId)) {// 超级用户
            list = this.findAppAndMenuResources();
        } else {
            list = this.findAppAndMenuResourcesByUserId(userId);
        }

        return resourcesToMenu(list);
    }

    /**
     * 根据用户ID得到导航栏资源（权限控制）
     * @param userId 用户ID
     * @return
     */
    @Cacheable(value = { CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE})
    public List<TreeNode> findTreeNodeResourcesWithPermissions(String userId) {
        List<Resource> list = null;
        if (SecurityUtils.isUserAdmin(userId)) {// 超级用户
            list = this.findResources(null);
        } else {
            list = this.findResourcesByUserId(userId, null);
        }

        logger.debug("缓存:{}", CacheConstants.RESOURCE_USER_RESOURCE_TREE_CACHE + " 参数：userId=" + userId);
        return resourcesToTreeNode(list);
    }

    /**
     * 查找授权应用 （权限控制）
     * @param userId 用户ID
     * @return
     */
    public List<Resource> findAppResourcesWithPermissions(String userId){
        List<Resource> list = null;
        if (SecurityUtils.isUserAdmin(userId)) {// 超级用户
            list = this.findAppResources();
        } else {
            list = this.findAppResourcesByUserId(userId);
        }
        return  list;
    }


    /**
     * 查找菜单资源
     * @return
     */
    public List<Resource> findAppAndMenuResources(){
        List<String> resourceTypes = Lists.newArrayList();
        resourceTypes.add(ResourceType.app.getValue());
        resourceTypes.add(ResourceType.menu.getValue());
        return  findResources(resourceTypes);
    }

    /**
     * 查找菜单资源 应用
     * @return
     */
    public List<Resource> findAppResources(){
        List<String> resourceTypes = Lists.newArrayList();
        resourceTypes.add(ResourceType.app.getValue());
        return  findResources(resourceTypes);
    }

    /**
     * 查找菜单资源
     * @return
     */
    public List<Resource> findMenuResources(){
        List<String> resourceTypes = Lists.newArrayList();
        resourceTypes.add(ResourceType.menu.getValue());
        return  findResources(resourceTypes);
    }

    /**
     * 查找所有资源
     * @return
     */
    public List<Resource> findResources(){
        return  findResources(null,null);
    }

    /**
     * 查找资源
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @return
     */
    public List<Resource> findResources(List<String> resourceTypes){
        return  findResources(resourceTypes,null);
    }

    /**
     * 查找资源
     * @param resourceTypes 资源类型 为null,则查询所有 {@link ResourceType}
     * @param excludeResourceId 排除的资源ID
     * @return
     */
    public List<Resource> findResources(List<String> resourceTypes, String excludeResourceId){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from Resource r where r.status = :p1 ");
        if(excludeResourceId != null){
            hql.append(" and r.id <> :excludeReourceId");
            parameter.put("excludeReourceId", excludeResourceId);
        }
        if(resourceTypes != null){
            hql.append(" and r.type in (:resourceTypes)");
            parameter.put("resourceTypes",resourceTypes);
        }
        hql.append(" order by r.orderNo asc");
        return  getEntityDao().find(hql.toString(),parameter);
    }

    /**
     * 查找所有资源
     * @return
     */
    public List<TreeNode> findTreeNodeResources(){
        List<Resource> list = this.findResources(null);
        return resourcesToTreeNode(list);

    }

    /**
     * 查找所有资源
     * @param excludeResourceId 需要排除的资源ID 子级也会被排除
     * @return
     */
    public List<TreeNode> findTreeNodeResourcesWithExclude(String excludeResourceId){
        List<Resource> list = this.findResources(null,excludeResourceId);
        return resourcesToTreeNode(list);

    }

    /**
     * 资源集合构造成TreeNode 结构自动调整
     * @param resources
     * @return
     */
    private List<TreeNode> resourcesToTreeNode(List<Resource> resources){
        List<TreeNode> tempTreeNodes = Lists.newArrayList();
        if(Collections3.isEmpty(resources)){
            return tempTreeNodes;
        }

        Map<String,TreeNode> tempMap = Maps.newLinkedHashMap();
        Iterator<Resource> iterator = resources.iterator();
        while (iterator.hasNext()){
            Resource resource = iterator.next();
            TreeNode treeNode = this.resourceToTreeNode(resource);
            boolean flag = true;
            for(TreeNode treeNode0:tempTreeNodes){
                if(treeNode0.getId().equals(treeNode.getId())){
                    flag = false;
                    break;
                }
            }
            if(flag){
                tempTreeNodes.add(treeNode);
            }
            tempMap.put(resource.getId(), treeNode);
        }

        Set<String> keyIds = tempMap.keySet();
        Set<String> removeKeyIds = Sets.newHashSet();
        Iterator<String> iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            String key = iteratorKey.next();
            TreeNode treeNode = null;
            for(TreeNode treeNode1:tempTreeNodes){
                if(treeNode1.getId().equals(key)){
                    treeNode = treeNode1;
                    break;
                }
            }
            if(StringUtils.isNotBlank(treeNode.getpId())){
                TreeNode pTreeNode = getParentTreeNode(treeNode.getpId(), tempTreeNodes);
                if(pTreeNode != null){
                    for(TreeNode treeNode2:tempTreeNodes){
                        if(treeNode2.getId().equals(pTreeNode.getId())){
                            treeNode2.addChild(treeNode);
                            removeKeyIds.add(treeNode.getId());
                            break;
                        }
                    }
                }
            }

        }

        //remove
        if(Collections3.isNotEmpty(removeKeyIds)){
            keyIds.removeAll(removeKeyIds);
        }

        List<TreeNode> result = Lists.newArrayList();
        keyIds = tempMap.keySet();
        iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            String _key = iteratorKey.next();
            TreeNode treeNode = null;
            for(TreeNode treeNode4:tempTreeNodes){
                if(treeNode4.getId().equals(_key)){
                    treeNode = treeNode4;
                    result.add(treeNode);
                    break;
                }
            }

        }
        return result;
    }

    /**
     *
     * @param resources
     * @return
     */
    private List<Menu> resourcesToMenu(List<Resource> resources){
        List<Menu> tempMenus = Lists.newArrayList();
        if(Collections3.isEmpty(resources)){
            return tempMenus;
        }
        Map<String,Menu> tempMap = Maps.newHashMap();
        Iterator<Resource> iterator = resources.iterator();
        while (iterator.hasNext()){
            Resource resource = iterator.next();
            Menu menu = this.resourceToMenu(resource);
            tempMenus.add(menu);
            tempMap.put(resource.getId(), menu);
        }

        Set<String> keyIds = tempMap.keySet();
        Iterator<String> iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            Menu menu = tempMap.get(iteratorKey.next());
            if(StringUtils.isNotBlank(menu.getpId())){
                Menu parentMenu = getParentMenu(menu.getpId(), tempMenus);
                if(parentMenu != null){
                    parentMenu.addChild(menu);
                    iteratorKey.remove();
                }
            }

        }

        List<Menu> result = Lists.newArrayList();
        keyIds = tempMap.keySet();
        iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            Menu menu = tempMap.get(iteratorKey.next());
            result.add(menu);

        }
        return result;
    }


    /**
     * 查找父级节点
     * @param parentId
     * @param treeNodes
     * @return
     */
    private TreeNode getParentTreeNode(String parentId, List<TreeNode> treeNodes){
        TreeNode t = null;
        for(TreeNode treeNode:treeNodes){
            if(parentId.equals(treeNode.getId())){
                t = treeNode;
                break;
            }
        }
        return t;
    }

    /**
     * 查找父级节点
     * @param parentId
     * @param menus
     * @return
     */
    private Menu getParentMenu(String parentId, List<Menu> menus){
        Menu t = null;
        for(Menu menu : menus){
            if(parentId.equals(menu.getId())){
                t = menu;
                break;
            }
        }
        return t;
    }

    /**
     * 资源转TreeNode
     * @param resource 资源
     * @return
     */
    private TreeNode resourceToTreeNode(Resource resource) {
        TreeNode treeNode = new TreeNode(resource.getId(),resource.getName(), resource.getIconCls());
        treeNode.setpId(resource.get_parentId());
        treeNode.addAttributes("url", resource.getUrl());
        treeNode.addAttributes("markUrl", resource.getMarkUrl());
        treeNode.addAttributes("code", resource.getCode());
        treeNode.addAttributes("type", resource.getType());
        return treeNode;
    }

    /**
     * 资源转Menu
     * @param resource 资源
     * @return
     */
    private Menu resourceToMenu(Resource resource) {
        Assert.notNull(resource,"参数resource不能为空");
        Menu menu = new Menu(resource.getId(), resource.getName());
        menu.setpId(resource.get_parentId());
        menu.setHref(resource.getUrl());
        menu.addAttributes("type", resource.getType());
        return menu;
    }



    /**
     * 得到排序字段的最大值.
     *
     * @return 返回排序字段的最大值
     */
    public Integer getMaxSort() throws DaoException, SystemException,
            ServiceException {
        Iterator<?> iterator = resourceDao.createQuery(
                "select max(m.orderNo)from Resource m ").iterate();
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
     * 检查用户是否具有某个资源编码的权限
     * @param userId 用户ID
     * @param resourceCode 资源编码
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public boolean isUserPermittedResourceCode(String userId, String resourceCode) throws DaoException, SystemException,
            ServiceException {
        Assert.notNull(userId, "参数[userId]为空!");
        Assert.notNull(resourceCode, "参数[resourceCode]为空!");
        List<Resource> list = this.findResourcesByUserId(userId);
        boolean flag = false;
        for (Resource resource : list) {
            if (resource != null && StringUtils.isNotBlank(resource.getCode()) && resource.getCode().equalsIgnoreCase(resourceCode)) {
                flag = true;
                break;
            }
        }
        return flag;
    }


    /**
     * 根据请求地址判断用户是否有权访问该url
     * @param requestUrl 请求URL地址
     * @param userId 用户ID
     * @return
     */
    @Cacheable(value = {CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE})
    public boolean isAuthorityWithPermissions(String requestUrl, String userId)
            throws DaoException,SystemException,ServiceException{
        //如果是超级管理员 直接允许被授权
        if(SecurityUtils.isUserAdmin(userId)) {
            return true;
        }
        //检查该URL是否需要拦截
        boolean isInterceptorUrl = this.isInterceptorUrl(requestUrl);
        if (isInterceptorUrl){
            //用户权限Lo
            List<String> userAuthoritys = this.getUserAuthoritysByUserId(userId);
            for(String markUrl :userAuthoritys){
                String[] markUrls = markUrl.split(";");
                for(int i=0;i<markUrls.length;i++){
                    if(StringUtils.isNotBlank(markUrls[i]) && StringUtils.simpleWildcardMatch(markUrls[i],requestUrl)){
                        return true;
                    }
                }
            }
            return false;
        }
        logger.debug("缓存:{}", CacheConstants.RESOURCE_USER_AUTHORITY_URLS_CACHE +"参数：requestUrl="+requestUrl+",userId="+userId);
        return true;
    }


    /**
     * 查找需要拦截的所有url规则
     * @return
     */
    public List<String> getAllInterceptorUrls()
            throws DaoException,SystemException,ServiceException{
        List<String> markUrls = Lists.newArrayList();
        //查找所有资源
        List<Resource> resources = this.findResources();
        for(Resource resource : resources){
            if(StringUtils.isNotBlank(resource.getMarkUrl())){
                markUrls.add(resource.getMarkUrl());
            }
        }
        return markUrls;
    }

    /**
     * 检查某个URL是都需要拦截
     * @param requestUrl 检查的URL地址
     * @return
     */
    public boolean isInterceptorUrl(String requestUrl)
            throws DaoException,SystemException,ServiceException{
        List<String> markUrlList = this.getAllInterceptorUrls();
        for(String markUrl :markUrlList){
            String[] markUrls = markUrl.split(";");
            for(int i=0;i<markUrls.length;i++){
                if(StringUtils.isNotBlank(markUrls[i]) && StringUtils.simpleWildcardMatch(markUrls[i],requestUrl)){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 根据用户ID查找用户拥有的URL权限
     * @param userId   用户ID
     * @return    List<String> 用户拥有的markUrl地址
     */
    public List<String> getUserAuthoritysByUserId(String userId)
            throws DaoException,SystemException,ServiceException{
        List<String> userAuthoritys = Lists.newArrayList();
        List<TreeNode> treeNodes = this.findTreeNodeResourcesWithPermissions(userId);
        for(TreeNode node : treeNodes){
            Object obj = node.getAttributes().get("markUrl");
            if(obj != null){
                String markUrl = (String)obj ;
                if(StringUtils.isNotBlank(markUrl)){
                    userAuthoritys.add(markUrl);
                }
            }
        }
        return  userAuthoritys;
    }



    /**    外部接口同步到资源  **/

    /**
     * 同步
     *
     * @param code      编码
     * @param name       资源名称
     * @param parentCode 上级编码
     * @param status 是否启用 默认值：启用 {@link StatusState}
     * @return
     */
    public void iSynchronous(String resourceType, String code, String name, String parentCode, String status) throws DaoException, SystemException,
            ServiceException {
        Validate.notNull(code, "参数[code]不能为null");
        if (status == null) {
            status = StatusState.NORMAL.getValue();
        }
        Resource parentResource = null;
        if (StringUtils.isNotBlank(parentCode)) {
            parentResource = this.iGetReource(resourceType, parentCode, null);
            if (parentResource == null) {
                throw new SystemException("上级[" + parentCode + "]不存在.");
            }
        }

        Resource resource = null;
        if (StringUtils.isNotBlank(code)) {
            resource = this.iGetReource(resourceType, code, null);
        }

        if (resource == null) {
            resource = new Resource();
            resource.setStatus(status);
        }

        resource.setName(name);
        resource.setCode(code);
        resource.setType(resourceType);
        resource.setParent(parentResource);
        this.saveOrUpdate(resource);
    }

    /**
     * 删除资源
     * @param resourceType 资源类型
     * @param code 资源编码
     */
    public void iDeleteResource(String resourceType, String code) {
        Resource resource = iGetReource(resourceType, code, null);
        if (resource == null) {
            throw new SystemException("编码[" + code + "]不存在.");
        }
        List<String> ids = Lists.newArrayList();
        ids.add(resource.getId());
        this.deleteByIds(ids);
    }

    /**
     *
     * @param resourceType 资源类型
     * @param code 资源编码
     * @param status
     * @return
     */
    public Resource iGetReource(String resourceType, String code, String status) {
        Validate.notNull(code, "参数[code]不能为null");
        Parameter parameter = new Parameter(code);
        StringBuffer hql = new StringBuffer();
        hql.append("from Resource r where r.code = :p1 ");
        if(resourceType != null){
            hql.append(" and r.type = :type");
            parameter.put("type",resourceType);
        }
        if(status != null){
            hql.append(" and r.status = :status ");
            parameter.put("status",status);
        }
        List<Resource> list = getEntityDao().find(hql.toString(), parameter);
        return list.isEmpty() ? null : list.get(0);
    }


    /**
     * 获取资源
     *
     * @return
     */
    public List<Resource> iGetResources(Integer resourceType) {
        Parameter parameter = new Parameter();
        StringBuffer hql = new StringBuffer();
        hql.append("from Resource r where 1=1");
        if(resourceType != null){
            hql.append(" and r.type = :type");
            parameter.put("type",resourceType);
        }
        List<Resource> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }

    /**    外部接口同步到资源  **/
}
