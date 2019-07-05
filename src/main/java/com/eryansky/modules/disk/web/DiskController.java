/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
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
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.PrettyMemoryUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.io.IoUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.DownloadUtils;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.disk.mapper.Folder;
import com.eryansky.utils.AppUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.modules.disk.mapper.File;
import com.eryansky.modules.disk._enum.FileSizeType;
import com.eryansky.modules.disk._enum.FolderAuthorize;
import com.eryansky.modules.disk.service.*;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.sys._enum.LogType;
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
import java.util.List;
import java.util.Map;

/**
 * 我的云盘 管理 包含：文件夹的管理 文件的管理
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-22
 */
@Controller
@RequestMapping(value = "${adminPath}/disk")
public class DiskController extends SimpleController {

    @Autowired
    private FolderService folderService;
    @Autowired
    private FileService fileService;

    public static final String NODE_TYPE = "nType";
    public static final String NODE_OPERATE = "operate";
    public static final String NODE_USERNAME = "userName";

    public static final String ICON_FOLDER = "easyui-icon-folder";
    public static final String ICON_DISK = "eu-icon-disk_yunpan";

    /**
     * 磁盘树 节点类型
     */
    public enum NType {
        FolderAuthorize, Folder;
    }

    public enum ModelType {
        Folder, File;
    }

    @ModelAttribute
    public void getModel(ModelType modelType, String id, Model uiModel) {
        if (modelType != null && StringUtils.isNotBlank(id)) {
            if (modelType.equals(ModelType.Folder)) {
                uiModel.addAttribute("model", folderService.get(id));
            } else if (modelType.equals(ModelType.File)) {
                uiModel.addAttribute("model", fileService.get(id));
            }
        }

    }

    /**
     * 我的云盘
     */
    @RequiresPermissions("disk:disk:view")
    @Logging(logType = LogType.access,value = "我的云盘")
    @RequestMapping(value = { "" })
    public ModelAndView list() {
        ModelAndView modelAndView = new ModelAndView("modules/disk/disk");
        return modelAndView;
    }


    /**
     * 文件夹树
     * @param folderAuthorize {@link FolderAuthorize}
     * @param excludeFolderId
     * @param selectType
     * @return
     */
    @RequestMapping(value = { "folderTree" })
    @ResponseBody
    public List<TreeNode> folderTree(String folderAuthorize,String excludeFolderId, String selectType) {
        List<TreeNode> treeNodes = Lists.newArrayList();
        TreeNode selectTreeNode = SelectType.treeNode(selectType);
        if(selectTreeNode != null){
            treeNodes.add(selectTreeNode);
        }
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        List<TreeNode> folderTreeNodes = folderService.findNormalTypeFolderTreeNodes(folderAuthorize,sessionInfo.getUserId(),  excludeFolderId);
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

        Combobox combobox = new Combobox(FolderAuthorize.User.getValue(), FolderAuthorize.User.getDescription());
        cList.add(combobox);

        if ("search".equals(requestType)) {
            combobox = new Combobox(FolderAuthorize.SysTem.getValue(), FolderAuthorize.SysTem.getDescription());
            cList.add(combobox);
        }


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
            Combobox combobox = new Combobox(_enums[i].getValue(),
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
    @Logging(logType = LogType.access,value = "我的云盘-文件夹保存")
    @RequestMapping(value = { "saveFolder" })
    @ResponseBody
    public Result saveFolder(@ModelAttribute("model") Folder folder) {
        if (StringUtils.isBlank(folder.getUserId())) {
            folder.setUserId(SecurityUtils.getCurrentUserId());
        }
        folderService.saveFolder(folder);
        return Result.successResult();
    }

    /**
     * 删除文件夹
     *
     * @param folderId
     *            文件夹ID
     * @return
     */
    @Logging(logType = LogType.access,value = "我的云盘-文件夹删除")
    @RequestMapping(value = { "folderRemove/{folderId}" })
    @ResponseBody
    public Result folderRemove(@PathVariable String folderId) {
        folderService.deleteFolderAndFiles(folderId);
        return Result.successResult();
    }

    /**
     * 递归用户文件夹树
     *
     * @param folder
     */
    public TreeNode folderToTreeNode(Folder folder) {
        TreeNode treeNode = new TreeNode(folder.getId(),folder.getName());
        treeNode.getAttributes().put(DiskController.NODE_TYPE,DiskController.NType.Folder.toString());
        treeNode.getAttributes().put(DiskController.NODE_OPERATE, true);
        treeNode.setIconCls(ICON_FOLDER);
        treeNode.setpId(folder.getParentId());
        return treeNode;
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

        TreeNode userOwnerTreeNode = new TreeNode(FolderAuthorize.User.getValue(), FolderAuthorize.User.getDescription());
        userOwnerTreeNode.getAttributes().put(NODE_TYPE,NType.FolderAuthorize.toString());
        userOwnerTreeNode.setIconCls(ICON_DISK);
        treeNodes.add(userOwnerTreeNode);

        List<Folder> userFolders = folderService.findNormalTypeFoldersByUserId(loginUserId);
        List<TreeNode> userFolderTreeNodes = Lists.newArrayList();
        for (Folder folder : userFolders) {
            userFolderTreeNodes.add(this.folderToTreeNode(folder));
        }
        treeNodes.addAll(AppUtils.toTreeTreeNodes(userFolderTreeNodes));
        return treeNodes;
    }

    /**
     * 文件列表
     *
     * @param folderId
     *            文件夹Id
     * @param folderAuthorize
     *            文件夹隶属云盘类型
     * @param fileName
     * @return
     */
    @RequestMapping(value = { "folderFileDatagrid" })
    @ResponseBody
    public String folderFileDatagrid(String folderId, String folderAuthorize,String fileName) {
        String json = null;
        long totalSize = 0L; // 分页总大小
        List<Map<String, Object>> footer = Lists.newArrayList();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String loginUserId = sessionInfo.getUserId(); // 登录人Id

        if (folderId == null && folderAuthorize == null ) {
            json = JsonMapper.getInstance().toJson(new Datagrid());
        } else {
            Page<File> page = new Page<File>(SpringMVCHolder.getRequest());
            Folder _folder = folderService.initHideFolderAndSaveForUser(loginUserId);
            File entity = new File();
            entity.setQuery(fileName);
            entity.setFolderId(_folder.getId());
            page = fileService.findPage(page, entity);

            Datagrid<File> dg = new Datagrid<File>(page.getTotalCount(),
                    page.getResult());
            if (Collections3.isNotEmpty(page.getResult())) {
                for (File file : page.getResult()) {
                    totalSize += file.getFileSize();
                }
            }
            Map<String, Object> map = Maps.newHashMap();
            map.put("name", "总大小");
            map.put("prettyFileSize",PrettyMemoryUtils.prettyByteSize(totalSize));
            footer.add(map);
            dg.setFooter(footer);
            json = JsonMapper.getInstance().toJson(
                    dg,
                    File.class,
                    new String[] { "id", "fileId", "name", "prettyFileSize","createTime", "userName"});
        }

        return json;
    }

    /**
     * 文件夹编辑页面
     *
     * @param folderId
     * @param folderAuthorize
     *            {@link FolderAuthorize}
     * @param parentFolderId
     * @return
     */
    @RequestMapping(value = { "folderInput" })
    public ModelAndView folderInput(String folderId, Integer folderAuthorize,
                                    String parentFolderId) {
        ModelAndView modelAndView = new ModelAndView(
                "modules/disk/disk-folderInput");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        Folder model = new Folder();
        if (folderId != null) {
            model = folderService.get(folderId);
        }

        modelAndView.addObject("model", model);
        modelAndView.addObject("folderAuthorize", folderAuthorize);
        if (StringUtils.isNotBlank(parentFolderId)) {// 不允许在别人的文件夹下创建文件夹
            Folder parentFolder = folderService.get(parentFolderId);
            if (!parentFolder.getUserId().equals(sessionInfo.getUserId())) {
                parentFolderId = null;
            }
        }
        modelAndView.addObject("parentFolderId", parentFolderId);
        return modelAndView;
    }

    /**
     * 文件上传页面
     *
     * @param folderId
     *            文件夹Id
     * @param folderAuthorize
     *            云盘类型Id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "fileInput" })
    public ModelAndView fileInput(String folderId, String folderAuthorize) throws Exception {
        ModelAndView modelAndView = new ModelAndView(
                "modules/disk/disk-fileInput");
        Folder model = null;
        if(FolderAuthorize.User.getValue().equals(folderId) || FolderAuthorize.User.getValue().equals(folderAuthorize)){
            String loginUserId = SecurityUtils.getCurrentUserId();
            model = folderService.initHideFolderAndSaveForUser(loginUserId);
        }else if (StringUtils.isNotBlank(folderId)) { // 选中文件夹
            model = folderService.get(folderId);
        }else {
            Exception e = new ActionException("上传文件异常！请联系管理员。");
            throw e;
        }

        modelAndView.addObject("model", model);
        return modelAndView;
    }

    /**
     * 文件信息修改
     *
     * @return
     */
    @Logging(logType = LogType.access,value = "我的云盘-文件修改")
    @RequestMapping(value = { "fileSave" })
    @ResponseBody
    public Result fileSave(@ModelAttribute("model") File file) {
        fileService.save(file);
        return Result.successResult();
    }

    /**
     * 文件删除
     *
     * @param fileIds
     *            文件Id集合
     * @return
     */
    @Logging(logType = LogType.access,value = "我的云盘-文件删除")
    @RequestMapping(value = { "delFolderFile" })
    @ResponseBody
    public Result delFolderFile(@RequestParam(value = "fileIds", required = false) List<String> fileIds) {
        fileService.deleteFileByFileIds(fileIds);
        return Result.successResult();
    }

    /**
     * 文件级联删除
     *
     * @param fileCodes
     *            文件code集合
     * @throws Exception
     */
    @Logging(logType = LogType.access,value = "我的云盘-文件级联删除")
    @RequestMapping(value = { "cascadeDelFile" })
    @ResponseBody
    public Result cascadeDelFile(@RequestParam(value = "fileCodes", required = false) List<String> fileCodes){
        fileService.deleteFileByFolderCode(fileCodes);
        return Result.successResult();
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
    @RequiresUser(required = false)
    @RequestMapping(value = { "fileUpload" })
    @ResponseBody
    public Result fileUpload(
            @RequestParam(value = "folderId", required = false) String folderId,
            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile,String jsessionid)
            throws Exception {
        SessionInfo sessionInfo = SecurityUtils.getSessionInfo(jsessionid);
        sessionInfo = null != sessionInfo ? sessionInfo:SecurityUtils.getCurrentSessionInfo();
        if(null == sessionInfo){
            return Result.errorResult().setMsg("未授权！");
        }
        Result result = Result.errorResult();
        if (StringUtils.isBlank(folderId)) {
            result.setMsg("文件夹Id丢失！");
        } else if (uploadFile == null) {
            result.setMsg("上传文件丢失！");
        } else {
            Folder folder = folderService.get(folderId);
            if (folder != null) {
                File file = fileService.fileUpload(sessionInfo, folder,uploadFile);
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
     * 文件检索
     */
    @RequiresPermissions("disk:disk:search")
    @Logging(logType = LogType.access,value = "我的云盘-文件检索")
    @RequestMapping(value = { "search" })
    public ModelAndView searchList() {
        boolean isAdmin = DiskUtils.isDiskAdmin(SecurityUtils.getCurrentUserId());
        ModelAndView modelAndView = new ModelAndView("modules/disk/disk-search");
        modelAndView.addObject("isAdmin", isAdmin);
        return modelAndView;
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
            String folderAuthorize,
            String sizeType,
            Date startTime,
            Date endTime,
            @RequestParam(value = "personIds", required = false) List<String> personIds) {
        String json = JsonMapper.getInstance().toJson(new Datagrid());
        String userId = SecurityUtils.getCurrentUserId(); // 登录人Id
        boolean isAdmin = DiskUtils.isDiskAdmin(userId); // 是否是云盘管理员
        if (isAdmin) {
            userId = null;
        }
        userId = Collections3.isNotEmpty(personIds) ? personIds.get(0):userId;
        Page<File> page = new Page<File>(SpringMVCHolder.getRequest());
        page = fileService.searchFilePage(page, userId, fileName,
                folderAuthorize, sizeType, startTime, endTime);
        if (page != null) {
            Datagrid<File> dg = new Datagrid<File>(page.getTotalCount(),
                    page.getResult());
            json = JsonMapper.getInstance().toJson(
                    dg,
                    File.class,
                    new String[] { "id", "name", "code", "prettyFileSize","location", "createTime", "userName" });
        }
        return json;

    }



    /**
     * 文件下载
     *
     * @param response
     * @param request
     * @param fileId 文件ID
     */
    @Logging(logType = LogType.access,value = "下载文件")
    @RequiresUser(required = false)
    @RequestMapping(value = { "fileDownload/{fileId}" })
    public ModelAndView fileDownload(HttpServletResponse response,
                             HttpServletRequest request, @PathVariable String fileId,String jsessionid) {
        SessionInfo sessionInfo = SecurityUtils.getSessionInfo(jsessionid);
        sessionInfo = null != sessionInfo ? sessionInfo:SecurityUtils.getCurrentSessionInfo();
        if(null == sessionInfo){
            throw new ActionException("未授权！");
        }
        File file = fileService.get(fileId);
        return downloadSingleFileUtil(response, request, file);

    }



    private ModelAndView downloadSingleFileUtil(HttpServletResponse response,
                                        HttpServletRequest request, File file) {
        ActionException fileNotFoldException = new ActionException("文件不存在，已被删除或移除。");
        if (file == null) {
            throw fileNotFoldException;
        }

        java.io.File diskFile = file.getDiskFile();
        if (!diskFile.exists() || !diskFile.canRead()) {
            throw fileNotFoldException;
        }
        String filename = file.getName();
        long fileLength = diskFile.length();// 记录文件大小
        long pastLength = 0;// 记录已下载文件大小
        long toLength = 0;// 记录客户端需要下载的字节段的最后一个字节偏移量（比如bytes=27000-39000，则这个值是为39000）
        long contentLength = 0;// 客户端请求的字节总量
        String rangeBytes = "";// 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容

        // ETag header
        // The ETag is contentLength + lastModified
        response.setHeader("ETag","W/\"" + fileLength + "-" + diskFile.lastModified() + "\"");
        // Last-Modified header
        response.setHeader("Last-Modified",new Date(diskFile.lastModified()).toString());

        if (request.getHeader("Range") != null) {// 客户端请求的下载的文件块的开始字节
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            logger.debug("request.getHeader(\"Range\")="+ request.getHeader("Range"));
            rangeBytes = request.getHeader("Range").replaceAll("bytes=", "");
            if (rangeBytes.indexOf('-') == rangeBytes.length() - 1) {// bytes=969998336-
                rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                pastLength = Long.parseLong(rangeBytes.trim());
                toLength = fileLength - 1;
            } else {// bytes=1275856879-1275877358
                String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                String temp2 = rangeBytes.substring(
                        rangeBytes.indexOf('-') + 1, rangeBytes.length());
                // bytes=1275856879-1275877358，从第 1275856879个字节开始下载
                pastLength = Long.parseLong(temp0.trim());
                // bytes=1275856879-1275877358，到第 1275877358 个字节结束
                toLength = Long.parseLong(temp2);
            }
        } else {// 从开始进行下载
            toLength = fileLength - 1;
        }
        // 客户端请求的是1275856879-1275877358 之间的字节
        contentLength = toLength - pastLength + 1;
        if (contentLength < Integer.MAX_VALUE) {
            response.setContentLength((int) contentLength);
        } else {
            // Set the content-length as String to be able to use a long
            response.setHeader("content-length", "" + contentLength);
        }
        String contentType = AppUtils.getServletContext().getMimeType(filename);
        if (null != contentType) {
            response.setContentType(contentType);
        }else{
            response.setContentType("application/x-download");
        }

        // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
        response.setHeader("Accept-Ranges", "bytes");
        // 必须先设置content-length再设置header
        response.addHeader("Content-Range", "bytes " + pastLength + "-" + toLength + "/" + fileLength);
        int bufferSize = 2048;
        response.setBufferSize(bufferSize);

        InputStream istream = null;
        OutputStream os = null;
        try {
            WebUtils.setDownloadableHeader(request,response,filename);
            os = response.getOutputStream();
            istream = new BufferedInputStream(new FileInputStream(diskFile), bufferSize);
            try {
                IoUtils.copy(istream, os, pastLength, toLength);
            } catch (IOException ie) {
                /**
                 * 在写数据的时候， 对于 ClientAbortException 之类的异常，
                 * 是因为客户端取消了下载，而服务器端继续向浏览器写入数据时， 抛出这个异常，这个是正常的。
                 * 尤其是对于迅雷这种吸血的客户端软件， 明明已经有一个线程在读取 bytes=1275856879-1275877358，
                 * 如果短时间内没有读取完毕，迅雷会再启第二个、第三个。。。线程来读取相同的字节段， 直到有一个线程读取完毕，迅雷会 KILL
                 * 掉其他正在下载同一字节段的线程， 强行中止字节读出，造成服务器抛 ClientAbortException。
                 * 所以，我们忽略这种异常
                 */
                // ignore
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IoUtils.closeSilently(istream);
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param fileIds 入参Ids拼接字符串
     * @throws Exception
     */
    @Logging(logType = LogType.access,value = "下载文件")
    @RequestMapping(value = { "downloadDiskFile" })
    public ModelAndView downloadDiskFile(
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestParam(value = "fileIds", required = false) List<String> fileIds)
            throws Exception {
        if (Collections3.isNotEmpty(fileIds)) {
            if (fileIds.size() == 1) {
                File file = fileService.get(fileIds.get(0));
                downloadSingleFileUtil(response, request, file);
            } else {
                List<File> fileList = fileService.findFilesByIds(fileIds);
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
     * @param fileList 文件对象集合
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
     * 清空缓存目录 正在运行时 慎用
     * @return
     */
    @Logging(logType = LogType.access,value = "我的云盘-清空缓存目录")
    @RequestMapping(value = { "clearTempDir" })
    @ResponseBody
    public Result clearTempDir(){
        logger.info("清空缓存目录...");
        DiskUtils.clearTempDir();
        logger.info("清空缓存目录完毕");
        return Result.successResult();
    }


}