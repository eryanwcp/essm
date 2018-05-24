/**
*  Copyright (c) 2012-2018 http://www.eryansky.com
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*/
package com.eryansky.modules.disk.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.disk._enum.FileSizeType;
import com.eryansky.modules.disk.extend.IFileManager;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eryansky.modules.disk.mapper.File;
import com.eryansky.modules.disk.dao.FileDao;
import com.eryansky.core.orm.mybatis.service.CrudService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 *  service
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-04
 */
@Service
@Transactional(readOnly = true)
public class FileService extends CrudService<FileDao, File> {


    @Autowired
    private IFileManager iFileManager;

    /**
     *
     * 文件删除
     * @param fileId 文件ID
     */
    @Transactional(readOnly = false)
    public void deleteFile(String fileId){
        File file = dao.get(fileId);
        try {
            //检查文件是否被引用
            List<File> files = this.getFileByCode(file.getCode(),fileId);
            if(Collections3.isEmpty(files)){
                iFileManager.deleteFile(file.getFilePath());
                logger.debug("删除文件：{}", new Object[]{file.getFilePath()});
            }
            dao.delete(file);
        } catch (IOException e) {
            logger.error("删除文件[{}]失败,{}",new Object[]{file.getFilePath(),e.getMessage()});
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    /**
     *
     * 文件级联删除
     *
     * @param fileCodes
     *            文件编码集合
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public void cascadeDelFile(List<String> fileCodes)throws Exception {
        if (Collections3.isNotEmpty(fileCodes)) {
            for (String code : fileCodes) {
                List<File> fileList = getFileByCode(code, null);
                if (Collections3.isNotEmpty(fileList)) {
                    for (File file : fileList) {
                        deleteFile(file.getId());
                    }
                }
            }
        } else {
            logger.warn("参数[ids]为空.");
        }

    }



    /**
     *
     * 文件删除
     * @param fileIds 文件集合
     */
    @Transactional(readOnly = false)
    public void deleteFolderFiles(List<String> fileIds) {
        if (Collections3.isNotEmpty(fileIds)) {
            for (String fileId : fileIds) {
                deleteFile(fileId);
            }
        } else {
            logger.warn("参数[ids]为空.");
        }
    }


    /**
     * 查找文件夹下所有文件
     * @param folderId 文件夹ID
     * @return
     */
    public List<File> getFolderFiles(String folderId) {
        return getFolderFiles(folderId, null);
    }

    /**
     * 查找文件夹下所有文件
     * @param folderId 文件夹ID
     * @param fileSuffixs 文件后缀名
     * @return
     */
    public List<File> getFolderFiles(String folderId,List<String> fileSuffixs) {
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);
        parameter.put("folderId",folderId);
        parameter.put("fileSuffixs",fileSuffixs);
        return dao.findFolderFiles(parameter);
    }




    /**
     * 统计文件大小
     * @param fileIds 文件ID集合
     * @return
     */
    public long countFileSize(List<String> fileIds){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("fileIds",fileIds);
        return dao.countFileSize(parameter);
    }


    /**
     * 根据文件标识获取文件
     * @param code 文件标识
     * @param excludeFileId 排除的文件ID  可为null
     * @return
     */
    private List<File> getFileByCode(String code,String excludeFileId){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("code",code);
        parameter.put("excludeFileId",excludeFileId);
        return dao.findByCode(parameter);
    }

    /**
     * 根据ID查找
     * @param fileIds
     * @return
     */
    public List<File> findFilesByIds(List<String> fileIds){
        Parameter parameter = Parameter.newParameter();
        parameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        parameter.put("fileIds",fileIds);
        return dao.findFilesByIds(parameter);
    }

    @Override
    public Page<File> findPage(Page<File> page, File entity) {
        return super.findPage(page, entity);
    }



    /**
     * 文件检索
     *
     * @param userId
     *            是否指定权限人员
     * @param fileName
     *            文件名称
     * @param folderAuthorize
     *            云盘类型
     * @param fileSizeType
     *            文件大小类型
     * @param startTime
     *            上传时间启
     * @param endTime
     *            上传时间止
     * @return
     */

    public Page<File> searchFilePage(Page<File> page, String userId,
                                     String fileName, String folderAuthorize, String fileSizeType,
                                     Date startTime, Date endTime) {
        Parameter patameter = new Parameter();
        patameter.put(BaseInterceptor.PAGE,page);
        patameter.put(DataEntity.FIELD_STATUS,DataEntity.STATUS_NORMAL);
        patameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());

        patameter.put("folderAuthorize", folderAuthorize);
        patameter.put("userId", userId);
        patameter.put("query", fileName);
        patameter.put("fileSizeType", fileSizeType);
        patameter.put("startTime", startTime == null ? startTime:DateUtils.formatDateTime(startTime));
        patameter.put("endTime", endTime == null ? endTime:DateUtils.formatDateTime(endTime));
        if (fileSizeType != null) {
            Long minSize = 10 * 1024 * 1024L;
            Long maxSize = 100 * 1024 * 1024L;
            if (FileSizeType.MIN.getValue().equals(fileSizeType)) {
                patameter.put("fileSize", minSize);
            } else if (FileSizeType.MIDDEN.getValue().equals(fileSizeType)) {
                patameter.put("minSize", minSize);
                patameter.put("maxSize", maxSize);
            } else if (FileSizeType.MAX.getValue().equals(fileSizeType)) {
                patameter.put("fileSize", maxSize);
            }
        }
        return page.setResult(dao.findAdvenceQueryList(patameter));
    }

}
