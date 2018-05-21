/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.entity.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.orm.mybatis.service.TreeService;
import com.eryansky.modules.sys._enum.OrganType;
import com.eryansky.modules.sys._enum.SexType;
import com.eryansky.modules.sys.mapper.OrganExtend;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.CacheConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
    private OrganDao dao;

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
        return id;
    }

    /**
     * 自定义删除方法.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    @Transactional(readOnly = false)
    public void deleteByIds(List<String> ids) {
        for(String id:ids){
            deleteById(id);
        }
    }

    public List<Organ> findAllNormal(){
        List<Organ> list = dao.findAllList(new Organ());
        return list;
    }




    /**
     * 查找所有机构类型机构 {@link OrganType.organ}
     * @return
     */
    public List<Organ> findCompanyOrgans(){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("type",OrganType.organ.getValue());
        return dao.findCustomQuery(parameter);
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
                    pTreeNode.setState(TreeNode.STATE_CLOASED);
                    if(Collections3.isEmpty(treeNode.getChildren())){
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
            if(Collections3.isEmpty(treeNode.getChildren())){
                treeNode.setState(TreeNode.STATE_OPEN);
            }
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
                organs = this.findOwnerAndChildsOrgans(parentId);
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
                TreeNode pTreeNode = getParentTreeNode(treeNode.getpId(), tempTreeNodes);
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
        TreeNode treeNode = new TreeNode(organ.getId(),organ.getName());
        if(StringUtils.isBlank(organ.getParentId())){
            treeNode.setIconCls(ICON_ORGAN_ROOT);
        }else{
            treeNode.setIconCls(ICON_GROUP);
        }
        List<String> childOrganIds = findChildsOrganIds(organ.getId());
        if(addUser != null && addUser){
            List<User> childUsers = userService.findOrganUsers(organ.getId());
            treeNode.setState((Collections3.isNotEmpty(childOrganIds) || Collections3.isNotEmpty(childUsers)) ? TreeNode.STATE_CLOASED:TreeNode.STATE_OPEN);
        }else{
            treeNode.setState(organ.getState());
        }
        treeNode.setpId(organ.getParentId());
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
     * 查找父级节点
     * @param parentId
     * @param treeNodes
     * @return
     */
    public TreeNode getParentTreeNode(String parentId,String type, List<TreeNode> treeNodes){
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
     * 同步所有机构ID
     */
    @Transactional(readOnly = false)
    public void syncAllParentIds() {
        List<Organ> rootOrgans = this.findRoots();
        updateParentIds(rootOrgans);
    }

    /**
     * 递归
     * @param organs
     */
    @Transactional(readOnly = false)
    public void updateParentIds(List<Organ> organs){
        for(Organ organ:organs){
            dao.updateParentIds(organ);
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
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("sysCode",sysCode);
        return  dao.getBySysCode(parameter);
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
    public List<Organ> findDataByParent(String parentId, List<String> status){
        //默认值 正常
        if(Collections3.isEmpty(status)){
            status = new ArrayList<String>(3);
            status.add(StatusState.NORMAL.getValue());
            status.add(StatusState.LOCK.getValue());
            status.add(StatusState.AUDIT.getValue());
        }
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,status);
        parameter.put("parentId",parentId);
        return dao.findChild(parameter);
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
    public List<Organ> findOrgansByIds(List<String> organIds) {
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
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.put("id",id);
        return dao.findChilds(parameter);
    }

    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<String> findChildsOrganIds(String id){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,AppConstants.getJdbcType());
        parameter.put("id",id);
        return dao.findChildsIds(parameter);
    }

    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<Organ> findOwnerAndChildsOrgans(String id){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put(BaseInterceptor.DB_NAME,AppConstants.getJdbcType());
        parameter.put("id",id);
        return dao.findOwnAndChilds(parameter);
    }


    /**
     * 查询指定机构以及子机构
     * @param id 机构ID
     * @return
     */
    public List<String> findOwnerAndChildsOrganIds(String id){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("id",id);
        return dao.findOwnAndChildsIds(parameter);
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
        Organ organ = this.get(anyOrganId);
        if(organ == null){
            return null;
        }
        return findChildsDepartmentOrgans(OrganUtils.getOrganCompanyId(organ.getId()));
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
        OrganExtend organExtend = OrganUtils.getOrganExtend(anyOrganId);
        if(organExtend == null){
            return null;
        }
        return findChildsDepartmentOrganIds(organExtend.getCompanyId());
    }

    /**
     * 本机构下的所有子部门（不包含下级机构类型的数据）
     * @param id 机构ID
     * @return
     */
    public List<Organ> findChildsDepartmentOrgans(String id){
        List<Organ> organs = findChilds(id);
        List<Organ> list = Lists.newArrayList();
        for(Organ organ:organs){
            if(OrganType.department.getValue().equals(organ.getType()) && OrganUtils.getOrganCompanyId(organ.getId()).equals(id)){
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
        List<Organ> organs = findChilds(id);
        List<String> list = Lists.newArrayList();
        for(Organ organ:organs){
            if(OrganType.department.getValue().equals(organ.getType()) && OrganUtils.getOrganCompanyId(organ.getId()).equals(id)){
                list.add(organ.getId());
            }
        }
        return list;
    }



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

}
