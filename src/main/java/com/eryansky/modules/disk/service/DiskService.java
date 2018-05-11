/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.core.orm.mybatis.service.BaseService;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.modules.disk._enum.FolderAuthorize;
import com.eryansky.modules.disk.mapper.File;
import com.eryansky.modules.disk.mapper.Folder;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.utils.AppConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-12-10
 */
@Service
public class DiskService extends BaseService{

    @Autowired
    private FolderService folderService;
    @Autowired
    private FileService fileService;
    @Autowired
    private IFileManager iFileManager;


    /**
     * 根据编码获取 获取系统文件夹
     * <br/>如果不存在则自动创建
     * @param code 系统文件夹编码
     * @return
     */
    @Transactional(readOnly = false)
    public Folder checkSystemFolderByCode(String code){
        Validate.notBlank(code, "参数[code]不能为null.");
        List<Folder> list =  folderService.findFoldersByUserId(null,null,FolderAuthorize.SysTem.getValue(),code);
        Folder folder =  list.isEmpty() ? null:list.get(0);
        if(folder == null){
            folder = new Folder();
            folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
            folder.setCode(code);
            folderService.save(folder);
        }
        return folder;
    }


    /**
     * 根据编码获取 获取用户的系统文件夹
     * <br/>如果不存在则自动创建
     * @param code 系统文件夹编码
     * @param userId 用户ID
     * @return
     */
    @Transactional(readOnly = false)
    public Folder checkSystemFolderByCode(String code,String userId){
        Validate.notBlank(code, "参数[code]不能为null.");
        Validate.notNull(userId, "参数[userId]不能为null.");
        List<Folder> list =  folderService.findFoldersByUserId(userId,null,FolderAuthorize.SysTem.getValue(),code);
        Folder folder =  list.isEmpty() ? null:list.get(0);
        if(folder == null){
            folder = new Folder();
            folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
            folder.setCode(code);
            folder.setUserId(userId);
            folderService.save(folder);
        }
        return folder;
    }

    /**
     * 保存系统文件
     * @param folderCode
     * @param file
     * @return
     */
    @Transactional(readOnly = false)
    public File saveSystemFile(String folderCode,File file){
        Validate.notBlank(folderCode, "参数[folderCode]不能为null.");
        Validate.notNull(file, "参数[file]不能为null.");
        Folder folder = checkSystemFolderByCode(folderCode);
        file.setFolderId(folder.getId());
        fileService.save(file);
        return file;
    }

    /**
     * 保存文件
     * @param fileId 文件ID
     * @return
     */
    public File getFileById(String fileId){
        Validate.notNull(fileId, "参数[fileId]不能为null.");
        return fileService.get(fileId);
    }


    /**
     * 保存文件
     * @param file
     * @return
     */
    @Transactional(readOnly = false)
    public File saveFile(File file){
        Validate.notNull(file, "参数[file]不能为null.");
        fileService.save(file);
        return file;
    }


    /**
     * 删除文件
     * @param file
     * @return
     */
    @Transactional(readOnly = false)
    public void deleteFile(File file){
        Validate.notNull(file, "参数[file]不能为null.");
        fileService.deleteFile(file.getId());
    }

    /**
     * 删除文件
     * @param fileId
     */
    @Transactional(readOnly = false)
    public void deleteFile(String fileId){
        Validate.notNull(fileId, "参数[fileId]不能为null.");
        fileService.deleteFile(fileId);
    }




    /**
     * 根据ID查找
     * @param fileIds 文件ID集合
     * @return
     */
    public List<File> findFilesByIds(List<String> fileIds){
        Validate.notEmpty(fileIds, "参数[fileIds]不能为null.");
        if(Collections3.isEmpty(fileIds)){
            return null;
        }
        return fileService.findFilesByIds(fileIds);
    }


    /**
     * 文件上传
     * @param sessionInfo
     * @param folder
     * @param uploadFile
     * @throws Exception
     */
    @Transactional(readOnly = false)
	public File fileUpload(SessionInfo sessionInfo, Folder folder,
			MultipartFile uploadFile) throws Exception {
		File file = null;
/*		Exception exception = null;
*/
        java.io.File tempFile = null;
        try {
			String fullName = uploadFile.getOriginalFilename();
			String code = FileUploadUtils.encodingFilenamePrefix(sessionInfo.getUserId().toString(),
					fullName);
            String storePath = iFileManager.getStorePath(folder,sessionInfo.getUserId(),uploadFile.getOriginalFilename());


            String fileTemp = AppConstants.getDiskTempDir() + java.io.File.separator + code;
            tempFile = new java.io.File(fileTemp);
            FileOutputStream fos = FileUtils.openOutputStream(tempFile);
            IOUtils.copy(uploadFile.getInputStream(), fos);

            iFileManager.saveFile(storePath,fileTemp,false);
			file = new File();
			file.setFolderId(folder.getId());
			file.setCode(code);
			file.setUserId(sessionInfo.getUserId());
			file.setName(fullName);
			file.setFilePath(storePath);
			file.setFileSize(uploadFile.getSize());
			file.setFileSuffix(FilenameUtils.getExtension(fullName));
            fileService.save(file);
		}catch (Exception e) {
            // exception = e;
            throw new ServiceException(DiskUtils.UPLOAD_FAIL_MSG + e.getMessage(), e);
        } finally {
//			if (exception != null && file != null) {
//				DiskUtils.deleteFile(null, file.getId());
//			}
            if(tempFile != null && tempFile.exists()){
                tempFile.delete();
            }

		}
		return file;

	}

}
