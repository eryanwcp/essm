/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.PrettyMemoryUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.DownloadUtils;
import com.eryansky.common.web.utils.WebUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.modules.disk.entity.*;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity._enum.FileSizeType;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.entity._enum.FolderType;
import com.eryansky.modules.disk.service.*;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.disk.utils.FileUtils;
import com.eryansky.modules.disk.utils.OrganExtendUtils;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys._enum.OrganType;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.utils.SelectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 我的云盘 管理 包含：文件夹的管理 文件的管理 以及文件分享
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-22
 */
@Controller
@RequestMapping(value = "${adminPath}/disk")
public class DiskController extends SimpleController {

    @Autowired
    private FolderManager folderManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private FileNoticeManager fileNoticeManager;
    @Autowired
    private DiskManager diskManager;
    @Autowired
    private OrganManager organManager;
    @Autowired
    private UserStorageManager userStorageManager;
    @Autowired
    private FileShareManager fileShareManager;
    @Autowired
    private FileHistoryManager fileHistoryManager;
    @Autowired
    private OrganStorageManager organStorageManager;

    public static final String NODE_TYPE = "nType";
    public static final String NODE_OPERATE = "operate";
    public static final String NODE_USERNAME = "userName";

    public static final String ICON_FOLDER = "easyui-icon-folder";
    public static final String ICON_DISK = "eu-icon-disk_yunpan";
    public static final String ICON_FAVORITES = "eu-icon-disk_favorites";
    public static final String ICON_DISK_UP = "eu-icon-disk_up";
    public static final String ICON_DISK_DOWN = "eu-icon-disk_down";
    public static final String ICON_DISK_ORGAN = "eu-icon-disk_organ";
    public static final String ICON_DISK_PUBLIC = "eu-icon-disk_public";

    /**
     * 文件分享类型
     */
    public enum FileShareType {
        PERSON, ORGAN, PUBLIC;
    }

    /**
     * 文件操作类型 分享,编辑,删除,收藏,收藏分享,取消分享,取消接收分享
     */
    public enum OperateHtml {
        SHARE, EDIT, DELETE, COLLECT, REMOVE_SHARE, REMOVE_RECEIVE;
    }

    /**
     * 磁盘树 节点类型
     */
    public enum NType {
        FolderAuthorize, Folder, Organ;
    }

    public enum ModelType {
        Folder, File;
    }

    @ModelAttribute
    public void getModel(ModelType modelType, String id, Model uiModel) {
        if (modelType != null && StringUtils.isNotBlank(id)) {
            if (modelType.equals(ModelType.Folder)) {
                uiModel.addAttribute("model", folderManager.loadById(id));
            } else if (modelType.equals(ModelType.File)) {
                uiModel.addAttribute("model", fileManager.loadById(id));
            }
        }

    }

    /**
     * 我的云盘
     */
    @RequestMapping(value = { "" })
    public ModelAndView list() {
        ModelAndView modelAndView = new ModelAndView("modules/disk/disk");
        return modelAndView;
    }

    /**
     * 历史访问
     */
    @RequestMapping(value = { "history" })
    public ModelAndView historyList() {
        return new ModelAndView("modules/disk/disk-history");
    }

    /**
     * 云盘动态
     */
    @RequestMapping(value = { "notice" })
    public ModelAndView noticeList() {
        return new ModelAndView("modules/disk/disk-notice");
    }

    /**
     * 文件检索
     */
    @RequestMapping(value = { "search" })
    public ModelAndView searchList() {
        boolean isAdmin = DiskUtils.isDiskAdmin(SecurityUtils
                .getCurrentSessionInfo().getUserId());
        ModelAndView modelAndView = new ModelAndView("modules/disk/disk-search");
        modelAndView.addObject("isAdmin", isAdmin);
        return modelAndView;
    }

    /**
     * 文件历史访问记录
     *
     * @param fileOperate
     *            操作类型
     * @param fileName
     *            文件名称
     * @return
     */
    @RequestMapping(value = { "fileHistoryDatagrid" })
    @ResponseBody
    public String fileHistoryDatagrid(Integer fileOperate, String fileName) {
        String json = "";
        String loginUserId = SecurityUtils.getCurrentSessionInfo().getUserId(); // 登录人Id
        Page<FileHistory> page = new Page<FileHistory>(
                SpringMVCHolder.getRequest());
        page = fileHistoryManager.findHistoryPage(page, loginUserId,
                fileOperate, fileName);
        if (page != null) {
            List<FileHistory> list = page.getResult();
            if (Collections3.isNotEmpty(list)) {
                for (FileHistory history : list) {
                    File historyFile = FileUtils.getFile(history.getFileId());
                    if (!StatusState.NORMAL.getValue().equals(
                            historyFile.getStatus())) {
                        history.setIsActive(false);
                    }
                }
            } else {
                list = Lists.newArrayList();
            }

            json = JsonMapper.getInstance().toJson(
                    new Datagrid<FileHistory>(list.size(), list));
        } else {
            json = JsonMapper.getInstance().toJson(new Datagrid());
        }
        return json;

    }

    /**
     * 文件检索
     *
     * @param fileName
     *            文件名
     * @param folderAuthorize
     *            云盘类型
     * @param startTime
     *            开始时间
     * @param endTime
     *            结束时间
     * @param personIds
     *            上传人Id集合
     * @return
     */

    @RequestMapping(value = { "fileSearchDatagrid" })
    @ResponseBody
    public String fileSearchDatagrid(
            String fileName,
            Integer folderAuthorize,
            Integer sizeType,
            Date startTime,
            Date endTime,
            @RequestParam(value = "personIds", required = false) List<String> personIds) {
        String json = JsonMapper.getInstance().toJson(new Datagrid());
        String loginUserId = SecurityUtils.getCurrentSessionInfo().getUserId(); // 登录人Id
        boolean isAdmin = DiskUtils.isDiskAdmin(loginUserId); // 是否是云盘管理员
        if (isAdmin) {
            loginUserId = null;
        }
        Page<File> page = new Page<File>(SpringMVCHolder.getRequest());
        page = fileManager.searchFilePage(page, loginUserId, fileName,
                folderAuthorize, sizeType, startTime, endTime, personIds);
        if (page != null) {
            Datagrid<File> dg = new Datagrid<File>(page.getTotalCount(),
                    page.getResult());
            json = JsonMapper.getInstance().toJson(
                    dg,
                    File.class,
                    new String[] { "id", "name", "code", "prettyFileSize",
                            "location", "createTime", "ownerName" });
        }
        return json;

    }

    /**
     * 云盘动态
     *
     * @return
     */

    @RequestMapping(value = { "diskNoticeDatagrid" })
    @ResponseBody
    public String diskNoticeDatagrid() {
        String json = JsonMapper.getInstance().toJson(new Datagrid());
        String loginUserId = SecurityUtils.getCurrentSessionInfo().getUserId(); // 登录人Id
        Page<FileNotice> page = new Page<FileNotice>(
                SpringMVCHolder.getRequest());
        page = fileNoticeManager.diskNoticePage(page, loginUserId);
        if (page != null) {
            Datagrid<FileNotice> dg = new Datagrid<FileNotice>(
                    page.getTotalCount(), page.getResult());
            json = JsonMapper.getInstance().toJson(
                    dg,
                    FileNotice.class,
                    new String[] { "id", "fileName", "createTime",
                            "operateUserName", "locationDsc", "operateDesc",
                            "isActive" });
        }
        return json;

    }

    /**
     * 文件夹树
     *
     * @param folderAuthorize
     *            {@link com.eryansky.modules.disk.entity._enum.FolderAuthorize}
     * @param organId
     * @param excludeFolderId
     * @param selectType
     * @return
     */
    @RequestMapping(value = { "folderTree" })
    @ResponseBody
    public List<TreeNode> folderTree(Integer folderAuthorize, String organId,
                                     String excludeFolderId, String selectType) {
        List<TreeNode> treeNodes = Lists.newArrayList();
        TreeNode selectTreeNode = SelectType.treeNode(selectType);
        if(selectTreeNode != null){
            treeNodes.add(selectTreeNode);
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        List<TreeNode> folderTreeNodes = null;
        if (FolderAuthorize.Organ.getValue().equals(folderAuthorize)
                && organId == null) {// 部门网盘 没有传递organId返回null
            folderTreeNodes = Lists.newArrayList();
        } else {
            folderTreeNodes = folderManager.getFolders(folderAuthorize,
                    sessionInfo.getUserId(), organId, excludeFolderId, null,
                    true);
        }
        treeNodes.addAll(folderTreeNodes);
        return treeNodes;
    }

    /**
     * 文件授权下拉框
     *
     * @return
     */
    @RequestMapping(value = { "folderAuthorizeCombobox" })
    @ResponseBody
    public List<Combobox> folderAuthorizeCombobox(String selectType,
                                                  String requestType) {
        List<Combobox> cList = Lists.newArrayList();

        Combobox selectCombobox = SelectType.combobox(selectType);
        if(selectCombobox != null){
            cList.add(selectCombobox);
        }

        Combobox combobox = new Combobox(FolderAuthorize.User.getValue()
                .toString(), FolderAuthorize.User.getDescription());
        cList.add(combobox);

        if ("search".equals(requestType)) {
            combobox = new Combobox(FolderAuthorize.Collect.getValue()
                    .toString(), FolderAuthorize.Collect.getDescription());
            cList.add(combobox);
        }
        combobox = new Combobox(FolderAuthorize.Organ.getValue().toString(),
                FolderAuthorize.Organ.getDescription());
        cList.add(combobox);
        combobox = new Combobox(FolderAuthorize.Public.getValue().toString(),
                FolderAuthorize.Public.getDescription());
        cList.add(combobox);

        return cList;
    }

    /**
     * 文件大小类型下拉框
     *
     * @return
     */
    @RequestMapping(value = { "fileSizeTypeCombobox" })
    @ResponseBody
    public List<Combobox> fileSizeTypeCombobox(String selectType) {
        List<Combobox> cList = Lists.newArrayList();

        Combobox selectCombobox = SelectType.combobox(selectType);
        if(selectCombobox != null){
            cList.add(selectCombobox);
        }
        FileSizeType[] _enums = FileSizeType.values();
        for (int i = 0; i < _enums.length; i++) {
            Combobox combobox = new Combobox(_enums[i].getValue().toString(),
                    _enums[i].getDescription());
            cList.add(combobox);
        }

        return cList;
    }

    /**
     * 保存文件夹
     *
     * @return
     */
    @RequestMapping(value = { "saveFolder" })
    @ResponseBody
    public Result saveFolder(@ModelAttribute("model") Folder folder) {
        if (StringUtils.isBlank(folder.getUserId())) {
            folder.setUserId(SecurityUtils.getCurrentSessionInfo().getUserId());
        }
        folderManager.saveFolder(folder);
        return Result.successResult();
    }

    /**
     * 删除文件夹
     *
     * @param folderId
     *            文件夹ID
     * @return
     */
    @RequestMapping(value = { "folderRemove/{folderId}" })
    @ResponseBody
    public Result folderRemove(@PathVariable String folderId) {
        folderManager.deleteFolderAndFiles(folderId);
        return Result.successResult();
    }

    /**
     * 递归用户文件夹树
     *
     * @param userTreeNodes
     * @param folder
     * @param isCascade
     */
    public void recursiveUserFolderTreeNode(List<TreeNode> userTreeNodes,
                                            Folder folder, boolean isCascade) {
        TreeNode treeNode = new TreeNode(folder.getId().toString(),
                folder.getName());
        treeNode.getAttributes().put(DiskController.NODE_TYPE,
                DiskController.NType.Folder.toString());
        treeNode.getAttributes().put(DiskController.NODE_OPERATE, true);
        treeNode.setIconCls(ICON_FOLDER);
        userTreeNodes.add(treeNode);
        if (isCascade) {
            List<Folder> childFolders = folderManager
                    .getChildFoldersByByParentFolderId(folder.getId());
            List<TreeNode> childTreeNodes = Lists.newArrayList();
            for (Folder childFolder : childFolders) {
                this.recursiveUserFolderTreeNode(childTreeNodes, childFolder,
                        isCascade);
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
     * 递归部门文件夹树
     *
     * @param organTreeNode
     * @param isAdmin
     */
    private void recursiveOrganTreeNode(TreeNode organTreeNode, String userId,
                                        boolean isAdmin) {
        organTreeNode.getAttributes().put(NODE_TYPE, NType.Organ.toString());
        String organId = organTreeNode.getId();
        Long limitStorage = organStorageManager
                .getOrganAvaiableStorage(organId);// 部门可用空间
        long usedStorage = fileManager.getOrganUsedStorage(organId);// 部门已用空间

        if (OrganType.department.getValue().equals(
                organTreeNode.getAttributes().get("type"))) {
            // 只有部门有容量
            String organNodeName = organTreeNode.getText() + "("
                    + PrettyMemoryUtils.prettyByteSize(usedStorage) + "/"
                    + PrettyMemoryUtils.prettyByteSize(limitStorage) + ")";
            organTreeNode.setText(organNodeName);
        }

        // 用户在部门下的文件夹
        List<Folder> organUserFolders = folderManager.getOrganFolders(organId,
                userId, false, null);
        // 排除用户在部门以外的所有文件夹
        List<Folder> excludeUserOrganFolders = folderManager.getOrganFolders(
                organId, userId, true, null);
        // 合并
        List<Folder> organFolders = Collections3.aggregate(organUserFolders,
                excludeUserOrganFolders);

        List<TreeNode> treeNodes = Lists.newArrayList();
        Iterator<Folder> iterator = organFolders.iterator();
        while (iterator.hasNext()) {
            Folder folder = iterator.next();
            folderManager.recursiveFolderTreeNode(treeNodes, folder, null,
                    isAdmin, true);
        }
        if (Collections3.isNotEmpty(treeNodes)) {
            organTreeNode.setState(TreeNode.STATE_CLOASED);
            for (int i = 0; i < treeNodes.size(); i++) {
                TreeNode t = treeNodes.get(i);
                t.setText(t.getText() + "（<span style='color:blue;'>"
                        + organFolders.get(i).getUserName() + "</span>）");
                organTreeNode.addChild(t);
            }
        }

        for (TreeNode childTreeNode : organTreeNode.getChildren()) {
            if (!NType.Folder.toString().equals(childTreeNode.getAttributes().get(NODE_TYPE))) {
                recursiveOrganTreeNode(childTreeNode, userId, isAdmin);
            }
        }
    }


    /**
     * 磁盘树
     *
     * @return
     */
    @RequestMapping(value = { "diskTree" })
    @ResponseBody
    public List<TreeNode> diskTree() {
        List<TreeNode> treeNodes = Lists.newArrayList(); // 返回的树节点
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String loginUserId = sessionInfo.getUserId(); // 登录人Id
        String loginOrganId = sessionInfo.getLoginOrganId(); // 登录人部门Id

        /**
         * 个人树构造----------begin
         */
        long limitStorage = userStorageManager
                .getUserAvaiableStorage(loginUserId);// 用户可用个人空间
        long usedStorage = fileManager.getUserUsedStorage(loginUserId);// 用户已用空间

        TreeNode userOwnerTreeNode = new TreeNode(FolderAuthorize.User
                .getValue().toString(), FolderAuthorize.User.getDescription()
                + "(" + PrettyMemoryUtils.prettyByteSize(usedStorage) + "/"
                + PrettyMemoryUtils.prettyByteSize(limitStorage) + ")");
        userOwnerTreeNode.getAttributes().put(NODE_TYPE,
                NType.FolderAuthorize.toString());
        userOwnerTreeNode.setIconCls(ICON_DISK);

        List<Folder> userFolders = folderManager.getFoldersByFolderAuthorize(
                FolderAuthorize.User.getValue(), loginUserId, null, null, null);
        List<TreeNode> userFolderTreeNodes = Lists.newArrayList();
        for (Folder folder : userFolders) {
            this.recursiveUserFolderTreeNode(userFolderTreeNodes, folder, true);
        }
        for (TreeNode userFolderTreeNode : userFolderTreeNodes) {
            userOwnerTreeNode.addChild(userFolderTreeNode);
        }
        treeNodes.add(userOwnerTreeNode);

        /**
         * 收藏树构造----------begin
         */
        TreeNode collectionTreeNode = new TreeNode(FolderAuthorize.Collect
                .getValue().toString(),
                FolderAuthorize.Collect.getDescription());
        collectionTreeNode.getAttributes().put(NODE_TYPE,
                NType.FolderAuthorize.toString());
        collectionTreeNode.setIconCls(ICON_FAVORITES);
        treeNodes.add(collectionTreeNode);

        /**
         * 我的分享树构造----------begin
         */
        TreeNode shareTreeNode = new TreeNode(FolderAuthorize.Share.getValue()
                .toString(), FolderAuthorize.Share.getDescription());
        shareTreeNode.getAttributes().put(NODE_TYPE,
                NType.FolderAuthorize.toString());
        shareTreeNode.setIconCls(ICON_DISK_UP);
        treeNodes.add(shareTreeNode);

        /**
         * 分享给我树构造----------begin
         */
        TreeNode reveiveTreeNode = new TreeNode(FolderAuthorize.ReceivePerson
                .getValue().toString(),
                FolderAuthorize.ReceivePerson.getDescription());
        reveiveTreeNode.getAttributes().put(NODE_TYPE,
                NType.FolderAuthorize.toString());
        reveiveTreeNode.setIconCls(ICON_DISK_DOWN);
        treeNodes.add(reveiveTreeNode);
        /**
         * 部门树构造----------begin
         */
//        TreeNode organTreeNode = new TreeNode(FolderAuthorize.Organ.getValue()
//                .toString(), FolderAuthorize.Organ.getDescription());
//        organTreeNode.getAttributes().put(NODE_TYPE,
//                NType.FolderAuthorize.toString());
//        organTreeNode.setIconCls(ICON_DISK_ORGAN);
//
        Boolean isAdmin = new Boolean(DiskUtils.isDiskAdmin(loginUserId)); // 是否是云盘管理员
//        if (isAdmin) {
//            loginOrganId = null;// 云盘管理员有所有部门的管理权限
//        }
//        List<TreeNode> organTreeNodes = organManager.findOrganTree(loginOrganId,false);
//        for (TreeNode organNode : organTreeNodes) {
//            this.recursiveOrganTreeNode(organNode, loginUserId, isAdmin);
//            organTreeNode.addChild(organNode);
//        }
//        treeNodes.add(organTreeNode);

        /**
         * 公共树构造----------begin
         */
        TreeNode publicTreeNode = new TreeNode(FolderAuthorize.Public
                .getValue().toString(), FolderAuthorize.Public.getDescription());
        publicTreeNode.getAttributes().put(NODE_TYPE,
                NType.FolderAuthorize.toString());
        publicTreeNode.setIconCls(ICON_DISK_PUBLIC);
        List<TreeNode> publicTreeNodes = folderManager.getFolders(
                FolderAuthorize.Public.getValue(), null, null, null, isAdmin,
                true);

        for (TreeNode treeNode : publicTreeNodes) {
            treeNode.setText(treeNode.getText() + "（<span style='color:blue;'>"
                    + treeNode.getAttributes().get(NODE_USERNAME) + "</span>）");
            publicTreeNode.addChild(treeNode);
        }
        treeNodes.add(publicTreeNode);

        return treeNodes;
    }

    /**
     * 文件列表
     *
     * @param folderId
     *            文件夹Id
     * @param folderAuthorize
     *            文件夹隶属云盘类型
     * @param folderOrgan
     *            文件夹隶属部门
     * @param fileName
     * @return
     */
    @RequestMapping(value = { "folderFileDatagrid" })
    @ResponseBody
    public String folderFileDatagrid(String folderId, Integer folderAuthorize,
                                     String folderOrgan, String fileName) {
        String json = null;
        long totalSize = 0L; // 分页总大小
        List<Map<String, Object>> footer = Lists.newArrayList();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String loginUserId = sessionInfo.getUserId(); // 登录人Id

        Boolean shareDisk = FolderAuthorize.Share.getValue() == folderAuthorize; // 我的分享
        Boolean receivePersonDisk = FolderAuthorize.ReceivePerson.getValue() == folderAuthorize; // 分享给我
        if (folderId == null && folderAuthorize == null && folderOrgan == null) {
            json = JsonMapper.getInstance().toJson(new Datagrid());
        } else if (shareDisk || receivePersonDisk) {// 我的分享或者分享给我
            Page<FileShare> page = new Page<FileShare>(
                    SpringMVCHolder.getRequest());
            if (shareDisk) {
                page = fileShareManager.findSharePage(page, loginUserId,
                        fileName);
            } else {
                page = fileShareManager.findReceivePage(page, loginUserId,
                        fileName);
            }
            if (page != null) {
                Datagrid<FileShare> dg = new Datagrid<FileShare>(
                        page.getTotalCount(), page.getResult());
                if (Collections3.isNotEmpty(page.getResult())) {
                    for (FileShare fileShare : page.getResult()) {
                        totalSize += fileShare.getFile().getFileSize();
                        if (shareDisk) {
                            fileShare.setOperate_all(Lists
                                    .newArrayList(OperateHtml.REMOVE_SHARE
                                            .toString()));
                        } else {
                            fileShare.setOperate_all(Lists.newArrayList(
                                    OperateHtml.COLLECT.toString(),
                                    OperateHtml.REMOVE_RECEIVE.toString()));
                        }
                    }
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("name", "总大小");
                map.put("prettyFileSize",
                        PrettyMemoryUtils.prettyByteSize(totalSize));
                footer.add(map);
                dg.setFooter(footer);
                json = JsonMapper.getInstance().toJson(
                        dg,
                        FileShare.class,
                        new String[] { "id", "fileId", "name",
                                "prettyFileSize", "shareTime", "userName",
                                "shareUserName", "operate_all",
                                "receiveLocation" });

            }

        } else {
            Page<File> page = new Page<File>(SpringMVCHolder.getRequest());
            List<String> folderIds = Lists.newArrayList();
            if (folderId != null) { // 正常情况下是仅选中了文件夹树节点
                folderIds.add(folderId);
            } else { // 正常情况下是仅选中了云盘类型树节点或者仅选中了部门树节点
                List<Folder> userFolders = folderManager.getAuthorizeFolders(
                        loginUserId, folderAuthorize, folderOrgan);// 获取用户授权使用的文件夹
                if (Collections3.isNotEmpty(userFolders)) {
                    for (Folder folder : userFolders) {
                        folderIds.add(folder.getId());
                    }
                }
            }
            page = fileManager.findPage(page, folderIds, fileName);

            Datagrid<File> dg = new Datagrid<File>(page.getTotalCount(),
                    page.getResult());
            if (Collections3.isNotEmpty(page.getResult())) {
                boolean isAdmin = DiskUtils.isDiskAdmin(sessionInfo.getUserId()); // 是否是云盘管理员
                boolean isLeader = false;// 是否是部门管理者
                if (folderOrgan != null) {
                    isLeader = OrganExtendUtils.getLeaderUser(folderOrgan).contains(
                            loginUserId);
                }
                Folder folder = folderId == null ? null : folderManager
                        .getById(folderId);
                boolean userDisk = FolderAuthorize.User.getValue() == folderAuthorize; // 我的云盘有分享编辑删除权限
                boolean collectDisk = FolderAuthorize.Collect.getValue() == folderAuthorize;// 我的收藏有分享删除权限
                boolean publicDisk = FolderAuthorize.Public.getValue() == folderAuthorize;
                boolean organDisk = FolderAuthorize.Organ.getValue() == folderAuthorize;
                for (File file : page.getResult()) {
                    List<String> operate = Lists.newArrayList();
                    Boolean isOwner = loginUserId.equals(file.getUserId());// 上传者

                    if (folder != null) {// 选中文件夹，上传者有分享编辑删除权限，除个人云盘外非上传者有分享收藏权限
                        if (isOwner) {
                            operate.add(OperateHtml.SHARE.toString());
                            operate.add(OperateHtml.EDIT.toString());
                            operate.add(OperateHtml.DELETE.toString());
                        } else if (FolderAuthorize.User.getValue() != folder
                                .getFolderAuthorize()) {
                            operate.add(OperateHtml.COLLECT.toString());
                        }
                    } else if (collectDisk || userDisk) {
                        operate.add(OperateHtml.SHARE.toString());
                        if (userDisk) {
                            operate.add(OperateHtml.EDIT.toString());
                        }
                        operate.add(OperateHtml.DELETE.toString());
                    } else if (publicDisk || organDisk || folderOrgan != null) { // 部门云盘或者公共云盘
                        Folder fileFolder = file.getFolder();
                        if (FolderType.SHARE.getValue().equals(
                                fileFolder.getType())) {// 分享文件夹
                            // file.setName("<font color=#D94600>[分享]</font>" +
                            // file.getName());
                            operate.add(OperateHtml.COLLECT.toString());
                            if (isAdmin || isLeader) {
                                operate.add(OperateHtml.REMOVE_RECEIVE
                                        .toString());
                            } else if (organDisk) {
                                List<String> leaderId = OrganExtendUtils.getLeaderUser(fileFolder.getOrganId());
                                if (leaderId.contains(loginUserId)) {
                                    operate.add(OperateHtml.REMOVE_RECEIVE
                                            .toString());
                                }
                            }

                        } else if (isOwner || isAdmin) {
                            operate.add(OperateHtml.SHARE.toString());
                            if (isAdmin) {
                                operate.add(OperateHtml.COLLECT.toString());
                            }
                            operate.add(OperateHtml.EDIT.toString());
                            operate.add(OperateHtml.DELETE.toString());
                        } else {
                            operate.add(OperateHtml.COLLECT.toString());
                        }
                    }

                    file.setOperate_all(operate);
                    totalSize += file.getFileSize();
                }
            }
            Map<String, Object> map = Maps.newHashMap();
            map.put("name", "总大小");
            map.put("prettyFileSize",
                    PrettyMemoryUtils.prettyByteSize(totalSize));
            footer.add(map);
            dg.setFooter(footer);
            json = JsonMapper.getInstance().toJson(
                    dg,
                    File.class,
                    new String[] { "id", "fileId", "name", "prettyFileSize",
                            "createTime", "userName", "operate_all" });
        }

        return json;
    }

    /**
     * 文件夹编辑页面
     *
     * @param folderId
     * @param folderAuthorize
     *            {@link com.eryansky.modules.disk.entity._enum.FolderAuthorize}
     * @param parentFolderId
     * @param organId
     * @return
     */
    @RequestMapping(value = { "folderInput" })
    public ModelAndView folderInput(String folderId, Integer folderAuthorize,
                                    String parentFolderId, String organId, String roleId) {
        ModelAndView modelAndView = new ModelAndView(
                "modules/disk/disk-folderInput");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Folder model = new Folder();
        if (folderId != null) {
            model = folderManager.loadById(folderId);
        }

        modelAndView.addObject("model", model);
        modelAndView.addObject("folderAuthorize", folderAuthorize);
        if (StringUtils.isNotBlank(parentFolderId)) {// 不允许在别人的文件夹下创建文件夹
            Folder parentFolder = folderManager.loadById(parentFolderId);
            if (!parentFolder.getUserId().equals(sessionInfo.getUserId())) {
                parentFolderId = null;
            }
        }
        modelAndView.addObject("parentFolderId", parentFolderId);
        modelAndView.addObject("organId", organId);
        return modelAndView;
    }

    /**
     * 文件上传页面
     *
     * @param folderId
     *            文件夹Id
     * @param folderAuthorize
     *            云盘类型Id
     * @param organId
     *            部门Id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "fileInput" })
    public ModelAndView fileInput(String folderId, Integer folderAuthorize,
                                  String organId) throws Exception {
        ModelAndView modelAndView = new ModelAndView(
                "modules/disk/disk-fileInput");
        Folder model = new Folder();
        if (StringUtils.isNotBlank(folderId)) { // 选中文件夹
            model = folderManager.loadById(folderId);
        } else if (folderAuthorize != null || StringUtils.isNotBlank(organId)) { // 选中非部门云盘的默认文件夹或者部门下的默认文件夹
            String loginUserId = SecurityUtils.getCurrentSessionInfo()
                    .getUserId();
            model = folderManager.initHideFolder(folderAuthorize, loginUserId,
                    organId);
            folderId = model.getId();
        } else {
            Exception e = new ActionException("上传文件异常！请联系管理员。");
            throw e;
        }

        modelAndView.addObject("folderId", folderId);
        return modelAndView;
    }

    /**
     * 文件信息修改
     *
     * @return
     */
    @RequestMapping(value = { "fileSave" })
    @ResponseBody
    public Result fileSave(@ModelAttribute("model") File file) {
        fileManager.saveEntity(file);
        return Result.successResult();
    }

    /**
     * 文件删除
     *
     * @param fileIds
     *            文件Id集合
     * @return
     */
    @RequestMapping(value = { "delFolderFile" })
    @ResponseBody
    public Result delFolderFile(
            @RequestParam(value = "fileIds", required = false) List<String> fileIds) {
        fileManager.removeFolderFile(fileIds);
        return Result.successResult();
    }

    /**
     * 文件级联删除
     *
     * @param fileCodes
     *            文件code集合
     * @throws Exception
     */
    @RequestMapping(value = { "cascadeDelFile" })
    @ResponseBody
    public Result cascadeDelFile(
            @RequestParam(value = "fileCodes", required = false) List<String> fileCodes)
            throws Exception {
        fileManager.cascadeDelFile(fileCodes);
        return Result.successResult();
    }

    /**
     * 上传容量校验
     *
     * @param sessionInfo
     * @param folder
     * @param uploadFileSize
     * @return
     * @throws ActionException
     */
    private boolean checkStorage(SessionInfo sessionInfo, Folder folder,
                                 long uploadFileSize) throws ActionException {
        boolean flag = false;
        if (FolderAuthorize.User.getValue().equals(folder.getFolderAuthorize())) {
            long limitStorage = userStorageManager
                    .getUserAvaiableStorage(sessionInfo.getUserId());// 用户可用个人空间
            long usedStorage = fileManager.getUserUsedStorage(sessionInfo
                    .getUserId());// 用户已用空间
            long avaiableStorage = limitStorage - usedStorage;
            if (avaiableStorage < uploadFileSize) {
                throw new ActionException("用户个人云盘空间不够！可用大小："
                        + PrettyMemoryUtils.prettyByteSize(avaiableStorage));
            }
        } else if (FolderAuthorize.Organ.getValue().equals(
                folder.getFolderAuthorize())) {
            long limitStorage = organStorageManager
                    .getOrganAvaiableStorage(folder.getOrganId());// 部门可用空间
            long usedStorage = fileManager.getOrganUsedStorage(folder
                    .getOrganId());// 部门已用空间
            long avaiableStorage = limitStorage - usedStorage;
            if (avaiableStorage < uploadFileSize) {
                throw new ActionException("部门云盘空间不够！可用大小："
                        + PrettyMemoryUtils.prettyByteSize(avaiableStorage));
            }
        }
        return flag;
    }

    /**
     * 文件上传容量校验
     *
     * @param folderId
     *            文件夹ID
     * @param uploadFileSize
     *            上传文件的大小 单位：字节
     * @return
     */
    @RequestMapping(value = { "fileLimitCheck/{folderId}" })
    @ResponseBody
    public Result fileLimitCheck(@PathVariable String folderId,
                                 Long uploadFileSize, String filename) {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Result result = Result.errorResult();
        try {
            Folder folder = folderManager.loadById(folderId);
            checkStorage(sessionInfo, folder, uploadFileSize);
            result = Result.successResult();
        } catch (ActionException e) {
            result.setMsg("文件【" + filename + "】上传失败，" + e.getMessage());
        }
        return result;
    }

    /**
     * 文件上传
     *
     * @param folderId
     *            文件夹
     * @param uploadFile
     *            上传文件
     * @return
     */
    @RequestMapping(value = { "fileUpload" })
    @ResponseBody
    public Result fileUpload(
            @RequestParam(value = "folderId", required = false) String folderId,
            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile)
            throws Exception {
        Result result = Result.errorResult();
        if (StringUtils.isBlank(folderId)) {
            result.setMsg("文件夹Id丢失！");
        } else if (uploadFile == null) {
            result.setMsg("上传文件丢失！");
        } else {
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            Folder folder = folderManager.loadById(folderId);
            if (folder != null) {
                checkStorage(sessionInfo, folder, uploadFile.getSize()); // 判断上传容量是否超出
                File file = diskManager.fileUpload(sessionInfo, folder,
                        uploadFile);
                String obj = null;
                if (file != null) {
                    obj = file.getId();
                }
                result = Result.successResult().setObj(obj);
            } else {
                result.setMsg("文件夹不存在，已被删除或移除！");
            }
        }
        return result;
    }

    /**
     * 文件转发 根据文件ID查找到服务器上的硬盘文件 virtual
     * @param response
     * @param request
     * @param fileId
     */
    @RequestMapping(value = { "file/{fileId}" })
    public ModelAndView file(HttpServletResponse response,
                             HttpServletRequest request, @PathVariable String fileId) {
        java.io.File tempFile = null;
        try {
            WebUtils.setExpiresHeader(response, WebUtils.ONE_YEAR_SECONDS);
            response.setHeader("Content-Type", "application/octet-stream");
            File file = fileManager.loadById(fileId);
            tempFile = file.getDiskFile();
            FileCopyUtils.copy(new FileInputStream(tempFile), response.getOutputStream());
        } catch (FileNotFoundException e) {
            if(logger.isWarnEnabled()) {
                logger.warn(String.format("请求的文件%s不存在", fileId), e.getMessage());
            }
        } catch (IOException e) {
            logger.warn(String.format("请求的文件%s不存在", fileId), e.getMessage());
        }catch (Exception e) {
            logger.warn(String.format("请求的文件%s不存在", fileId), e.getMessage());
        }finally {
//            if(tempFile != null){//删除缓存文件
//                tempFile.delete();
//            }
        }
        return null;

    }



    /**
     * 文件下载------通知、邮件共用
     *
     * @param response
     * @param request
     * @param fileId
     *            文件ID
     */
    @Logging(logType = LogType.access,value = "下载文件[#fileId]")
    @RequiresUser(required = false)
    @RequestMapping(value = { "fileDownload/{fileId}" })
    public ModelAndView fileDownload(HttpServletResponse response,
                             HttpServletRequest request, @PathVariable String fileId) {
        File file = fileManager.loadById(fileId);
        downloadSingleFileUtil(response, request, file);
        return null;

    }



    private ModelAndView downloadSingleFileUtil(HttpServletResponse response,
                                        HttpServletRequest request, File file) {
        ActionException fileNotFoldException = new ActionException(
                "文件不存在，已被删除或移除。");
        if (file == null) {
            throw fileNotFoldException;
        }

        try {
            java.io.File diskFile = file.getDiskFile();
            if (!diskFile.exists() || !diskFile.canRead()) {
                throw fileNotFoldException;
            }
            String displayName = file.getName();
            DiskUtils.download(request, response, new FileInputStream(
                    diskFile), displayName);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw fileNotFoldException;
        }
        return null;
    }

    /**
     * 文件下载----云盘单用,做Joinpoint
     *
     * @param fileIds
     *            入参Ids拼接字符串
     * @throws Exception
     */
    @RequestMapping(value = { "downloadDiskFile" })
    public ModelAndView downloadDiskFile(
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestParam(value = "fileIds", required = false) List<String> fileIds)
            throws Exception {
        if (Collections3.isNotEmpty(fileIds)) {
            if (fileIds.size() == 1) {
                File file = fileManager.getById(fileIds.get(0));
                downloadSingleFileUtil(response, request, file);
            } else {
                List<File> fileList = fileManager.findFilesByIds(fileIds);
                downloadMultiFileUtil(response, request, fileList);
            }
        } else {
            throw new ActionException("下载链接失效。");
        }
        return null;
    }

    /**
     * 批量下载文件导出ZIP包
     *
     * @param response
     * @param request
     * @param fileList
     *            文件对象集合
     * @throws Exception
     */
    private ModelAndView downloadMultiFileUtil(HttpServletResponse response,
                                       HttpServletRequest request, List<File> fileList) throws Exception {
        if (Collections3.isNotEmpty(fileList)) {
            java.io.File tempZipFile = null;
            try {
                // 创建一个临时压缩文件， 文件流全部注入到这个文件中
                tempZipFile = new java.io.File(Identities.uuid()+"_temp.zip");
                DiskUtils.makeZip(fileList, tempZipFile.getAbsolutePath());
                String dName = "【批量下载】" + fileList.get(0).getName() + ".zip";
                DownloadUtils.download(request, response, new FileInputStream(
                        tempZipFile), dName);
            } catch (Exception e) {
                throw e;
            }finally {
                if(tempZipFile != null && tempZipFile.isFile()){
                    tempZipFile.delete();//删除临时Zip文件
                }
            }
        }
        return null;
    }

    /**
     * 文件分享选中用户页面
     *
     * @return
     */
    @RequestMapping(value = { "share-file/{fileId}" })
    public ModelAndView shareFilePage(@PathVariable String fileId) {
        ModelAndView modelAndView = new ModelAndView(
                "modules/disk/disk-share-file");
        if (StringUtils.isNotBlank(fileId)) {
            modelAndView.addObject("fileId", fileId);
            modelAndView.addObject("fileShareType", Lists.newArrayList(
                    FileShareType.PERSON.toString(),
                    FileShareType.ORGAN.toString(),
                    FileShareType.PUBLIC.toString()));
        }
        return modelAndView;
    }

    /**
     * 文件分享
     *
     * @param fileId
     *            分享文件
     * @param personIds
     *            分享人Ids
     * @param organIds
     *            分享部门Ids
     * @param shareType
     *            分享方式
     * @return
     */
    @RequestMapping(value = { "shareFile" })
    @ResponseBody
    public Result shareFile(
            @RequestParam String fileId,
            @RequestParam(value = "personIds", required = false) List<String> personIds,
            @RequestParam(value = "organIds", required = false) List<String> organIds,
            String shareType) {
        Result result = Result.errorResult();
        if (StringUtils.isNotBlank(fileId)) {
            File file = fileManager.getById(fileId);
            if (file != null) {
                String loginUserId = SecurityUtils.getCurrentSessionInfo()
                        .getUserId();
                if (FileShareType.PERSON.toString().equals(shareType)) { // 分享给个人
                    if (Collections3.isNotEmpty(personIds)) {
                        if (personIds.contains(loginUserId)) {// 剔除自己
                            personIds.remove(loginUserId);
                        }
                        if (Collections3.isNotEmpty(personIds)) {
                            fileShareManager.shareFileToPerson(file,
                                    loginUserId, personIds);
                            result = Result.successResult().setObj(fileId);
                        } else {
                            result.setMsg("被分享人不能为自己!");
                        }
                    } else {
                        result.setMsg("被分享人丢失!");
                    }

                } else if (FileShareType.ORGAN.toString().equals(shareType)) {// 分享给部门
                    if (Collections3.isNotEmpty(organIds)) {
                        fileShareManager.shareFileToOrgan(file, loginUserId,
                                organIds);
                        result = Result.successResult().setObj(fileId);
                    } else {
                        result.setMsg("被分享部门丢失!");
                    }

                } else if (FileShareType.PUBLIC.toString().equals(shareType)) {
                    fileShareManager.shareFileToPublic(file, loginUserId);
                    result = Result.successResult().setObj(fileId);

                }
            } else {
                result.setMsg("文件不存在,已被删除或移除!");
            }
        } else {
            result.setMsg("文件Id丢失!");
        }
        return result;

    }

    /**
     * 取消分享
     *
     * @param shareId
     *            文件
     * @return
     */
    @RequestMapping(value = { "removeShare/{shareId}" })
    @ResponseBody
    public Result removeShare(@PathVariable String shareId) {
        Result result = Result.errorResult();
        if (shareId != null) {
            fileShareManager.removeShare(shareId);
            result = Result.successResult();
        } else {
            result.setMsg("分享Id丢失!");
        }
        return result;
    }

    /**
     * 取消接收的分享
     *
     * @param pageId
     *            记录Id
     * @param nodeType
     *            选中节点的类型
     * @param nodeId
     *            选中节点Id
     * @return
     */
    @RequestMapping(value = { "removeReceive" })
    @ResponseBody
    public Result removeReceive(String pageId, String nodeType, Integer nodeId) {
        Result result = Result.errorResult();
        if (StringUtils.isNotBlank(pageId)) {
            String loginUserId = SecurityUtils.getCurrentSessionInfo()
                    .getUserId(); // 登录人Id
            fileShareManager.removeReceive(pageId, loginUserId, nodeType,
                    nodeId);
            result = Result.successResult();
        } else {
            result.setMsg("分享Id丢失!");
        }
        return result;
    }

    /**
     * 文件收藏
     *
     * @param pageId
     *            入参Id
     * @return
     */
    @RequestMapping(value = { "CollectFile" })
    @ResponseBody
    public Result CollectFile(String pageId, Integer folderAuthorize) {
        Result result = Result.errorResult();
        if (StringUtils.isNotBlank(pageId)) {
            File file = null;
            if (FolderAuthorize.ReceivePerson.getValue()
                    .equals(folderAuthorize)) {
                file = fileShareManager.getById(pageId).getFile();
            } else {
                file = fileManager.getById(pageId);
            }
            if (file != null) {
                String loginUserId = SecurityUtils.getCurrentSessionInfo()
                        .getUserId();
                fileManager.collectFile(file, loginUserId);
                result = Result.successResult()
                        .setMsg("收藏[" + file.getName() + "]文件成功!")
                        .setObj(file.getId());
            } else {
                result.setMsg("文件不存在,已被删除或移除!");
            }
        } else {
            result.setMsg("文件Id丢失!");
        }
        return result;

    }

    /**
     * 清空缓存目录 正在运行时 慎用
     * @return
     */
    @RequestMapping(value = { "clearTempDir" })
    @ResponseBody
    public Result clearTempDir(){
        logger.info("清空缓存目录...");
        DiskUtils.clearTempDir();
        logger.info("清空缓存目录完毕");
        return Result.successResult();
    }


}