/**
*  Copyright (c) 2012-2018 http://www.eryansky.com
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*/
package com.eryansky.modules.disk.service;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.disk._enum.FolderAuthorize;
import com.eryansky.modules.disk._enum.FolderType;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.modules.disk.mapper.File;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.disk.web.DiskController;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eryansky.modules.disk.mapper.Folder;
import com.eryansky.modules.disk.dao.FolderDao;
import com.eryansky.core.orm.mybatis.service.CrudService;

import java.util.List;

/**
 *  service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-04
 */
@Service
@Transactional(readOnly = true)
public class FolderService extends CrudService<FolderDao, Folder> {

    @Autowired
    private FileService fileService;


    /**
     * 保存文件。 若为编辑部门下文件时需要判断是否有子文件夹并更新子文件夹的部门信息
     *
     * @param folder
     *            文件夹对象
     */
    @Transactional(readOnly = false)
    public void saveFolder(Folder folder) {
        String folderId = folder.getId();
        String folderOrganId = folder.getOrganId();
        if (StringUtils.isNotBlank(folderId) && StringUtils.isNotBlank(folderOrganId)) {
            List<Folder> children = Lists.newArrayList();
            recursiveFolder(children, folder);
            if (Collections3.isNotEmpty(children)) {
                for (Folder child : children) {
                    child.setOrganId(folderOrganId);
                    save(child);// 更新子文件夹的部门Id
                }

            }
        }
        save(folder);
    }

    /**
     * 查找默认文件夹,无则初始化---> 针对 我的云盘、部门云盘 、公共云盘
     *
     * @param folderAuthorize
     *            云盘类型Id
     * @param userId
     *             用户Id
     */
    @Transactional(readOnly = false)
    public Folder initHideFolder(String folderAuthorize, String userId) {
        Folder folder = null;
        if (FolderAuthorize.User.getValue().equals(folderAuthorize)) {
            folder = initHideForUser(userId);
        }
        return folder;

    }


    /**
     * 判断和创建个人云盘的默认文件夹
     */
    @Transactional(readOnly = false)
    public Folder initHideForUser(String userId) {
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
     * @param folderId
     */
    @Transactional(readOnly = false)
    public void deleteFolderAndFiles(String folderId) {
        Validate.notNull(folderId,"参数[folderId]不能为null.");
        List<String> fileIds = Lists.newArrayList();
        List<String> folderIds = Lists.newArrayList();
        recursiveFolderAndFile(folderIds, fileIds, folderId);
        fileService.deleteFolderFiles(fileIds);
        for(String id:folderIds){
            this.delete(id);
        }

    }

    /**
     * 某个用户是否可以操作文件夹
     * @param userId 用户ID
     * @param folder 文件夹
     * @return
     */
    public boolean isOperateFolder(String userId,Folder folder){
        String _userId = userId;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(_userId == null){
            _userId = sessionInfo.getUserId();
        }

        boolean operateAble =  DiskUtils.isDiskAdmin(_userId);
        if(!operateAble){
            if(sessionInfo.getUserId().equals(folder.getUserId())){
                operateAble = true;
            }
        }
        return operateAble;
    }

    /**
     * 是否允操作文件夹
     *
     * @param folderId
     *            文件夹ID
     * @param isAdmin
     *            是否是管理员
     * @return
     */
    public boolean isOperateFolder(String folderId, boolean isAdmin) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Folder folder = get(folderId);
        boolean operateAble = isAdmin;
        if (!operateAble && folder != null) {
            if (sessionInfo.getUserId().equals(folder.getUserId())) {
                operateAble = true;
            }
        }
        return operateAble;
    }

    /**
     * 递归文件夹树
     * @param treeNodes
     *            传入的树节点
     * @param folder
     *            树节点隶属的文件夹
     * @param excludeFolderId
     *            父文件夹
     * @param isAdmin
     *            是否有管理员权限
     * @param isCascade
     *            是否递归
     */
    public void recursiveFolderTreeNode(List<TreeNode> treeNodes,
                                        Folder folder, String excludeFolderId, Boolean isAdmin,
                                        boolean isCascade) {
        TreeNode treeNode = new TreeNode(folder.getId().toString(),
                folder.getName());
        treeNode.getAttributes().put(DiskController.NODE_TYPE,
                DiskController.NType.Folder.toString());
        if (isAdmin != null) {
            treeNode.getAttributes().put(DiskController.NODE_OPERATE,isOperateFolder(folder.getId(), isAdmin));
        }
        treeNode.getAttributes().put(DiskController.NODE_USERNAME,
                folder.getUserName());
        treeNode.setIconCls("icon-folder");
        treeNodes.add(treeNode);
        if (isCascade) {
            List<Folder> childFolders = this.findChildsByParentId(folder.getId());
            List<TreeNode> childTreeNodes = Lists.newArrayList();
            for (Folder childFolder : childFolders) {
                if (!childFolder.getId().equals(excludeFolderId)) {
                    this.recursiveFolderTreeNode(childTreeNodes, childFolder,
                            excludeFolderId, isAdmin, isCascade);
                }
            }
            if (Collections3.isNotEmpty(childTreeNodes)) {
                treeNode.setState(TreeNode.STATE_CLOASED);
                for (TreeNode childTreeNode : childTreeNodes) {
                    treeNode.addChild(childTreeNode);
                }
            }
        }

    }

    /**
     *
     * @param folderAuthorize {@link com.eryansky.modules.disk._enum.FolderAuthorize}
     * @param userId 用户ID
     * @param excludeFolderId 排除的文件夹ID
     * @param isAdmin  是否有管理员权限
     * @param isCascade 是否级联
     * @return
     */
    public List<TreeNode> getFolders(String folderAuthorize, String userId,  String excludeFolderId, Boolean isAdmin, boolean isCascade){
        Validate.notNull(folderAuthorize,"参数[folderAuthorize]不能为null.");
        List<Folder> folders = this.getFoldersByFolderAuthorize(folderAuthorize,userId,null);
        List<TreeNode> treeNodes = Lists.newArrayList();
        for(Folder folder:folders){
            if(!folder.getId().equals(excludeFolderId)){
                this.recursiveFolderTreeNode(treeNodes,folder,excludeFolderId,isAdmin,isCascade);
            }
        }
        return treeNodes;
    }

    /**
     * TODO 我的分享和分享给我不由此进入；其他类型云盘查询隐藏文件夹
     * @param userId
     *            指定登录人
     * @param folderAuthorize
     *            指定云盘类型--> 不含我的分享和分享给我
     * @return
     */
    public List<Folder> getAuthorizeFolders(String userId,
                                            String folderAuthorize) {
        return getFoldersByFolderAuthorize(folderAuthorize,userId,null);
    }


    /**
     * 查询某个授权类型下的文件夹
     * 0个人：个人文件夹
     * @param folderAuthorize
     * @param userId
     * @param parentId 上级文件夹 null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
    public List<Folder> getFoldersByFolderAuthorize(String folderAuthorize,
                                                    String userId, String parentId) {
        Validate.notNull(folderAuthorize, "参数[folderAuthorize]不能为null.");
        Parameter parameter = new Parameter();
        parameter.put(Folder.FIELD_STATUS,Folder.STATUS_NORMAL);
        parameter.put("folderAuthorize",folderAuthorize);
        parameter.put("parentId",parentId);
        parameter.put("type",FolderType.NORMAL.getValue());

        if (FolderAuthorize.User.getValue().equals(folderAuthorize)) {
            Validate.notNull(userId, "参数[userId]不能为null.");
            parameter.put("userId", userId);
        } else {
            throw new ServiceException("无法识别参数[folderAuthorize]："
                    + folderAuthorize);
        }

        return dao.getFoldersByFolderAuthorize(parameter);
    }


    /**
     * 获取用户文创建的文件夹
     * @param userId 用户ID
     * @return
     */
    public List<Folder> findFoldersByUserId(String userId){
        return findFoldersByUserId(userId,null,null,null);
    }

    public List<Folder> findFoldersByUserId(String userId,String type,String folderAuthorize,String code){
        Parameter parameter = new Parameter();
        parameter.put(Folder.FIELD_STATUS,Folder.STATUS_NORMAL);
        parameter.put("type",type);
        parameter.put("userId",userId);
        parameter.put("folderAuthorize",folderAuthorize);
        parameter.put("code",code);
        return dao.findFoldersByUserId(parameter);
    }


    /**
     * 递归 查找文件夹下的文件夹以及文件
     *
     * @param folderIds
     *            父文件夹
     * @param fileIds
     *            文件Id集合
     * @param folderId
     *            文件夹Id集合
     */
    private void recursiveFolderAndFile(List<String> folderIds,
                                        List<String> fileIds, String folderId) {
        folderIds.add(folderId);
        if (Collections3.isNotEmpty(fileIds)) {
            List<File> folderFiles = fileService.getFolderFiles(folderId);
            for (File folderFile : folderFiles) {
                fileIds.add(folderFile.getId());
            }
        }
        List<Folder> childFolders = this.findChildsByParentId(folderId);
        if (Collections3.isNotEmpty(childFolders)) {
            for (Folder childFolder : childFolders) {
                recursiveFolderAndFile(folderIds, fileIds, childFolder.getId());
            }
        }
    }

    /**
     * 递归 查找文件夹下的子文件夹集合
     *
     * @param folderList
     *            子文件夹集合
     * @param folder
     *            父文件夹
     */
    private void recursiveFolder(List<Folder> folderList, Folder folder) {
        List<Folder> childFolders = this.findChildsByParentId(folder.getId());
        if (Collections3.isNotEmpty(childFolders)) {
            for (Folder childFolder : childFolders) {
                if (folderList != null) {
                    folderList.add(childFolder);
                }
                recursiveFolder(folderList, childFolder);
            }
        }
    }

    /**
     * 根据父级ID查找子级文件夹
     * @param parentId 父级文件夹ID null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
    public List<Folder> findChildsByParentId(String parentId){
        Parameter parameter = new Parameter();
        parameter.put(Folder.FIELD_STATUS,Folder.STATUS_NORMAL);
        parameter.put("type",FolderType.NORMAL.getValue());
        parameter.put("parentId",parentId);

        return dao.findChildsByParentId(parameter);
    }


}
