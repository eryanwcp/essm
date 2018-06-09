/**
*  Copyright (c) 2012-2018 http://www.eryansky.com
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*/
package com.eryansky.modules.disk.service;

import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.orm.mybatis.service.TreeService;
import com.eryansky.modules.disk._enum.FolderAuthorize;
import com.eryansky.modules.disk._enum.FolderType;
import com.eryansky.modules.disk.web.DiskController;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.AppUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eryansky.modules.disk.mapper.Folder;
import com.eryansky.modules.disk.dao.FolderDao;

import java.util.List;

/**
 *  service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-04
 */
@Service
@Transactional(readOnly = true)
public class FolderService extends TreeService<FolderDao, Folder> {

    @Autowired
    private FileService fileService;

    /**
     * 根据编码获取 获取系统文件夹
     * <br/>如果不存在则自动创建
     * @param code 系统文件夹编码
     * @return
     */
    @Transactional(readOnly = false)
    public Folder checkAndSaveSystemFolderByCode(String code){
        return checkAndSaveSystemFolderByCode(code,null);
    }


    /**
     * 根据编码获取 获取用户的系统文件夹
     * <br/>如果不存在则自动创建
     * @param code 系统文件夹编码
     * @param userId 用户ID
     * @return
     */
    @Transactional(readOnly = false)
    public Folder checkAndSaveSystemFolderByCode(String code, String userId){
        Validate.notBlank(code, "参数[code]不能为null.");
        List<Folder> list =  findFoldersByUserId(userId,null,FolderAuthorize.SysTem.getValue(),code);
        Folder folder =  list.isEmpty() ? null:list.get(0);
        if(folder == null){
            folder = new Folder();
            folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
            folder.setName(code+"["+FolderType.HIDE.getDescription()+"]");
            folder.setCode(code);
            folder.setType(FolderType.HIDE.getValue());
            folder.setUserId(userId);
            save(folder);
        }
        return folder;
    }

    @Transactional(readOnly = false)
    public void saveFolder(Folder folder) {
        save(folder);
    }


    /**
     * 判断和创建个人云盘的默认文件夹
     */
    @Transactional(readOnly = false)
    public Folder initHideFolderAndSaveForUser(String userId) {
        List<Folder> list = findFoldersByUserId(userId,FolderType.HIDE.getValue(),FolderAuthorize.User.getValue(),null);
        Folder folder = Collections3.isEmpty(list) ? null:list.get(0);
        if (folder == null) {
            folder = new Folder();// 创建默认文件夹
            folder.setUserId(userId);
            folder.setType(FolderType.HIDE.getValue());
            folder.setFolderAuthorize(FolderAuthorize.User.getValue());
            folder.setName(FolderType.HIDE.getDescription());
            save(folder);
        }
        return folder;
    }


    /**
     * 删除文件夹 包含子级文件夹以及文件
     * @param folderId
     */
    @Transactional(readOnly = false)
    public void deleteFolderAndFiles(String folderId) {
        Validate.notNull(folderId,"参数[folderId]不能为null.");
        dao.deleteCascadeByFolderId(new Folder(folderId));
        fileService.deleteCascadeByFolderId(folderId);
    }



    public TreeNode folderToTreeNode(Folder folder) {
        TreeNode treeNode = new TreeNode(folder.getId(),folder.getName());
        treeNode.getAttributes().put(DiskController.NODE_TYPE,
                DiskController.NType.Folder.toString());
        treeNode.getAttributes().put(DiskController.NODE_USERNAME,folder.getUserName());
        treeNode.setIconCls("icon-folder");
        treeNode.setpId(folder.getParentId());
        return treeNode;
    }


    /**
     *
     * @param folderAuthorize {@link com.eryansky.modules.disk._enum.FolderAuthorize}
     * @param userId 用户ID
     * @param excludeFolderId 排除的文件夹ID
     * @return
     */
    public List<TreeNode> findNormalTypeFolderTreeNodes(String folderAuthorize, String userId, String excludeFolderId){
        Validate.notNull(folderAuthorize,"参数[folderAuthorize]不能为null.");
        List<Folder>  list = findFolders(folderAuthorize,userId,FolderType.NORMAL.getValue(),null,null,excludeFolderId);
        List<TreeNode> treeNodes = Lists.newArrayList();
        for(Folder folder:list){
            treeNodes.add(folderToTreeNode(folder));
        }
        return AppUtils.toTreeTreeNodes(treeNodes);
    }

    public List<Folder> findFolders(String folderAuthorize, String userId,String type,String code,String parentId, String excludeFolderId){
        Parameter parameter = Parameter.newParameter();
        parameter.addParameter(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.addParameter(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.addParameter("folderAuthorize",folderAuthorize);
        parameter.addParameter("userId",userId);
        parameter.addParameter("type",type);
        parameter.addParameter("code",code);
        parameter.addParameter("parentId",parentId);
        parameter.addParameter("excludeFolderId",excludeFolderId);
        return dao.findFolders(parameter);
    }


    /**
     * 获取用户文创建的文件夹
     * @param userId 用户ID
     * @return
     */
    public List<Folder> findFoldersByUserId(String userId){
        return findFoldersByUserId(userId,null,null,null);
    }

    public List<Folder> findNormalTypeFoldersByUserId(String userId){
        return findFoldersByUserId(userId,FolderType.NORMAL.getValue(),null,null);
    }

    public List<Folder> findFoldersByUserId(String userId,String type,String folderAuthorize,String code){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("type",type);
        parameter.put("userId",userId);
        parameter.put("folderAuthorize",folderAuthorize);
        parameter.put("code",code);
        return dao.findFoldersByUserId(parameter);
    }

    /**
     * 根据父级ID查找子级文件夹
     * @param parentId 父级文件夹ID null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
    public List<Folder> findNormalTypeChild(String parentId){
        return findChild(parentId,FolderType.NORMAL.getValue());
    }
    /**
     * 根据父级ID查找子级文件夹
     * @param parentId 父级文件夹ID null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
    public List<Folder> findChild(String parentId){
        return findChild(parentId,null);
    }

    /**
     * 根据父级ID查找子级文件夹
     * @param parentId 父级文件夹ID null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @param type {@link FolderType}
     * @return
     */
    public List<Folder> findChild(String parentId, String type){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("type",type);
        parameter.put("id",parentId);
        return dao.findChild(parameter);
    }
    /**
     * 根据父级ID查找子级文件夹(包含下级)
     * @param parentId 父级文件夹ID null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
    public List<Folder> findNormalTypeChilds(String parentId){
        return findChilds(parentId,FolderType.NORMAL.getValue());
    }
    /**
     * 根据父级ID查找子级文件夹(包含下级)
     * @param parentId 父级文件夹ID null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
    public List<Folder> findChilds(String parentId){
        return findChilds(parentId,null);
    }


    /**
     * 根据父级ID查找子级文件夹(包含下级)
     * @param parentId 父级文件夹ID null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @param type {@link FolderType}
     * @return
     */
    public List<Folder> findChilds(String parentId, String type){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("type",type);
        parameter.put("id",parentId);
        return dao.findChilds(parameter);
    }


    public List<String> findChildsIds(String parentId){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("id",parentId);
        return dao.findChildsIds(parameter);
    }


}
