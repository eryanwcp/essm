/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.modules.disk.service.DiskService;
import com.eryansky.modules.sys.service.UserService;
import com.google.common.collect.Maps;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.mapper.File;
import com.eryansky.modules.disk.mapper.Folder;
import com.eryansky.modules.disk._enum.FolderAuthorize;
import com.eryansky.utils.AppConstants;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.io.*;

/**
 * 云盘公共接口 以及相关工具类
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-12-10
 */
public class DiskUtils {

    protected static Logger logger = LoggerFactory.getLogger(DiskUtils.class);

    private static UserService userService = SpringContextHolder.getBean(UserService.class);
    private static DiskService diskService = SpringContextHolder.getBean(DiskService.class);
    private static IFileManager iFileManager = SpringContextHolder.getBean("iFileManager");
    /**
     * 文件夹标识 通知
     */
    public static String FOLDER_NOTICE = "notice";
    /**
     * kindeditor
     */
    public static String FOLDER_KINDEDITOR = "kindeditor";
    /**
     * 用户头像
     */
    public static String FOLDER_USER_PHOTO = "userphoto";
    /**
     * 文件上传失败提示信息
     */
    public static final String UPLOAD_FAIL_MSG = "文件上传失败！";
    /**
     * 文件虚拟路径 用于文件转发
     */
    public static final String FILE_VIRTUAL_PATH = "disk/file/";

    /**
     * KindEditor编辑器文件
     * @param userId
     * @return
     */
    public static String getKindEditorRelativePath(String userId) {
        Folder folder = new Folder();
        folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
        folder.setCode(FOLDER_KINDEDITOR);
        return getDISKStoreDir(folder, userId);
    }


    /**
     * 得到用户头像出差相对路径
     * @param userId 用户ID
     * @return
     */
    public static String getUserPhotoRelativePath(String userId) {
        Folder folder = new Folder();
        folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
        folder.setCode(FOLDER_USER_PHOTO);
        return getDISKStoreDir(folder, userId);
    }


    /**
     * 得到用户相对路径
     * @param folderCode 文件夹编码
     * @param userId 用户ID
     * @return
     */
    public static String getRelativePath(String folderCode,String userId) {
        Folder folder = new Folder();
        folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
        folder.setCode(folderCode);
        return getDISKStoreDir(folder, userId);
    }


    /**
     *  生成对象保存的相对地址
     *
     * @param folder
     *            文件夹
     * @return
     */
    public static String getRelativePath(Folder folder, String userId) {
        Date now = Calendar.getInstance().getTime();
        StringBuffer path = new StringBuffer();
        path.append(DateFormatUtils.format(now, "yyyy"))
                .append(java.io.File.separator);
        String folderAuthorize = FolderAuthorize.getByValue(folder.getFolderAuthorize()).toString()
                .toLowerCase();
        path.append(userId).append(java.io.File.separator)
                .append(folderAuthorize).append(java.io.File.separator);
        if (FolderAuthorize.User.getValue().equals(folder.getFolderAuthorize())) {
            path.append(folder.getId());
        } else if(FolderAuthorize.SysTem.getValue().equals(
                folder.getFolderAuthorize())) {
            path.append(folder.getCode());
        }
        return path.toString();
    }

    /**
     * 本地磁盘存储目录
     * @param folder
     * @param userId
     * @return
     */
    public static String getDISKStoreDir(Folder folder, String userId) {
        String path = getRelativePath(folder, userId);
        com.eryansky.common.utils.io.FileUtils.checkSaveDir(path);
        return path;
    }

    /**
     * FTP存储目录
     * @param folder
     * @param userId
     * @return
     */
    public static String getFTPStoreDir(Folder folder, String userId) {
        Date now = Calendar.getInstance().getTime();
        String _S = "/";
        StringBuffer path = new StringBuffer();
        path.append(DateFormatUtils.format(now, "yyyy"))
                .append(_S);
        String folderAuthorize = FolderAuthorize
                .getByValue(folder.getFolderAuthorize()).toString()
                .toLowerCase();
        path.append(userId).append(_S)
                .append(folderAuthorize).append(_S);
        if (FolderAuthorize.User.getValue().equals(folder.getFolderAuthorize())) {
            path.append(folder.getId());
        } else if (FolderAuthorize.SysTem.getValue().equals(
                folder.getFolderAuthorize())) {
            path.append(folder.getCode());
        }
        return path.toString();
    }


    /**
     * 根据编码获取 获取系统文件夹 <br/>
     * 如果不存在则自动创建
     *
     * @param code
     *            系统文件夹编码
     * @return
     */
    public static Folder getSystemFolderByCode(String code) {
        return diskService.checkSystemFolderByCode(code);
    }

    public static Folder getSystemFolderByCode(String code,String userId) {
        return diskService.checkSystemFolderByCode(code,userId);
    }

    /**
     * 获取用户通知文件夹
     * @param userId
     * @return
     */
    public static Folder getUserNoticeFolder(String userId) {
        return diskService.checkSystemFolderByCode(FOLDER_NOTICE,userId);
    }


    /**
     * 获取用户头像文件夹
     * @param userId
     * @return
     */
    public static Folder getUserPhotoFolder(String userId) {
        return diskService.checkSystemFolderByCode(FOLDER_USER_PHOTO,userId);
    }

    /**
     * 获取用户KindEditor编辑器文件夹
     * @param userId
     * @return
     */
    public static Folder getUserKindEditorFolder(String userId) {
        return diskService.checkSystemFolderByCode(FOLDER_KINDEDITOR,userId);
    }


    /**
     * 保存系统文件
     *
     * @param folderCode
     *            系统文件夹编码
     * @param sessionInfo
     *            session信息 允允许为null
     * @param multipartFile
     *            上传文件对象 SpringMVC
     * @return
     * @throws InvalidExtensionException
     * @throws FileUploadBase.FileSizeLimitExceededException
     * @throws FileNameLengthLimitExceededException
     * @throws IOException
     */
    public static File saveSystemFile(String folderCode, SessionInfo sessionInfo,
                                      MultipartFile multipartFile) throws InvalidExtensionException,
            FileUploadBase.FileSizeLimitExceededException,
            FileNameLengthLimitExceededException, IOException {
        String userId = null;
        if (sessionInfo != null && sessionInfo.getUserId() != null) {
            userId = sessionInfo.getUserId();
        }

        String code = FileUploadUtils.encodingFilenamePrefix(userId + "",multipartFile.getOriginalFilename());
        Folder folder = getSystemFolderByCode(folderCode, userId);
        String storeFilePath = iFileManager.getStorePath(folder,userId,multipartFile.getOriginalFilename());
        File file = new File();
        file.setFolderId(folder.getId());
        file.setCode(code);
        file.setUserId(userId);
        file.setName(multipartFile.getOriginalFilename());
        file.setFilePath(storeFilePath);
        file.setFileSize(multipartFile.getSize());
        file.setFileSuffix(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
        iFileManager.saveFile(file.getFilePath(),multipartFile.getInputStream(), true);
        diskService.saveFile(file);
        return file;
    }

    /**
     * 保存系统文件
     *
     * @param folderCode
     *            系统文件夹编码
     * @param sessionInfo
     *            session信息 允允许为null
     *
     * @param workbook
     * @param fileName
     *
     * @return
     * @throws InvalidExtensionException
     * @throws FileUploadBase.FileSizeLimitExceededException
     * @throws FileNameLengthLimitExceededException
     * @throws IOException
     */
    public static File saveExcelFile(String folderCode, SessionInfo sessionInfo,HSSFWorkbook workbook, String fileName) throws InvalidExtensionException,
            FileUploadBase.FileSizeLimitExceededException,
            FileNameLengthLimitExceededException, IOException {
        String userId = null;
        if (sessionInfo != null && sessionInfo.getUserId() != null) {
            userId = sessionInfo.getUserId();
        }

        String code = FileUploadUtils.encodingFilenamePrefix(userId + "",fileName);
        Folder folder = getSystemFolderByCode(folderCode, userId);
        String storeFilePath = iFileManager.getStorePath(folder,userId,fileName);
        File file = new File();
        file.setFolderId(folder.getId());
        file.setCode(code);
        file.setUserId(userId);
        file.setName(fileName);
        file.setFilePath(storeFilePath);
        file.setFileSize(Long.valueOf(workbook.getBytes().length));
        file.setFileSuffix(FilenameUtils.getExtension(fileName));
        FileOutputStream fileOut = new FileOutputStream(storeFilePath);
        workbook.write(fileOut);
        fileOut.close();
//        iFileManager.saveFile(file.getFilePath(),multipartFile.getInputStream(), true);
        diskService.saveFile(file);
        return file;
    }


    /**
     * 保存通知文件
     *
     * @param sessionInfo
     * @param multipartFile
     * @return
     * @throws InvalidExtensionException
     * @throws FileUploadBase.FileSizeLimitExceededException
     * @throws FileNameLengthLimitExceededException
     * @throws IOException
     */
    public static File saveNoticeFile(SessionInfo sessionInfo, MultipartFile multipartFile)
            throws InvalidExtensionException,
            FileUploadBase.FileSizeLimitExceededException,
            FileNameLengthLimitExceededException, IOException {
        return saveSystemFile(DiskUtils.FOLDER_NOTICE, sessionInfo,
                multipartFile);
    }

    /**
     * 更新文件
     * @param file 文件
     * @return
     */
    public static void updateFile(File file){
        diskService.saveFile(file);
    }


    /**
     * 删除文件
     * @param fileId 文件ID
     * @return
     */
    public static void deleteFile(String fileId){
        Validate.notNull(fileId, "参数[fileId]不能为null.");
        diskService.deleteFile(fileId);
    }
    /**
     * 删除文件
     * @param file
     * @return
     */
    public static void deleteFile(File file){
        Validate.notNull(file, "参数[file]不能为null.");
        diskService.deleteFile( file);
    }


    /**
     * 获取文件虚拟路径
     * @param file
     * @return
     */
    public static String getVirtualFilePath(File file){
//        return AppConstants.getAdminPath() + "/" + FILE_VIRTUAL_PATH + file.getId();
        return AppConstants.getAdminPath() + "/" + FILE_VIRTUAL_PATH + file.getId()+ "."+file.getFileSuffix();
    }


    public static java.io.File getDiskFile(String fileId){
        if(fileId == null){
            return null;
        }
        File file = diskService.getFileById(fileId);
        return getDiskFile(file);
    }

    public static java.io.File getDiskFile(File file){
        if (file == null || file.getId() == null) {
            return null;
        }
//        String tempPath = AppConstants.getDiskTempDir() + java.io.File.separator + file.getCode();
        String tempPath = AppConstants.getDiskTempDir() + java.io.File.separator + file.getCode();
        java.io.File tempFile = new java.io.File(tempPath);
        try {
            if (file != null) {
                if(!tempFile.exists()){
                    iFileManager.loadFile(file.getFilePath(), tempPath);
                }
                return tempFile;
            }
        } catch (IOException e) {
            logger.warn(String.format("请求的文件%s不存在", file.getId()), e.getMessage());
        }
        return null;
    }


//    云盘文件压缩相关

    public static final String ENCODING_DEFAULT = "UTF-8";

    public static final int BUFFER_SIZE_DIFAULT = 1024 * 8;

    public static void makeZip(List<File> inFiles, String zipPath)
            throws Exception {
        makeZip(inFiles, zipPath, ENCODING_DEFAULT);
    }

    public static void makeZip(List<File> inFiles, String zipPath,
                               String encoding) throws Exception {
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(
                new FileOutputStream(zipPath)));
        zipOut.setEncoding(encoding);
        doZipFile(zipOut, inFiles);
        zipOut.flush();
        zipOut.close();
    }

    /**
     * 处理文件同名
     *
     * @param inFiles
     *            文件对象集合
     * @return
     * @throws Exception
     */
    private static void doZipFile(ZipOutputStream zipOut, List<File> inFiles)
            throws Exception {
        if (Collections3.isNotEmpty(inFiles)) {
            Map<String, Integer> countMap = Maps.newHashMap();

            for (File file : inFiles) {
                String name = file.getName();
                Integer mapVal = countMap.get(name);
                String newName = name;
                if (mapVal == null) {
                    mapVal = 0;
                } else {
                    mapVal++;
                    int index = name.lastIndexOf(".");
                    if (index > -1) {
                        newName = (new StringBuffer(name).insert(index, "("
                                + mapVal + ")")).toString();
                    } else {
                        newName = (new StringBuffer(name).append("(" + mapVal
                                + ")")).toString();
                    }
                }
                if (file.getDiskFile().isFile()) {
                    BufferedInputStream bis = new BufferedInputStream(
                            new FileInputStream(file.getDiskFile()));
                    ZipEntry entry = new ZipEntry(newName);
                    zipOut.putNextEntry(entry);
                    byte[] buff = new byte[BUFFER_SIZE_DIFAULT];
                    int size;
                    while ((size = bis.read(buff, 0, buff.length)) != -1) {
                        zipOut.write(buff, 0, size);
                    }
                    zipOut.closeEntry();
                    bis.close();
                }
                countMap.put(name, mapVal);

            }
        }
    }

    /**
     * 清空缓存目录
     */
    public static void clearTempDir(){
        String tempDir = AppConstants.getDiskTempDir();
        java.io.File file = new java.io.File(tempDir);
        FileUtils.deleteFile(file.listFiles());

    }

    /**
     * 云盘管理员 超级管理 + 系统管理员 + 网盘管理员
     * @param userId 用户ID 如果为null,则为当前登录用户ID
     * @return
     */
    public static boolean isDiskAdmin(String userId){
        String _userId = userId;
        if(_userId == null){
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            _userId = sessionInfo.getUserId();
        }

        boolean isAdmin = false;
        if (userService.isSuperUser(_userId) || SecurityUtils.isPermittedRole(AppConstants.ROLE_SYSTEM_MANAGER) || SecurityUtils.isPermittedRole(AppConstants.ROLE_DISK_MANAGER)) {//系统管理员 + 网盘管理员
            isAdmin = true;
        }
        return isAdmin;
    }


    /**
     * 下载文件
     * @param request
     * @param response
     * @param inputStream 输入流
     * @param displayName 下载显示的文件名
     * @throws IOException
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, InputStream inputStream, String displayName) throws IOException {
        response.reset();
        WebUtils.setNoCacheHeader(response);
        String contentType = "application/x-download";
        if(StringUtils.isNotBlank(displayName)){
            if(displayName.endsWith(".doc")){
                contentType = "application/msword";
            }else if (displayName.endsWith(".docx")){
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }else if(displayName.endsWith(".xls")){
                contentType = "application/vnd.ms-excel";
            }else if(displayName.endsWith(".xlsx")){
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }else if(displayName.endsWith(".ppt")){
                contentType = "application/vnd.ms-powerpoint";
            }else if(displayName.endsWith(".pptx")){
                contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            }else if(displayName.endsWith(".pdf")){
                contentType = "application/pdf";
            }else if(displayName.endsWith(".jpg") || displayName.endsWith(".jpeg")){
                contentType = "image/jpeg";
            }else if(displayName.endsWith(".gif")){
                contentType = "image/gif";
            }else if(displayName.endsWith(".bmp")){
                contentType = "image/bmp";
            }
        }


        response.setContentType(contentType);
        response.setContentLength((int) inputStream.available());

//        String displayFilename = displayName.substring(displayName.lastIndexOf("_") + 1);
//        displayFilename = displayFilename.replace(" ", "_");
        WebUtils.setDownloadableHeader(request,response,displayName);
        BufferedInputStream is = null;
        OutputStream os = null;
        try {

            os = response.getOutputStream();
            is = new BufferedInputStream(inputStream);
            IOUtils.copy(is, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}