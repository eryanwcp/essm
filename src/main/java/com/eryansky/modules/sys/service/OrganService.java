/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm._enum.StatusState;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.orm.mybatis.service.TreeService;
import com.eryansky.modules.sys._enum.OrganType;
import com.eryansky.modules.sys._enum.SexType;
import com.eryansky.modules.sys.mapper.OrganExtend;
import com.eryansky.modules.sys.mapper.Resource;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.AppUtils;
import com.eryansky.utils.CacheConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.dao.OrganDao;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 机构表 service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@Service
@Transactional(readOnly = true)
public class OrganService extends TreeService<OrganDao, Organ> {

    private static final String ICON_ORGAN_ROOT = "eu-icon-organ-root";
    private static final String ICON_USER_RED = "eu-icon-user_red";
    private static final String ICON_USER = "eu-icon-user";
    private static final String ICON_GROUP = "eu-icon-group";

    @Autowired
    private UserService userService;


    /**
     * 保存或修改.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    @Transactional(readOnly = false)
    public Organ saveOrgan(Organ entity) {
        logger.debug("清空缓存:{}");
        Assert.notNull(entity, "参数[entity]为空!");
        super.save(entity);
        return entity;
    }

    /**
     * 删除(根据主键ID).
     *
     * @param id
     *            主键ID
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    @Transactional(readOnly = false)
    public String deleteById(final String id){
        dao.delete(new Organ(id));
        logger.debug("清空缓存:{}", CacheConstants.ORGAN_USER_TREE_CACHE);
        return id;
    }

    /**
     * 自定义删除方法.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    @Transactional(readOnly = false)
    public void deleteByIds(Collection<String> ids) {
        for(String id:ids){
            deleteById(id);
        }
    }

    /**
     * 自定义删除方法.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    @Transactional(readOnly = false)
    public void deleteOwnerAndChilds(String id){
        dao.deleteOwnerAndChilds(new Organ(id));
        logger.debug("清空缓存:{}", CacheConstants.ORGAN_USER_TREE_CACHE);
    }

    /**
     * 递归
     * @param organs
     */
    @Transactional(readOnly = false)
    public void updateParentIds(Collection<Organ> organs){
        for(Organ organ:organs){
            dao.updateParentIds(organ);
        }
    }

    /**
     * 同步所有机构ID
     */
    @Transactional(readOnly = false)
    public void syncAllParentIds() {
        List<Organ> rootOrgans = this.findRoots();
        updateParentIds(rootOrgans);
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
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("code",code);
        return  dao.getByCode(parameter);
    }

    /**
     *
     * 根据编码得到Organ.
     *
     * @param code
     *            机构编码
     * @return
     */
    public Organ getDeleteByIdOrCode(String id,String code){
        if (StringUtils.isBlank(code)) {
            return null;
        }
        Parameter parameter = new Parameter();
        parameter.put("id",id);
        parameter.put("code",code);
        return  dao.getDeleteByIdOrCode(parameter);
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
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("sysCode",sysCode);
        return  dao.getBySysCode(parameter);
    }


    /**
     * 查找所有
     * @return
     */
    public List<Organ> findAllNormal(){
        List<Organ> list = dao.findAllList(new Organ());
        return list;
    }

    /**
     * 查找所有（包括已删除）
     * @return
     */
    public List<Organ> findAllWithDelete(){
        Organ entity = new Organ();
        entity.setStatus(null);
        List<Organ> list = dao.findAllList(entity);
        return list;
    }

    /**
     *
     * @param organIds 必须包含的机构ID
     * @param query 查询关键字
     * @param types 机构类型
     * @return
     */
    public List<Organ> findWithInclude(Collection<String> organIds, String query, Collection<String> types){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("ids",organIds);
        parameter.put("types", types);
        parameter.put("query",query);
        return dao.findWithInclude(parameter);
    }

    /**
     *
     * @param organIds 必须包含的部门ID
     * @param query
     * @return
     */
    public List<Organ> findDepartmensWithInclude(Collection<String> organIds, String query){
        List<String> organTypes = new ArrayList<String>(1);
        organTypes.add(OrganType.department.getValue());
        return findWithInclude(organIds,query,organTypes);
    }

    /**
     * 查找所有单位
     * @return
     */
    public List<Organ> findCompanys(){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("type",OrganType.organ.getValue());
        return dao.findCustomQuery(parameter);
    }

    /**
     * 查找所有单位 树形
     * @return
     */
    public List<TreeNode> findCompanysTree(){
        List<Organ> organs = this.findCompanys();
        List<TreeNode> tempTreeNodes = Lists.newArrayList();
        Iterator<Organ> iterator = organs.iterator();
        while (iterator.hasNext()){
            Organ organ = iterator.next();
            TreeNode treeNode = this.organToTreeNode(organ,false,true);
            tempTreeNodes.add(treeNode);
        }
        List<TreeNode> result = AppUtils.toTreeTreeNodes(tempTreeNodes);
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
    public List<TreeNode> findOrganUserTree(String parentId,Collection<String> checkedUserIds,boolean cascade){
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
    public List<TreeNode> findOrganUserTree(String parentId,String excludeOrganId,boolean addUser,Collection<String> checkedUserIds,boolean cascade){
        List<Organ> organs = null;
        if(parentId == null){
            if(cascade){
                organs = this.findAllNormal();
            }else{
                organs = this.findRoots();
            }
        }else{
            if(cascade){
                organs = this.findOwnerAndChilds(parentId);
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
                List<User> organUsers = userService.findOrganDefaultUsers(organ.getId());
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
                Organ parentOrgan = this.get(parentId);
                if (parentOrgan != null) {
                    List<User> parentOrganUsers = userService.findOrganDefaultUsers(parentOrgan.getId());
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
                TreeNode pTreeNode = getParentTreeNode(treeNode.getpId(),(String)treeNode.getAttributes().get("nType"), tempTreeNodes);
                if(pTreeNode != null){
                    pTreeNode.setState(TreeNode.STATE_CLOASED);
                    if(cascade && Collections3.isEmpty(treeNode.getChildren())){
                        treeNode.setState(TreeNode.STATE_OPEN);
                    }
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
            if(cascade && Collections3.isEmpty(treeNode.getChildren())){
                treeNode.setState(TreeNode.STATE_OPEN);
            }
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
    public TreeNode organToTreeNode(Organ organ, Boolean addUser){
        return organToTreeNode(organ,addUser,null);
    }


    /**
     * 机构转TreeNode
     * @param organ 机构
     * @param addUser 状态栏考虑用户
     * @return
     */
    public TreeNode organToTreeNode(Organ organ, Boolean addUser,Boolean onlyCompany){
        TreeNode treeNode = new TreeNode(organ.getId(),organ.getName());
        if(StringUtils.isBlank(organ.getParentId()) || "0".equals(organ.getParentId())){
            treeNode.setIconCls(ICON_ORGAN_ROOT);
        }else{
            treeNode.setIconCls(ICON_GROUP);
        }
        if(addUser != null && addUser){
            List<Organ> childOrgans = findChild(organ.getId());
            List<User> childUsers = userService.findOrganUsers(organ.getId());
            treeNode.setState((Collections3.isNotEmpty(childOrgans) || Collections3.isNotEmpty(childUsers)) ? TreeNode.STATE_CLOASED:TreeNode.STATE_OPEN);
        }else if(onlyCompany != null && onlyCompany){
            List<Organ> childCompanys = findChildCompany(organ.getId());
            treeNode.setState(Collections3.isNotEmpty(childCompanys)? TreeNode.STATE_CLOASED:TreeNode.STATE_OPEN);
        }else{
            treeNode.setState(organ.getState());
        }
        treeNode.setpId(organ.getParentId());
        treeNode.addAttribute("code", organ.getCode());
//        treeNode.addAttribute("sysCode", organ.getSysCode());
        treeNode.addAttribute("type", organ.getType());
        treeNode.addAttribute("nType", "o");//节点类型 机构
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
        treeNode.addAttribute("nType", "u");//节点类型 用户
        treeNode.addAttribute("loginName", user.getLoginName());//节点类型 用户
        return treeNode;
    }


    /**
     * 用户转TreeNode
     * @param users 用户
     * @return
     */
    public List<TreeNode> userToTreeNode(Collection<User> users){
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
    public TreeNode getParentTreeNode(String parentId, Collection<TreeNode> treeNodes){
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
     * @param treeNodes
     * @return
     */
    public TreeNode getParentTreeNode(String parentId,String type, Collection<TreeNode> treeNodes){
        TreeNode t = null;
        for(TreeNode treeNode:treeNodes){
            String _type = (String) treeNode.getAttributes().get("nType");
            if(parentId.equals(treeNode.getId()) && _type != null && type != null && _type.equals(type)){
                t = treeNode;
                break;
            }
        }
        return t;
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
     * 默认按 sort asc排序.
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
     * 默认按 sort asc排序.
     *
     * @param parentId
     *            父节点ID(当该参数为null的时候查询顶级机构列表)
     * @param status
     *            数据状态 @see com.eryansky.common.orm._enum.StatusState
     *            <br>status传null则使用默认值 默认值:StatusState.NORMAL.getValue()
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Organ> findByParent(String parentId, String status){
        //默认值 正常
        if(status == null){
            status = StatusState.NORMAL.getValue();
        }
        List<String> list = Lists.newArrayList();
        list.add(status);
        return findDataByParent(parentId,list);
    }

    /**
     * 数据列表
     * @param parentId
     * @param status
     * @return
     */
    public List<Organ> findDataByParent(String parentId, Collection<String> status){
        //默认值 正常
        if(Collections3.isEmpty(status)){
            status = new ArrayList<String>(3);
            status.add(StatusState.NORMAL.getValue());
            status.add(StatusState.LOCK.getValue());
            status.add(StatusState.AUDIT.getValue());
        }
        return findChild(parentId,status,null);
    }


    public List<Organ> findChild(String parentId){
        //默认值 正常
        List<String> status = Lists.newArrayList();
        status.add(StatusState.NORMAL.getValue());
        return findChild(parentId,status,null);
    }

    public List<Organ> findChildCompany(String parentId){
        //默认值 正常
        List<String> status = Lists.newArrayList();
        status.add(StatusState.NORMAL.getValue());
        List<String> types = Lists.newArrayList();
        types.add(OrganType.organ.getValue());
        return findChild(parentId,status,types);
    }



    public List<Organ> findChild(String parentId,Collection<String> types){
        //默认值 正常
        List<String> list = Lists.newArrayList();
        list.add(StatusState.NORMAL.getValue());
        return findChild(parentId,list,types);
    }


    /**
     * 数据列表
     * @param parentId
     * @param status
     * @param types
     * @return
     */
    public List<Organ> findChild(String parentId,Collection<String> status,Collection<String> types){
        Parameter parameter = new Parameter();
        parameter.put("parentId",parentId);
        parameter.put(DataEntity.FIELD_STATUS,status);
        parameter.put("types",types);
        return dao.findChild(parameter);
    }

    /**
     * 查找子节点数量
     * @param parentId
     * @return
     */
    public Integer findChildCount(String parentId){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("parentId",parentId);
        return dao.findChildCount(parameter);
    }



    public List<String> findChildIds(String parentId){
        return findChildIds(parentId,null);
    }

    public List<String> findChildIds(String parentId, Collection<String> status){
        //默认值 正常
        if(Collections3.isEmpty(status)){
            status = new ArrayList<String>(1);
            status.add(StatusState.NORMAL.getValue());
        }
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,status);
        parameter.put("parentId",parentId);
        return dao.findChildIds(parameter);
    }



    /**
     * 得到排序字段的最大值.
     *
     * @return 返回排序字段的最大值
     */
    public Integer getMaxSort(){
        Integer max = dao.getMaxSort();
        return max == null ? 0:max;
    }

    /**
     * 根据ID查找
     * @param organIds 机构ID集合
     * @return
     */
    public List<Organ> findOrgansByIds(Collection<String> organIds) {
        Parameter parameter = new Parameter();
        parameter.put("organIds",organIds);
        return dao.findOrgansByIds(parameter);
    }


    /**
     * 查找当前部门下所有用户ID
     * @param organId
     * @return
     */
    public List<String> findOrganUserIds(String organId) {
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findOrganUserIds(parameter);
    }


    /**
     * 查找用户所属机构
     * @param userId 用户ID
     * @return
     */
    public List<Organ> findOrgansByUserId(String userId) {
        Parameter parameter = new Parameter();
        parameter.put("userId",userId);
        return dao.findOrgansByUserId(parameter);
    }

    /**
     * 查找用户所属机构IDS
     * @param userId 用户ID
     * @return
     */
    public List<String> findOrganIdsByUserId(String userId) {
        Parameter parameter = new Parameter();
        parameter.put("userId",userId);
        return dao.findOrganIdsByUserId(parameter);
    }


    /**
     * 查找岗位关联的机构信息
     * @param postId 岗位ID
     * @return
     */
    public List<Organ> findAssociationOrgansByPostId(String postId) {
        Parameter parameter = new Parameter();
        parameter.put("postId",postId);
        return dao.findAssociationOrgansByPostId(parameter);
    }

    /**
     * 查找岗位关联的机构信息IDS
     * @param postId 岗位ID
     * @return
     */
    public List<String> findAssociationOrganIdsByPostId(String postId) {
        if(StringUtils.isBlank(postId)){
            return Lists.newArrayList();
        }
        Parameter parameter = new Parameter();
        parameter.put("postId",postId);
        return dao.findAssociationOrganIdsByPostId(parameter);
    }


    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<Organ> findChilds(String id){
        return findChilds(id,null);
    }

    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @param types 机构类型
     * @return
     */
    public List<Organ> findChilds(String id,Collection<String> types){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.put("id",id);
        parameter.put("types",types);
        return dao.findChilds(parameter);
    }

    /**
     * 查找下级单位
     * @param id 机构ID
     * @return
     */
    public List<Organ> findChildsCompanys(String id){
        List<String> types = Lists.newArrayList();
        types.add(OrganType.organ.getValue());
        return findChilds(id,types);
    }

    /**
     * 查找下级直属部门
     * @param id 机构ID
     * @return
     */
    public List<Organ> findChildsDepartments(String id){
        List<String> types = Lists.newArrayList();
        types.add(OrganType.department.getValue());
        return findChilds(id,types);
    }


    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<String> findChildsIds(String id){
        return findChildsIds(id,null);
    }

    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @param types 机构类型
     * @return
     */
    public List<String> findChildsIds(String id,Collection<String> types){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,AppConstants.getJdbcType());
        parameter.put("id",id);
        parameter.put("types",types);
        return dao.findChildsIds(parameter);
    }

    /**
     * 查找下级直属单位IDS
     * @param id 机构ID
     * @return
     */
    public List<String> findChildsCompanyIds(String id){
        List<String> types = Lists.newArrayList();
        types.add(OrganType.organ.getValue());
        return findChildsIds(id,types);
    }

    /**
     * 查找下级直属部门IDS
     * @param id 机构ID
     * @return
     */
    public List<String> findChildsDepartmentIds(String id){
        List<String> types = Lists.newArrayList();
        types.add(OrganType.department.getValue());
        return findChildsIds(id,types);
    }



    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<Organ> findOwnerAndChilds(String id){
        return findOwnerAndChilds(id,null);
    }

    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @param types 机构类型
     * @return
     */
    public List<Organ> findOwnerAndChilds(String id,List<String> types){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,AppConstants.getJdbcType());
        parameter.put("id",id);
        parameter.put("types",types);
        return dao.findOwnerAndChilds(parameter);
    }




    /**
     * 查找本机以及下级所有单位
     * @param id 机构ID
     * @return
     */
    public List<Organ> findOwnerAndChildsCompanys(String id){
        List<String> types = Lists.newArrayList();
        types.add(OrganType.organ.getValue());
        return findOwnerAndChilds(id,types);
    }

    /**
     * 查找本机以及下级所有单位
     * @param id 机构ID
     * @return
     */
    public List<TreeNode> findOwnerAndChildsCompanysTree(String id){
        List<Organ> organs = this.findOwnerAndChildsCompanys(id);
        List<TreeNode> tempTreeNodes = Lists.newArrayList();
        Iterator<Organ> iterator = organs.iterator();
        while (iterator.hasNext()){
            Organ organ = iterator.next();
            TreeNode treeNode = this.organToTreeNode(organ);
            tempTreeNodes.add(treeNode);
        }

        return AppUtils.toTreeTreeNodes(tempTreeNodes);
    }

    /**
     * 查找下级所有部门
     * @param id 机构ID
     * @return
     */
    public List<Organ> findOwnerAndChildsDepartments(String id){
        List<String> types = Lists.newArrayList();
        types.add(OrganType.department.getValue());
        return findOwnerAndChilds(id,types);
    }



    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<String> findOwnerAndChildsIds(String id){
        return findOwnerAndChildsIds(id,null);
    }

    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @param types 机构类型
     * @return
     */
    public List<String> findOwnerAndChildsIds(String id,Collection<String> types){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,AppConstants.getJdbcType());
        parameter.put("id",id);
        parameter.put("types",types);
        return dao.findOwnerAndChildsIds(parameter);
    }

    /**
     * 查找下级所有单位IDS
     * @param id 机构ID
     * @return
     */
    public List<String> findOwnerAndChildsCompanyIds(String id){
        List<String> types = Lists.newArrayList();
        types.add(OrganType.organ.getValue());
        return findOwnerAndChildsIds(id,types);
    }

    /**
     * 查找下级所有部门IDS
     * @param id 机构ID
     * @return
     */
    public List<String> findOwnerAndChildsDepartmentIds(String id){
        List<String> types = Lists.newArrayList();
        types.add(OrganType.department.getValue());
        return findOwnerAndChildsIds(id,types);
    }

    /**
     * 快速查找方法
     *
     * @param userIds 用户
     * @returnIDS
     */
    public List<TreeNode> findOrganUserTreeByUserIds(Collection<String> userIds) throws ServiceException {
        List<User> users = userService.findUsersByIds(userIds);
        return findOrganUserTreeByUsers(users,null);
    }


    /**
     * 快速查找方法
     *
     * @param userList 用户列表
     * @param rootOrganId 根机构ID
     * @return
     */
    public List<TreeNode> findOrganUserTreeByUsers(Collection<User> userList, String rootOrganId) throws ServiceException {
        int minLevel = Integer.MAX_VALUE;
        int maxLevel = Integer.MIN_VALUE;
        Map<Integer, List<OrganExtend>> organMap = Maps.newHashMap();// 树层级 机构
        Map<String, OrganExtend> organTempMap = Maps.newHashMap();// 机构 机构
        Map<String, List<User>> userMap = Maps.newHashMap();// 机构ID 用户
        if (Collections3.isNotEmpty(userList)) {
            for (User user : userList) {
                OrganExtend userOrganExtend = OrganUtils.getOrganExtendByUserId(user.getId());
                if (userOrganExtend == null) {
                    throw new ServiceException(Result.ERROR, user.getName() + "未设置默认机构.", null);
                }

                OrganExtend _userOrganExtend = userOrganExtend;
                while (_userOrganExtend != null) {
                    List<OrganExtend> organs = organMap.get(_userOrganExtend.getLevel());
                    if (Collections3.isEmpty(organs)) {
                        organs = Lists.newArrayList();
                    }
                    List<OrganExtend> pList = organMap.get(_userOrganExtend.getLevel());
                    if(Collections3.isEmpty(pList) || !pList.contains(_userOrganExtend)){
                        organs.add(_userOrganExtend);
                    }
                    organMap.put(_userOrganExtend.getLevel(), organs);
                    organTempMap.put(_userOrganExtend.getId(),_userOrganExtend);

                    if (maxLevel < _userOrganExtend.getLevel()) {
                        maxLevel = _userOrganExtend.getLevel();
                    }
                    if (minLevel > _userOrganExtend.getLevel()) {
                        minLevel = _userOrganExtend.getLevel();
                    }

                    //补全上级机构
                    if(StringUtils.isNotBlank(rootOrganId)){
                        if(rootOrganId.equals(_userOrganExtend.getId())){
                            _userOrganExtend = null;
                        }else{
                            _userOrganExtend = OrganUtils.getOrganExtend(_userOrganExtend.getParentId());
                        }
                    }else{
                        _userOrganExtend = null;
                    }

                }

                List<User> users = userMap.get(userOrganExtend.getId());
                if (Collections3.isEmpty(users)) {
                    users = Lists.newArrayList();
                }
                users.add(user);
                userMap.put(userOrganExtend.getId(), users);
            }

            for (User user : userList) {
                //补漏(中间漏了的机构)
                OrganExtend _userOrganExtend = OrganUtils.getOrganExtendByUserId(user.getId());
                while (_userOrganExtend != null) {
                    if(_userOrganExtend.getLevel() >= minLevel && !organTempMap.containsKey(_userOrganExtend.getId())){
                        List<OrganExtend> organs = organMap.get(_userOrganExtend.getLevel());
                        if (Collections3.isEmpty(organs)) {
                            organs = Lists.newArrayList();
                        }
                        List<OrganExtend> pList = organMap.get(_userOrganExtend.getLevel());
                        if(Collections3.isEmpty(pList) || !pList.contains(_userOrganExtend)){
                            organs.add(_userOrganExtend);
                        }
                        organMap.put(_userOrganExtend.getLevel(), organs);
                        organTempMap.put(_userOrganExtend.getId(),_userOrganExtend);

                    }
                    _userOrganExtend = OrganUtils.getOrganExtend(_userOrganExtend.getParentId());

                }
            }

        }

        List<Integer> levelKeys = Lists.newArrayList(organMap.keySet());
        Collections.sort(levelKeys);

        List<TreeNode> treeNodes = Lists.newArrayList();
        for (Integer level : levelKeys) {
            List<OrganExtend> organExtends = organMap.get(level);
            for (OrganExtend oe : organExtends) {
                TreeNode organTreeNode = new TreeNode(oe.getId(), oe.getName());
                organTreeNode.setpId(oe.getParentId());
                organTreeNode.addAttribute("nType", "o");
                organTreeNode.addAttribute("type", oe.getType());
                organTreeNode.addAttribute("code", oe.getCode());
                organTreeNode.addAttribute("sysCode", oe.getSysCode());
                organTreeNode.setIconCls(ICON_GROUP);
                organTreeNode.setNocheck(true);
                treeNodes.add(organTreeNode);
                List<User> _userList = userMap.get(oe.getId());
                if (Collections3.isEmpty(_userList)) {
                    continue;
                }
                Collections.sort(_userList, new Comparator<User>() {

                    @Override
                    public int compare(User u1, User u2) {
                        if (u1.getSort() > u2.getSort()) {
                            return 1;
                        } else if(u1.getSort() < u2.getSort()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }

                });
                for (User user : _userList) {
                    TreeNode userNode = new TreeNode(user.getId(), user.getName());
                    userNode.setpId(oe.getId());
                    userNode.addAttribute("nType", "u");
                    if (SexType.girl.getValue().equals(user.getSex())) {
                        userNode.setIconCls(ICON_USER_RED);
                    } else {
                        userNode.setIconCls(ICON_USER);
                    }
                    organTreeNode.addChild(userNode);
                }
            }
        }
        return treeNodes;
    }


    /*机构扩展表*/

    /**
     * 根据机构ID查找
     * @param organId
     * @return
     */
    public OrganExtend getOrganExtend(String organId){
        if(StringUtils.isBlank(organId)){
            return null;
        }
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("id",organId);
        return dao.getOrganExtend(parameter);
    }
    /**
     * 根据机构ID查找
     * @param organId
     * @return
     */
    public OrganExtend getOrganCompany(String organId){
        if(StringUtils.isBlank(organId)){
            return null;
        }
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("id",organId);
        return dao.getOrganCompany(parameter);
    }

    /**
     * 根据用户ID查找
     * @param userId
     * @return
     */
    public OrganExtend getOrganExtendByUserId(String userId){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("userId",userId);
        return dao.getOrganExtendByUserId(parameter);
    }

    /**
     * 根据用户ID查找
     * @param userId
     * @return
     */
    public OrganExtend getCompanyByUserId(String userId){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("userId",userId);
        return dao.getCompanyByUserId(parameter);
    }

    /**
     * 查找所有单位
     * @return
     */
    public List<OrganExtend> findCompanyOrganExtends(){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        List<String> types = Lists.newArrayList();
        types.add(OrganType.organ.getValue());
        parameter.put("types",types);
        return dao.findOrganExtends(parameter);
    }


    /**
     * 查找机构下直属部门
     * @param organId
     * @return
     */
    public List<OrganExtend> findDepartmentOrganExtendsByCompanyId(String organId){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findDepartmentOrganExtendsByCompanyId(parameter);
    }


    /**
     * 查找机构下直属部门ID
     * @param organId
     * @return
     */
    public List<String> findDepartmentOrganIdsByCompanyId(String organId){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findDepartmentOrganIdsByCompanyId(parameter);
    }


    /**
     * 查找机构下直属部门以及小组
     * @param organId
     * @return
     */
    public List<OrganExtend> findDepartmentAndGroupOrganExtendsByCompanyId(String organId){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findDepartmentAndGroupOrganExtendsByCompanyId(parameter);
    }


    /**
     * 查找机构下直属部门以及小组IDS
     * @param organId
     * @return
     */
    public List<String> findDepartmentAndGroupOrganIdsByCompanyId(String organId){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("organId",organId);
        return dao.findDepartmentAndGroupOrganIdsByCompanyId(parameter);
    }

    /*机构扩展表*/


    /**
     * 自定义SQL查询
     * @param whereSQL
     * @return
     */
    public List<Organ> findByWhereSQL(String whereSQL){
        Parameter parameter = Parameter.newParameter();
        parameter.put("whereSQL",whereSQL);
        return dao.findByWhereSQL(parameter);
    }
    /**
     * 自定义SQL查询
     * @param sql
     * @return
     */
    public List<Organ> findBySql(String sql){
        Parameter parameter = Parameter.newParameter();
        parameter.put("sql",sql);
        return dao.findBySql(parameter);
    }
}
