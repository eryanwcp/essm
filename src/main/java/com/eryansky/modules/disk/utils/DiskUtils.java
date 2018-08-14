/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
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
import com.eryansky.modules.disk._enum.FolderType;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.modules.disk.service.FileService;
import com.eryansky.modules.disk.service.FolderService;
import com.eryansky.modules.sys.mapper.User;
import com.google.common.collect.Lists;
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
import java.util.*;
import java.io.*;

/**
 * 云盘公共接口 以及相关工具类
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-12-10
 */
public class DiskUtils {

    protected static Logger logger = LoggerFactory.getLogger(DiskUtils.class);

    private static FolderService folderService = SpringContextHolder.getBean(FolderService.class);
    private static FileService fileService = SpringContextHolder.getBean(FileService.class);
    private static IFileManager iFileManager = SpringContextHolder.getBean("iFileManager");
    /**
     * 文件上传失败提示信息
     */
    public static final String UPLOAD_FAIL_MSG = "文件上传失败！";
    /**
     * 文件虚拟路径 用于文件转发
     */
    public static final String FILE_VIRTUAL_PATH = "disk/fileDownload/";


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
        FileUtils.checkSaveDir(path);
        return path;
    }

    /**
     * 本地磁盘存储目录
     * @param folderCode
     * @param userId
     * @return
     */
    public static String getDISKStoreDir(String  folderCode, String userId) {
        Folder folder = new Folder();
        folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
        folder.setCode(folderCode);
        return getDISKStoreDir(folder,userId);
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
     * @param code 系统文件夹编码
     * @return
     */
    public static Folder checkAndSaveSystemFolderByCode(String code) {
        return folderService.checkAndSaveSystemFolderByCode(code);
    }
    /**
     * 根据编码获取 获取系统文件夹 <br/>
     * 如果不存在则自动创建
     *
     * @param code 系统文件夹编码
     * @param userId 用户ID
     * @return
     */
    public static Folder checkAndSaveSystemFolderByCode(String code, String userId) {
        return folderService.checkAndSaveSystemFolderByCode(code,userId);
    }


    /**
     * 保存系统文件
     *
     * @param folderCode
     *            系统文件夹编码
     * @param userId 用户ID 允允许为null
     * @param multipartFile
     *            上传文件对象 SpringMVC
     * @return
     * @throws InvalidExtensionException
     * @throws FileUploadBase.FileSizeLimitExceededException
     * @throws FileNameLengthLimitExceededException
     * @throws IOException
     */
    public static File saveSystemFile(String folderCode, String userId,
                                      MultipartFile multipartFile) throws InvalidExtensionException,
            FileUploadBase.FileSizeLimitExceededException,
            FileNameLengthLimitExceededException, IOException {
        String _userId = StringUtils.isBlank(userId) ? User.SUPERUSER_ID:userId;
        String code = FileUploadUtils.encodingFilenamePrefix(_userId + "",multipartFile.getOriginalFilename());
        Folder folder = checkAndSaveSystemFolderByCode(folderCode, _userId);
        String storeFilePath = iFileManager.getStorePath(folder,_userId,multipartFile.getOriginalFilename());
        File file = new File();
        file.setFolderId(folder.getId());
        file.setCode(code);
        file.setUserId(_userId);
        file.setName(multipartFile.getOriginalFilename());
        file.setFilePath(storeFilePath);
        file.setFileSize(multipartFile.getSize());
        file.setFileSuffix(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
        iFileManager.saveFile(file.getFilePath(),multipartFile.getInputStream(), true);
        fileService.save(file);
        return file;
    }

    /**
     * 保存系统文件
     *
     * @param folderCode
     *            系统文件夹编码
     * @param userId 用户ID 允允许为null
     * @param inputStream 文件输入流
     * @param fileName
     *            文件名称
     * @return
     * @throws InvalidExtensionException
     * @throws FileUploadBase.FileSizeLimitExceededException
     * @throws FileNameLengthLimitExceededException
     * @throws IOException
     */
    public static File saveSystemFile(String folderCode, String userId,
                                      InputStream inputStream,String fileName) throws InvalidExtensionException,
            FileUploadBase.FileSizeLimitExceededException,
            FileNameLengthLimitExceededException, IOException {
        String _userId = StringUtils.isBlank(userId) ? User.SUPERUSER_ID:userId;
        String code = FileUploadUtils.encodingFilenamePrefix(_userId + "",fileName);
        Folder folder = checkAndSaveSystemFolderByCode(folderCode, _userId);
        String storeFilePath = iFileManager.getStorePath(folder,_userId,fileName);
        File file = new File();
        file.setFolderId(folder.getId());
        file.setCode(code);
        file.setUserId(_userId);
        file.setName(fileName);
        file.setFilePath(storeFilePath);
        file.setFileSize(Long.valueOf(inputStream.available()));
        file.setFileSuffix(FilenameUtils.getExtension(fileName));
        iFileManager.saveFile(file.getFilePath(),inputStream, true);
        fileService.save(file);
        return file;
    }

    /**
     * 保存系统文件
     *
     * @param folderCode
     *            系统文件夹编码
     * @param userId 用户ID 允允许为null
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
    public static File saveSystemFile(String folderCode, String userId,HSSFWorkbook workbook, String fileName) throws InvalidExtensionException,
            FileUploadBase.FileSizeLimitExceededException,
            FileNameLengthLimitExceededException, IOException {
        String _userId = StringUtils.isBlank(userId) ? User.SUPERUSER_ID:userId;
        String code = FileUploadUtils.encodingFilenamePrefix(_userId + "",fileName);
        Folder folder = checkAndSaveSystemFolderByCode(folderCode, _userId);
        String storeFilePath = iFileManager.getStorePath(folder,_userId,fileName);
        File file = new File();
        file.setFolderId(folder.getId());
        file.setCode(code);
        file.setUserId(_userId);
        file.setName(fileName);
        file.setFilePath(storeFilePath);
        file.setFileSize(Long.valueOf(workbook.getBytes().length));
        file.setFileSuffix(FilenameUtils.getExtension(fileName));
        FileOutputStream fileOut = new FileOutputStream(storeFilePath);
        workbook.write(fileOut);
        fileOut.close();
//        iFileManager.saveFile(file.getFilePath(),multipartFile.getInputStream(), true);
        fileService.save(file);
        return file;
    }


    /**
     * 获取文件的位置
     *
     * @param folderId
     *            文件所属文件夹
     * @return
     */
    public static String getFileLocationName(String folderId) {
        StringBuffer location = new StringBuffer("");
        Folder folder = folderService.get(folderId);
        if (folder != null) {
            String type = folder.getType();// 文件夹类型
            String userName = folder.getUserName();// 文件夹创建人
            String folderName = folder.getName();// 文件夹名称
            String folderAuthorize = folder.getFolderAuthorize();// 文件夹授权类型
            if (FolderAuthorize.User.getValue().equals(folderAuthorize)) {
                location.append(FolderAuthorize.User.getDescription())
                        .append("：").append(userName);
                if (FolderType.NORMAL.getValue().equals(type)) {
                    location.append("： ").append(folderName);
                }
            }

        }
        return location.toString();
    }


    /**
     * 获取文件夹文件
     * @param folderId
     * @return
     */
    public static List<File> findFolderFiles(String folderId) {
        return findFolderFiles(folderId,null);
    }


    /**
     * 获取文件夹文件
     * @param folderId
     * @return
     */
    public static List<File> findFolderFiles(String folderId, Collection<String> fileSuffixs) {
        return fileService.findFolderFiles(folderId,fileSuffixs);
    }

    /**
     * 更新文件
     * @param fildIds 文件IDS
     * @return
     */
    public static void deleteFolderFiles(Collection<String> fildIds){
        fileService.deleteFileByFileIds(fildIds);
    }

    /**
     * 根据IDS查找文件
     * @param fildIds 文件IDS
     * @return
     */
    public static List<File> findFilesByIds(Collection<String> fildIds) {
        return fileService.findFilesByIds(fildIds);
    }


    /**
     * 复制文件
     * @param userId 用户ID
     * @param folderCode 文件夹编码
     * @param fildIds 文件IDS
     * @return
     */
    public static List<File> copyFiles(String userId,String folderCode,Collection<String> fildIds) {
        List<File> sourceFiles = findFilesByIds(fildIds);
        if(Collections3.isEmpty(sourceFiles)){
            return Collections.emptyList();
        }
        List<File> newFiles = new ArrayList<File>(sourceFiles.size());
        for (File sourceFile : sourceFiles) {
            File file = sourceFile.copy();
            file.setFolderId(DiskUtils.checkAndSaveSystemFolderByCode(folderCode,userId).getId());
            file.setUserId(userId);
            DiskUtils.saveFile(file);
            newFiles.add(file);
        }
        return newFiles;
    }

    /**
     * 提取文件IDS
     * @param files 文件集合
     * @return
     */
    public static List<String> toFileIds(Collection<File> files) {
        if(Collections3.isEmpty(files)){
            return Collections.emptyList();
        }
        List<String> newFileIds = Lists.newArrayList();
        for (File file : files) {
            newFileIds.add(file.getId());
        }
        return newFileIds;
    }


    /**
     * 统计文件大小
     * @param fileIds 文件ID集合
     * @return
     */
    public static long countFileSize(Collection<String> fileIds){
        if(Collections3.isNotEmpty(fileIds)){
            return fileService.countFileSize(fileIds);
        }
        return 0L;
    }

    /**
     * 根据文件ID获取文件
     * @param fileId
     * @return
     */
    public static File getFile(String fileId){
        return fileService.get(fileId);
    }

    /**
     * 保存文件
     * @param file 文件
     * @return
     */
    public static void saveFile(File file){
        fileService.save(file);
    }


    /**
     * 删除文件
     * @param fileId 文件ID
     * @return
     */
    public static void deleteFile(String fileId){
        Validate.notNull(fileId, "参数[fileId]不能为null.");
        fileService.deleteByFileId(fileId);
    }
    /**
     * 删除文件
     * @param file
     * @return
     */
    public static void deleteFile(File file){
        Validate.notNull(file, "参数[file]不能为null.");
        fileService.deleteByFileId(file.getId());
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

    /**
     * 获取文件路径
     * @param fileId
     * @return
     */
    public static String getFileUrl(String fileId){
        File file = getFile(fileId);
        if(file != null){
            return file.getUrl();
        }
        return null;
    }



    public static java.io.File getDiskFile(String fileId){
        if(fileId == null){
            return null;
        }
        File file = fileService.get(fileId);
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
        if (SecurityUtils.isUserAdmin(_userId) || SecurityUtils.isPermittedRole(AppConstants.ROLE_SYSTEM_MANAGER) || SecurityUtils.isPermittedRole(AppConstants.ROLE_DISK_MANAGER)) {//系统管理员 + 网盘管理员
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
            }else if(displayName.endsWith(".mp4")){
                contentType = "video/mp4";
            }else if(displayName.endsWith(".m4v")){
                contentType = "video/m4v";
            }else if(displayName.endsWith(".webm")){
                contentType = "video/webm";
            }else if(displayName.endsWith(".ogg")){
                contentType = "video/ogg";
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