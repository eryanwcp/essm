/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Sets;
import com.eryansky.modules.sys._enum.OrganType;
import com.eryansky.modules.sys._enum.SexType;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.utils.CacheConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 机构Organ管理 Service层实现类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-09-09 下午21:26:46
 */
@Service
public class OrganManager extends EntityManager<Organ, String> {

    private static final String ICON_ORGAN_ROOT = "eu-icon-organ-root";
    private static final String ICON_USER_RED = "eu-icon-user_red";
    private static final String ICON_USER = "eu-icon-user";
    private static final String ICON_GROUP = "eu-icon-group";
    @Autowired
    private UserManager userManager;

    private HibernateDao<Organ, String> organDao;// 默认的泛型DAO成员变量.

    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        organDao = new HibernateDao<Organ, String>(sessionFactory, Organ.class);
    }

    @Override
    protected HibernateDao<Organ, String> getEntityDao() {
        return organDao;
    }

    /**
     * 保存或修改.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void saveOrUpdate(Organ entity) throws DaoException, SystemException,
            ServiceException {
        logger.debug("清空缓存:{}");
        Assert.notNull(entity, "参数[entity]为空!");
        organDao.saveOrUpdate(entity);
    }

    /**
     * 保存或修改.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void merge(Organ entity) throws DaoException, SystemException,
            ServiceException {
        Assert.notNull(entity, "参数[entity]为空!");
        organDao.merge(entity);
    }

    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    @Override
    public void saveEntity(Organ entity) throws DaoException, SystemException, ServiceException {
        super.saveEntity(entity);
    }

    /**
     * 删除(根据主键ID).
     *
     * @param id
     *            主键ID
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void deleteById(final String id){
        getEntityDao().delete(id);
    }

    /**
     * 自定义删除方法.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void deleteByIds(List<String> ids) throws DaoException, SystemException,
            ServiceException {
        super.deleteByIds(ids);
    }

    public List<Organ> findAllNormal(){
        StringBuffer hql = new StringBuffer();
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        hql.append(" from Organ o where o.status = :p1 order by  o.orderNo  asc");
        List<Organ> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }

    /**
     * 查询指定机构编码的数据
     * @param organCodes
     * @return
     */
    public List<Organ> findAll(List<String> organCodes){
        StringBuffer hql = new StringBuffer();
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        hql.append(" from Organ o where o.status = :p1");
        if(Collections3.isNotEmpty(organCodes)){
            hql.append(" and (");
            for(int i=0;i<organCodes.size();i++){
                String organCode = organCodes.get(i);
                if(i==0){
                    hql.append(" o.code like :organCode").append(i);
                }else{
                    hql.append(" or o.code like :organCode").append(i);
                }
                parameter.put("organCode"+i,organCode);
            }
            hql.append(")");
        }
        hql.append(" order by  o.orderNo  asc");
        List<Organ> list = getEntityDao().find(hql.toString(), parameter);
        return list;
    }

    /**
     *
     * @param organIds 必须包含的机构ID
     * @param query 查询关键字
     * @return
     */
    public List<Organ> findWithInclude(List<String> organIds,String query,List<String> organTypes){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from Organ o where o.status = :p1 ");
        if(Collections3.isEmpty(organIds) && StringUtils.isBlank(query)){
            hql.append(" and 1 <> 1 ");
        }else if(Collections3.isNotEmpty(organIds) && StringUtils.isNotBlank(query)){
            hql.append(" and (o.id in (:organIds) or o.name like :query )");
            parameter.put("organIds",organIds);
            parameter.put("query","%"+query+"%");
        }else{
            if(Collections3.isNotEmpty(organIds)){
                hql.append(" and o.id in (:organIds)");
                parameter.put("organIds",organIds);

            }else if(StringUtils.isNotBlank(query)){
                hql.append(" and o.name like :query ");
                parameter.put("query","%"+query+"%");
            }
        }
        if(Collections3.isNotEmpty(organTypes)){
            hql.append(" and o.type in (:organTypes)");
            parameter.put("organTypes",organTypes);
        }
        hql.append(" order by o.orderNo asc");
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     *
     * @param organIds 必须包含的部门ID
     * @param query
     * @return
     */
    public List<Organ> findDepartmensWithInclude(List<String> organIds,String query){
        List<String> organTypes = new ArrayList<String>(1);
        organTypes.add(OrganType.department.getValue());
        return findWithInclude(organIds,query,organTypes);
    }

    /**
     * 查找所有机构类型机构 {@link OrganType.organ}
     * @return
     */
    public List<Organ> findCompanyOrgans(){
        List<String> organTypes = new ArrayList<String>(1);
        organTypes.add(OrganType.organ.getValue());
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        StringBuilder hql = new StringBuilder();
        hql.append("from Organ o where o.status = :p1 ");
        if(Collections3.isNotEmpty(organTypes)){
            hql.append(" and o.type in (:organTypes)");
            parameter.put("organTypes",organTypes);
        }
        hql.append(" order by o.orderNo asc");
        List<Organ> list = organDao.find(hql.toString(), parameter);
        return list;
    }

    /**
     * 查找所有机构类型机构 {@link OrganType.organ}
     * @return
     */
    public List<TreeNode> findCompanyTree(){
        List<String> organTypes = new ArrayList<String>(1);
        organTypes.add(OrganType.organ.getValue());

        List<Organ> organs = this.findCompanyOrgans();

        List<TreeNode> tempTreeNodes = Lists.newArrayList();
        Map<String,TreeNode> tempMap = Maps.newLinkedHashMap();
        Iterator<Organ> iterator = organs.iterator();
        while (iterator.hasNext()){
            Organ organ = iterator.next();
            TreeNode treeNode = this.organToTreeNode(organ,null);
            tempTreeNodes.add(treeNode);
            tempMap.put(organ.getId(), treeNode);
        }

        Set<String> keyIds = tempMap.keySet();
        Iterator<String> iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            TreeNode treeNode = tempMap.get(iteratorKey.next());
            if(StringUtils.isNotBlank(treeNode.getpId())){
                TreeNode pTreeNode = getParentTreeNode(treeNode.getpId(), tempTreeNodes);
                if(pTreeNode != null){
                    pTreeNode.addChild(treeNode);
                    iteratorKey.remove();
                }
            }

        }

        List<TreeNode> result = Lists.newArrayList();
        keyIds = tempMap.keySet();
        iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            TreeNode treeNode = tempMap.get(iteratorKey.next());
            result.add(treeNode);

        }
        return result;
    }


    /**
     * 机构树
     * @return
     */
    public List<TreeNode> findOrganTree(){
        return findOrganTree(null, null);
    }

    /**
     * 机构树
     * @param parentId 顶级机构 查询所有为null
     * @return
     */
    @Cacheable(value = CacheConstants.ORGAN_USER_TREE_CACHE,condition = "#cacheable == true")
    public List<TreeNode> findOrganTree(String parentId,boolean cacheable){
        return findOrganTree(parentId, null);
    }

    public List<TreeNode> findOrganTree(String parentId,boolean cacheable,boolean cascade){
        return findOrganUserTree(parentId, null, false, null, cascade);
    }


    /**
     * 机构树
     * @param parentId 顶级机构 查询所有为null
     * @param excludeOrganId 排除的机构ID
     * @return
     */
    public List<TreeNode> findOrganTree(String parentId,String excludeOrganId){
        return findOrganUserTree(parentId, excludeOrganId, false, null, true);
    }

    /**
     * 机构用户树
     * @param parentId 顶级机构 查询所有为null
     * @param addUser 是否在机构下添加用户
     * @return
     */
    public List<TreeNode> findOrganUserTree(String parentId,boolean addUser,boolean cascade){
        return  findOrganUserTree(parentId,null,addUser,null,cascade);
    }

    /**
     * 机构用户树
     * @param parentId 顶级机构 查询所有为null
     * @param checkedUserIds 选中的用户
     * @return
     */
    public List<TreeNode> findOrganUserTree(String parentId,List<String> checkedUserIds,boolean cascade){
        return  findOrganUserTree(parentId,null,true,checkedUserIds,cascade);
    }


    /**
     *
     * @param parentId 顶级机构 查询所有为null
     * @param excludeOrganId 排除的机构ID
     * @param addUser 是否在机构下添加用户
     * @param checkedUserIds 选中的用户（addUser为true时 有效）
     * @param cascade 级联
     * @return
     */
    public List<TreeNode> findOrganUserTree(String parentId,String excludeOrganId,boolean addUser,List<String> checkedUserIds,boolean cascade){
        List<Organ> organs = null;
        if(parentId == null){
            if(cascade){
                organs = this.findAllNormal();
            }else{
                organs = this.findRoots();
            }
        }else{
            if(cascade){
                organs = this.findChildsOrgans(parentId);
            }else{
                organs = this.findByParent(parentId,StatusState.NORMAL.getValue());
            }

        }

        List<TreeNode> tempTreeNodes = Lists.newArrayList();
        Map<String,TreeNode> tempMap = Maps.newLinkedHashMap();

        Iterator<Organ> iterator = organs.iterator();
        while (iterator.hasNext()){
            Organ organ = iterator.next();
            if(StringUtils.isNotBlank(excludeOrganId) && organ.getId().equals(excludeOrganId)){
                continue;
            }
            //排除下级机构
            if(StringUtils.isNotBlank(excludeOrganId) && organ.getParentIds() != null && organ.getParentIds().contains(excludeOrganId)){
                continue;
            }
            TreeNode treeNode = this.organToTreeNode(organ,addUser);
            if(cascade && addUser){
                List<User> organUsers = organ.getDefautUsers();
                if(Collections3.isNotEmpty(organUsers)){
                    for(User organUser:organUsers){
                        TreeNode userTreeNode = userToTreeNode(organUser);
                        if(Collections3.isNotEmpty(checkedUserIds)){
                            if(checkedUserIds.contains(userTreeNode.getId())){
                                userTreeNode.setChecked(true);
                            }
                        }
                        treeNode.addChild(userTreeNode);
                    }
                }
            }
            tempTreeNodes.add(treeNode);
            tempMap.put(organ.getId(), treeNode);
        }


        if(addUser) {
            if (parentId != null) {
                Organ parentOrgan = this.getById(parentId);
                if (parentOrgan != null) {
                    List<User> parentOrganUsers = parentOrgan.getUsers();
                    if (Collections3.isNotEmpty(parentOrganUsers)) {
                        for (User parentOrganUser : parentOrganUsers) {
                            TreeNode parentUserTreeNode = userToTreeNode(parentOrganUser);
                            if (Collections3.isNotEmpty(checkedUserIds)) {
                                if (checkedUserIds.contains(parentUserTreeNode.getId())) {
                                    parentUserTreeNode.setChecked(true);
                                }
                            }
                            tempTreeNodes.add(parentUserTreeNode);
                            tempMap.put(parentOrganUser.getId(), parentUserTreeNode);
                        }
                    }
                }
            }
        }

        Set<String> keyIds = tempMap.keySet();
        Iterator<String> iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            TreeNode treeNode = tempMap.get(iteratorKey.next());
            if(StringUtils.isNotBlank(treeNode.getpId())){
                TreeNode pTreeNode = getParentTreeNode(treeNode.getpId(), tempTreeNodes);
                if(pTreeNode != null){
                    pTreeNode.addChild(treeNode);
                    iteratorKey.remove();
                }
            }

        }

        List<TreeNode> result = Lists.newArrayList();
        keyIds = tempMap.keySet();
        iteratorKey = keyIds.iterator();
        while (iteratorKey.hasNext()){
            TreeNode treeNode = tempMap.get(iteratorKey.next());
            result.add(treeNode);

        }
        return result;
    }

    /**
     * 机构转TreeNode
     * @param organ 机构
     * @return
     */
    public TreeNode organToTreeNode(Organ organ){
        return organToTreeNode(organ, null);
    }

    /**
     * 机构转TreeNode
     * @param organ 机构
     * @param addUser 状态栏考虑用户
     * @return
     */
    public TreeNode organToTreeNode(Organ organ,Boolean addUser){
        TreeNode treeNode = new TreeNode(organ.getId(),organ.getName());
        if(StringUtils.isBlank(organ.get_parentId())){
            treeNode.setIconCls(ICON_ORGAN_ROOT);
        }else{
            treeNode.setIconCls(ICON_GROUP);
        }
        if(addUser != null && addUser){
            treeNode.setState(organ.getState2());
        }else{
            treeNode.setState(organ.getState());
        }
        treeNode.setpId(organ.get_parentId());
        treeNode.addAttributes("code", organ.getCode());
        treeNode.addAttributes("sysCode", organ.getSysCode());
        treeNode.addAttributes("type", organ.getType());
        treeNode.addAttributes("nType", "o");//节点类型 机构
        return treeNode;
    }

    /**
     * 用户转TreeNode
     * @param user 用户
     * @return
     */
    public TreeNode userToTreeNode(User user){
        TreeNode treeNode = new TreeNode(user.getId(),user.getName());
        if(SexType.girl.getValue().equals(user.getSex())){
            treeNode.setIconCls(ICON_USER_RED);
        }else{
            treeNode.setIconCls(ICON_USER);
        }
        treeNode.addAttributes("nType", "u");//节点类型 用户
        treeNode.addAttributes("loginName", user.getLoginName());//节点类型 用户
        return treeNode;
    }


    /**
     * 用户转TreeNode
     * @param users 用户
     * @return
     */
    public List<TreeNode> userToTreeNode(List<User> users){
        List<TreeNode> treeNodes = Lists.newArrayList();
        for(User user:users){
            treeNodes.add(userToTreeNode(user));
        }
        return treeNodes;
    }

    /**
     * 查找父级节点
     * @param parentId
     * @param treeNodes
     * @return
     */
    public TreeNode getParentTreeNode(String parentId, List<TreeNode> treeNodes){
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
     * 同步所有机构ID
     */
    public void syncAllParentIds() {
        List<Organ> rootOrgans = this.findRoots();
        updateParentIds(rootOrgans);
    }

    /**
     * 递归
     * @param organs
     */
    private void updateParentIds(List<Organ> organs){
        for(Organ organ:organs){
            organ.syncParentIds2();
            this.update(organ);
            updateParentIds(organ.getSubOrgans());
        }
    }

    /**
     *
     * 根据系统编码得到Organ.
     *
     * @param sysCode
     *            机构系统编码
     * @return
     */
    public Organ getBySysCode(String sysCode)  {
        if (StringUtils.isBlank(sysCode)) {
            return null;
        }
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),sysCode);
        StringBuffer hql = new StringBuffer();
        hql.append("from Organ o  where o.status = :p1 and o.sysCode = :p2");
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<Organ> list = query.list();
        return  list.isEmpty() ? null:list.get(0);
    }

    /**
     *
     * 根据编码得到Organ.
     *
     * @param code
     *            机构编码
     * @return
     */
    public Organ getByCode(String code){
        if (StringUtils.isBlank(code)) {
            return null;
        }
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),code);
        StringBuffer hql = new StringBuffer();
        hql.append("from Organ o  where o.status = :p1 and o.code = :p2");
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setMaxResults(1);
        List<Organ> list = query.list();
        return  list.isEmpty() ? null:list.get(0);
    }

    /**
     * 查找根节点
     * @return
     */
    public List<Organ> findRoots(){
        return findByParent(null, null);
    }
    /**
     *
     * 根据父ID得到 Organ. <br>
     * 默认按 orderNo asc,id asc排序.
     *
     * @param parentId
     *            父节点ID(当该参数为null的时候查询顶级机构列表)
     * @return
     */
    public List<Organ> findByParent(String parentId){
        return findByParent(parentId,null);
    }
    /**
     *
     * 根据父ID得到 Organ. <br>
     * 默认按 orderNo asc,id asc排序.
     *
     * @param parentId
     *            父节点ID(当该参数为null的时候查询顶级机构列表)
     * @param status
     *            数据状态 @see com.eryansky.common.orm.entity.StatusState
     *            <br>status传null则使用默认值 默认值:StatusState.NORMAL.getValue()
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Organ> findByParent(String parentId, String status){
        //默认值 正常
        if(status == null){
            status = StatusState.NORMAL.getValue();
        }
        StringBuilder sb = new StringBuilder();
        Parameter parameter = new Parameter();
        sb.append("from Organ o where o.status = :status  ");
        parameter.put("status", status);
        sb.append(" and o.parent");
        if (parentId == null) {
            sb.append(" is null ");
        } else {
            sb.append(".id = :parentId ");
            parameter.put("parentId", parentId);
        }
        sb.append(" order by o.orderNo asc");

        List<Organ> list = organDao.find(sb.toString(), parameter);
        return list;
    }

    /**
     * 数据列表
     * @param parentId
     * @param status
     * @return
     */
    public List<Organ> findDataByParent(String parentId, List<String> status){
        //默认值 正常
        if(Collections3.isEmpty(status)){
            status = new ArrayList<String>(3);
            status.add(StatusState.NORMAL.getValue());
            status.add(StatusState.LOCK.getValue());
            status.add(StatusState.AUDIT.getValue());
        }
        StringBuilder sb = new StringBuilder();
        Parameter parameter = new Parameter();
        sb.append("from Organ o where o.status in (:status)  ");
        parameter.put("status", status);
        sb.append(" and o.parent");
        if (StringUtils.isBlank(parentId)) {
            sb.append(" is null ");
        } else {
            sb.append(".id = :parentId ");
            parameter.put("parentId", parentId);
        }
        sb.append(" order by o.orderNo asc");

        List<Organ> list = organDao.find(sb.toString(), parameter);
        return list;
    }


    /**
     * 得到排序字段的最大值.
     *
     * @return 返回排序字段的最大值
     */
    public Integer getMaxSort() throws DaoException, SystemException,
            ServiceException {
        Iterator<?> iterator = organDao.createQuery(
                "select max(o.orderNo) from Organ o ").iterate();
        Integer max = 0;
        while (iterator.hasNext()) {
            max = (Integer) iterator.next();
            if (max == null) {
                max = 0;
            }
        }
        return max;
    }

    /**
     * 根据ID查找
     * @param organIds 机构ID集合
     * @return
     */
    public List<Organ> findOrgansByIds(List<String> organIds) {
        Parameter parameter = new Parameter(organIds);
        return getEntityDao().find("from Organ o where o.id in :p1",parameter);
    }


    /**
     * 查找当前部门下所有用户ID
     * @param organId
     * @return
     */
    public List<String> findOrganUserIds(String organId) {
        Parameter parameter = new Parameter(organId,StatusState.NORMAL.getValue());
        StringBuilder hql = new StringBuilder();
        hql.append("select u.id from User u where u.defaultOrganId in (select o.id from Organ o where o.id = :p1) and u.status = :p2");
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<Organ> findChildsOrgans(String id){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        Organ organ = this.loadById(id);
        StringBuilder hql = new StringBuilder();
        hql.append("from Organ o where o.status = :p1 ");
        if(organ != null){
            hql.append(" and o.parentIds like  :parentIds ");
            parameter.put("parentIds","%," + id + ",%");
        }
        hql.append(" order by o.orderNo asc");
        List<Organ> list = organDao.find(hql.toString(), parameter);
        return list;
    }

    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<String> findChildsOrganIds(String id){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue());
        Organ organ = this.loadById(id);
        StringBuilder hql = new StringBuilder();
        hql.append("select o.id from Organ o where o.status = :p1 ");
        if(organ != null){
            hql.append(" and o.parentIds like  :parentIds ");
            parameter.put("parentIds","%," + id + ",%");
        }
        hql.append(" order by o.orderNo asc");
        List<String> list = organDao.find(hql.toString(), parameter);
        return list;
    }

    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<Organ> findOwnerAndChildsOrgans(String id){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),id);
        Organ organ = this.loadById(id);
        StringBuilder hql = new StringBuilder();
        hql.append("from Organ o where o.status = :p1  and (o.id = :p2 ");
        if(organ != null){
            hql.append(" or o.parentIds like  :parentIds ");
            parameter.put("parentIds","%," + id + ",%");
        }
        hql.append(" ) ");
        hql.append(" order by o.orderNo asc");
        List<Organ> list = organDao.find(hql.toString(), parameter);
        return list;
    }


    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<String> findOwnerAndChildsOrganIds(String id){
        Parameter parameter = new Parameter(StatusState.NORMAL.getValue(),id);
        Organ organ = this.loadById(id);
        StringBuilder hql = new StringBuilder();
        hql.append("select o.id from Organ o where o.status = :p1  and (o.id = :p2 ");
        if(organ != null){
            hql.append(" or o.parentIds like  :parentIds ");
            parameter.put("parentIds","%," + id + ",%");
        }
        hql.append(" ) ");
        hql.append(" order by o.orderNo asc");
        List<String> list = organDao.find(hql.toString(), parameter);
        return list;
    }



    /**
     * 所在机构下的所有子部门（不包含下级机构类型的数据）
     * @param anyOrganId 机构/部门 ID
     * @return
     */
    public List<Organ> findOrganChildsDepartmentOrgans(String anyOrganId){
        if(StringUtils.isBlank(anyOrganId)){
            return  null;
        }
        Organ organ = this.loadById(anyOrganId);
        if(organ == null){
            return null;
        }
        return findChildsDepartmentOrgans(organ.getCompanyId());
    }

    /**
     * 所在机构下的所有子部门（不包含下级机构类型的数据）
     * @param anyOrganId 机构/部门 ID
     * @return
     */
    public List<String> findOrganChildsDepartmentOrganIds(String anyOrganId){
        if(StringUtils.isBlank(anyOrganId)){
            return  null;
        }
        Organ organ = this.loadById(anyOrganId);
        if(organ == null){
            return null;
        }
        return findChildsDepartmentOrganIds(organ.getCompanyId());
    }

    /**
     * 本机构下的所有子部门（不包含下级机构类型的数据）
     * @param id 机构ID
     * @return
     */
    public List<Organ> findChildsDepartmentOrgans(String id){
        List<Organ> organs = findChildsOrgans(id);
        List<Organ> list = Lists.newArrayList();
        for(Organ organ:organs){
            if(OrganType.department.getValue().equals(organ.getType()) && organ.getCompanyId().equals(id)){
                list.add(organ);
            }
        }
        return list;
    }

    /**
     * 本机构下的所有子部门（不包含下级机构类型的数据）
     * @param id 机构ID
     * @return
     */
    public List<String> findChildsDepartmentOrganIds(String id){
        List<Organ> organs = findChildsOrgans(id);
        List<String> list = Lists.newArrayList();
        for(Organ organ:organs){
            if(OrganType.department.getValue().equals(organ.getType()) && organ.getCompanyId().equals(id)){
                list.add(organ.getId());
            }
        }
        return list;
    }

    /**
     * 所在部门下的所有子部门（不包含下级机构类型的数据）
     * @param anyOrganId 机构/部门 ID
     * @return
     */
    public List<Organ> findOrganChildsOfficeOrgans(String anyOrganId){
        if(StringUtils.isBlank(anyOrganId)){
            return  null;
        }
        Organ organ = this.loadById(anyOrganId);
        if(organ == null){
            return null;
        }
        return findChildsDepartmentOrgans(organ.getOfficeId());
    }

    /**
     * 所在部门下的所有子部门（不包含下级机构类型的数据）
     * @param anyOrganId 机构/部门 ID
     * @return
     */
    public List<String> findOrganChildsOfficeOrganIds(String anyOrganId){
        if(StringUtils.isBlank(anyOrganId)){
            return  null;
        }
        Organ organ = this.loadById(anyOrganId);
        if(organ == null){
            return null;
        }
        return findChildsDepartmentOrganIds(organ.getOfficeId());
    }

    /**
     * 本部门下的所有子部门（不包含下级机构类型的数据）
     * @param id 机构ID
     * @return
     */
    public List<Organ> findChildsOfficeOrgans(String id){
        List<Organ> organs = findChildsOrgans(id);
        List<Organ> list = Lists.newArrayList();
        for(Organ organ:organs){
            if(organ.getOfficeId().equals(id)){
                list.add(organ);
            }
        }
        return list;
    }

    /**
     * 本部门下的所有子部门（不包含下级机构类型的数据）
     * @param id 机构ID
     * @return
     */
    public List<String> findChildsOfficeOrganIds(String id){
        List<Organ> organs = findChildsOrgans(id);
        List<String> list = Lists.newArrayList();
        for(Organ organ:organs){
            if(organ.getOfficeId().equals(id)){
                list.add(organ.getId());
            }
        }
        return list;
    }


    /**
     * 快速查找方法
     *
     * @param unionUsers
     * @return
     */
    public List<TreeNode> getOrganUserTreeDataFast(Collection<String> unionUsers) throws ServiceException {
        int minGrade = 0;
        int maxGrade = 0;
        Map<Integer, Set<Organ>> organMap = Maps.newHashMap();// 树层级 机构
        Map<String, List<User>> userMap = Maps.newHashMap();// 机构ID 用户
        if (Collections3.isNotEmpty(unionUsers)) {
            for (String userId : unionUsers) {
                User user = userManager.loadById(userId);
                Organ userOrgan = user.getDefaultOrgan();
                if (userOrgan == null) {
                    throw new ServiceException(Result.ERROR, user.getName() + "未设置默认机构.", null);
                }
                Set<Organ> organs = organMap.get(userOrgan.getGrade());
                if (Collections3.isEmpty(organs)) {
                    organs = Sets.newHashSet();
                }
                organs.add(userOrgan);
                organMap.put(userOrgan.getGrade(), organs);

                List<User> users = userMap.get(userOrgan.getId());
                if (Collections3.isEmpty(users)) {
                    users = Lists.newArrayList();
                }
                users.add(user);
                userMap.put(userOrgan.getId(), users);
                if (maxGrade < userOrgan.getGrade()) {
                    maxGrade = userOrgan.getGrade();
                }
                if (minGrade > userOrgan.getGrade()) {
                    minGrade = userOrgan.getGrade();
                }

            }
        }
        List<Integer> gradeKeys = Lists.newArrayList(organMap.keySet());
        Collections.sort(gradeKeys);
        //补全上级机构
        if(gradeKeys.size() >1){
            for (Integer grade : gradeKeys) {
                Set<Organ> organs = organMap.get(grade);
                for (Organ rs : organs) {
                    Organ iOrgan = rs;
                    int add = 1;
                    while (iOrgan != null && iOrgan.getGrade() > minGrade){
                        iOrgan = iOrgan.getParent();
                        if(iOrgan != null){
                            Set<Organ> _organs = organMap.get(minGrade + add);
                            if (Collections3.isEmpty(_organs)) {
                                _organs = Sets.newHashSet();
                            }
                            _organs.add(iOrgan);
                            organMap.put(minGrade + add, _organs);
                            add ++;
                        }

                    }
                }
            }
        }

        List<TreeNode> tempTreeNodes = Lists.newArrayList();
        Map<String,TreeNode> tempMap = Maps.newLinkedHashMap();

        gradeKeys = Lists.newArrayList(organMap.keySet());
        Collections.sort(gradeKeys);
        for (Integer grade : gradeKeys) {
            Set<Organ> organs = organMap.get(grade);
            for (Organ rs : organs) {
                TreeNode organTreeNode = new TreeNode(rs.getId(), rs.getName());
                organTreeNode.setpId(rs.get_parentId());
                Map<String, Object> attributes = Maps.newHashMap();
                attributes.put("nType", "o");
                attributes.put("type", rs.getType());
                attributes.put("sysCode", rs.getSysCode());
                organTreeNode.setAttributes(attributes);
                organTreeNode.setIconCls(ICON_GROUP);
                organTreeNode.setNocheck(true);
                List<User> userList = userMap.get(rs.getId());
                if (Collections3.isNotEmpty(userList)) {
                    Collections.sort(userList, new Comparator<User>() {

                        @Override
                        public int compare(User u1, User u2) {
                            if (u1.getOrderNo() > u2.getOrderNo()) {
                                return 1;
                            } else if(u1.getOrderNo() < u2.getOrderNo()) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }

                    });
                    for (User user : userList) {
                        TreeNode userNode = new TreeNode(user.getId(), user.getName());
                        Map<String, Object> userAttributes = Maps.newHashMap();
                        userAttributes.put("nType", "u");
                        userNode.setAttributes(userAttributes);
                        if (SexType.girl.getValue().equals(user.getSex())) {
                            userNode.setIconCls(ICON_USER_RED);
                        } else {
                            userNode.setIconCls(ICON_USER);
                        }
                        organTreeNode.addChild(userNode);
                    }
                }
                boolean flag = true;
                for(TreeNode treeNode0:tempTreeNodes){
                    if(treeNode0.getId().equals(organTreeNode.getId())){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    tempTreeNodes.add(organTreeNode);
                }
                tempMap.put(organTreeNode.getId(), organTreeNode);
            }
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
     * 迭代筛选机构用户树只剩下需要的用户
     *
     * @param organUserTree
     * @param unionUsers
     *            需要筛选的用户
     * @return
     */
    private void iteratTree(List<TreeNode> newTree, List<TreeNode> organUserTree, Set<String> unionUsers) {
        for (TreeNode treeNode : organUserTree) {
            if ("u".equals(treeNode.getAttributes().get("nType"))) { // 判断当前节点是否为机构
                if (unionUsers.contains(treeNode.getId())) { // 如果需要筛选的包含的ID
                    newTree.add(treeNode); // 保留树结构
                }
            } else {// 子节点为用户
                List<TreeNode> childrens = treeNode.getChildren();
                if (Collections3.isNotEmpty(childrens)) { // 结构节点包含子节点
                    List<TreeNode> newNode = Lists.newArrayList();
                    iteratTree(newNode, childrens, unionUsers); // 迭代当前结构的子节点
                    if (Collections3.isNotEmpty(newNode)) {
                        treeNode.setChildren(newNode);
                        newTree.add(treeNode); // 添加新结构
                    }
                }
            }
        }
    }

}
