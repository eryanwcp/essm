/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.entity._enum.FolderType;
import com.eryansky.modules.disk.service.FileManager;
import com.eryansky.modules.sys.service.OrganManager;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-02-03
 */
public class FileUtils {

    private static FileManager fileManager = SpringContextHolder.getBean(FileManager.class);


    public static File getFile(String fileId){
        return fileManager.loadById(fileId);
    }




    /**
     * 获取文件的位置
     *
     * @param folder
     *            文件所属文件夹
     * @return
     */
    public static String getFileLocationName(Folder folder) {
        StringBuffer location = new StringBuffer("");
        if (folder != null) {
            Integer type = folder.getType();// 文件夹类型
            String userName = folder.getUserName();// 文件夹创建人
            String folderName = folder.getName();// 文件夹名称
            Integer folderAuthorize = folder.getFolderAuthorize();// 文件夹授权类型
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
    public static List<File> getFolderFiles(String folderId) {
        return fileManager.getFolderFiles(folderId);
    }


    /**
     * 获取文件夹文件
     * @param folderId
     * @return
     */
    public static List<File> getFolderFiles(String folderId,List<String> fileSuffixs) {
        return fileManager.getFolderFiles(folderId,fileSuffixs);
    }

    /**
     * 统计文件大小
     * @param fileIds 文件ID集合
     * @return
     */
    public static long countFileSize(List<String> fileIds){
        if(Collections3.isNotEmpty(fileIds)){
            return fileManager.countFileSize(fileIds);
        }
        return 0L;
    }
}
