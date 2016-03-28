/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
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
     *
     * @param organIds 必须包含的机构ID
     * @param query 查询关键字
     * @return
     */
    public List<Organ> findWithInclude(List<String> organIds,String query,List<Integer> organTypes){
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
        List<Integer> organTypes = new ArrayList<Integer>(1);
        organTypes.add(OrganType.department.getValue());
        return findWithInclude(organIds,query,organTypes);
    }

    /**
     * 机构树
     * @return
     */
    public List<TreeNode> findOrganTree(){
        return findOrganTree(null,null);
    }

    /**
     * 机构树
     * @param parentId 顶级机构 查询所有为null
     * @return
     */
    @Cacheable(value = CacheConstants.ORGAN_USER_TREE_CACHE,condition = "#cacheable == true")
    public List<TreeNode> findOrganTree(String parentId,boolean cacheable){
        return findOrganTree(parentId,null);
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
        return findOrganUserTree(parentId,excludeOrganId,false,null,true);
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
                organs = this.findOwnerAndChildsOrgans(parentId);
            }else{
                organs = this.loadById(parentId).getSubOrgans();
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
            TreeNode treeNode = this.organToTreeNode(organ);
            if(addUser){
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
        TreeNode treeNode = new TreeNode(organ.getId(),organ.getName());
        if(StringUtils.isBlank(organ.get_parentId())){
            treeNode.setIconCls(ICON_ORGAN_ROOT);
        }else{
            treeNode.setIconCls(ICON_GROUP);
        }
        treeNode.setState(organ.getState());
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

}
