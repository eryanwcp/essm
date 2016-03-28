package com.eryansky.modules.disk.extend;

import com.eryansky.modules.disk.entity.Folder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
public interface IFileManager {

    //枚举上传状态
    public enum UploadStatus {
        Create_Directory_Fail,      //远程服务器相应目录创建失败
        Create_Directory_Success,   //远程服务器创建目录成功
        Upload_New_File_Success,    //上传新文件成功
        Upload_New_File_Failed,     //上传新文件失败
        File_Exits,                 //文件已经存在
        Remote_Bigger_Local,        //远程文件大于本地文件
        Upload_From_Break_Success,  //断点续传成功
        Upload_From_Break_Failed,   //断点续传失败
        Delete_Remote_Faild;        //删除远程文件失败
    }

    //枚举下载状态
    public enum DownloadStatus {
        Remote_File_Noexist,    //远程文件不存在
        Local_Bigger_Remote,    //本地文件大于远程文件
        Download_From_Break_Success,    //断点下载文件成功
        Download_From_Break_Failed,     //断点下载文件失败
        Download_New_Success,           //全新下载文件成功
        Download_New_Failed;            //全新下载文件失败
    }

    void init();

    void destroy();

    /**
     * 保存文件
     *
     * @param path          存储文件路径
     * @param localPath 本地文件路径
     * @param coverFile     是否覆盖
     * @return
     */
    UploadStatus saveFile(String path, String localPath, boolean coverFile) throws IOException;

    /**
     * @param path      存储文件路径
     * @param inputStream     本地文件路径输入流
     * @param coverFile 是否覆盖
     * @return
     */
    UploadStatus saveFile(String path, InputStream inputStream, boolean coverFile) throws IOException;

    /**
     * 加载文件
     *
     * @param path      存储文件路径
     * @param localPath 本地文件路径
     * @return
     */
    DownloadStatus loadFile(String path, String localPath) throws IOException;

    /**
     * 删除文件
     *
     * @param path     存储文件路径
     */
    UploadStatus deleteFile(String path) throws IOException;

    String getStorePath(Folder folder,String userId,String fileName);
}
