/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.extend;

import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.utils.AppConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-06-24 
 */
public class DISKManager implements IFileManager {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public UploadStatus saveFile(String path, String localPath, boolean coverFile) throws IOException {
        FileUtils.copyFile(new File(localPath), new File(AppConstants.getDiskBasePath() + File.separator + path));
        return UploadStatus.Upload_New_File_Success;
    }

    @Override
    public UploadStatus saveFile(String path, InputStream inputStream, boolean coverFile) throws IOException {
        FileUtils.copyInputStreamToFile(inputStream, new File(AppConstants.getDiskBasePath() + File.separator + path));
        return UploadStatus.Upload_New_File_Success;
    }

    @Override
    public DownloadStatus loadFile(String path, String localPath) throws IOException {
        File srcFile = new File(AppConstants.getDiskBasePath()+File.separator+path);
        DownloadStatus downloadStatus = DownloadStatus.Download_New_Failed;
        if(srcFile.exists()){
            FileUtils.copyFile(srcFile,new File(localPath));
            downloadStatus = DownloadStatus.Download_New_Success;
        }
        return downloadStatus;
    }

    @Override
    public UploadStatus deleteFile(String path) throws IOException{
        FileUtils.deleteFile(new File(AppConstants.getDiskBasePath()+File.separator+path));
        return UploadStatus.Delete_Remote_Faild;
    }

    @Override
    public String getStorePath(Folder folder, String userId,String fileName) {
        String path = DiskUtils.getDISKStoreDir(folder, userId);
        String code = FileUploadUtils.encodingFilenamePrefix(userId.toString(),
                fileName);
        path += java.io.File.separator + code + "_" + fileName;
        return path;
    }
}
