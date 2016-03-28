/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

/**
 * FTP 使用Resource方式注解 <br>
 * 例如：@Resource(name = "payFtp")
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-3-20 下午1:30:39
 */
public class FtpFactory {

    // 可使用@Resource进行注解
    /**
     * 服务器地址
     */
    private String url;
    /**
     * FTP端口
     */
    private int port;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    public FtpFactory() {
    }

    public FtpFactory(String url, int port, String username, String password) {
        this.url = url;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 向FTP服务器上传文件.
     * 
     * @param path
     *            FTP服务器保存目录
     * @param filename
     *            上传到FTP服务器上的文件名
     * @param input
     *            输入流
     * @return 成功返回true，否则返回false
     */
    public boolean ftpUploadFile(String path, String filename, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        try {
            int reply;
            ftp.connect(url, port);
            ftp.login(username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            // 转到指定上传目录
            ftp.changeWorkingDirectory(path);
            ftp.setBufferSize(1024);
            ftp.setFileType(FTPClient.ASCII_FILE_TYPE);
            // 将上传文件存储到指定目录
            ftp.storeFile(filename, input);
            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * 向FTP服务器上传文件.
     * 
     * @param path
     *            FTP服务器保存目录
     * @param filename
     *            上传到FTP服务器上的文件名
     * @param inputFilename
     *            输入流
     * @return 成功返回true，否则返回false
     */
    public boolean ftpUploadFile(String path, String filename,
            String inputFilename) {
        File file = new File(inputFilename);
        try {
            return ftpUploadFile(path, filename, new BufferedInputStream(
                    new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    /**
     * 从FTP服务器下载文件.
     * 
     * @param remotePath
     *            FTP服务器上的相对路径
     * @param fileName
     *            要下载的文件名
     * @param localPath
     *            下载后保存到本地的路径
     * @return
     */
    public boolean ftpDownFile(String remotePath, String fileName,
            String localPath) {
        // 初始表示下载失败
        boolean success = false;
        // 创建FTPClient对象
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            // 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.connect(url, port);
            // 登录ftp
            ftp.login(username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            // 转到指定下载目录
            ftp.changeWorkingDirectory(remotePath);
            // 列出该目录下所有文件
            FTPFile[] fs = ftp.listFiles();
            // 遍历所有文件，找到指定的文件
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    // 根据绝对路径初始化文件
                    File localFile = new File(localPath + "/" + ff.getName());
                    // 输出流
                    OutputStream is = new FileOutputStream(localFile);
                    // 下载文件
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                    success = true;
                }
            }
            ftp.logout();
            // 下载成功

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }
}
