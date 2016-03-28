package com.eryansky.modules.disk.extend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.utils.DiskUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

/**
 * 支持断点续传的FTP实用类
 * 实现中文目录创建及中文文件创建，添加对于中文的支持
 */
public class FTPManager implements IFileManager {

    protected static Logger logger = LoggerFactory.getLogger(FTPManager.class);
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

    /**
     * 是否重连
     */
    private boolean reconnect = true;
    private boolean isconnect = false;
    /**
     * 重连间隔时间 默认：1分钟
     */
    private long connectTime = 1 * 60 * 1000L;

    public FTPClient ftpClient = new FTPClient();

    public final static String CHARSET_UTF_8 = "UTF-8";
    public final static String CHARSET_ISO8859_1 = "iso-8859-1";
    String baseWorkDirectory = "";// 基本工作目录



    public FTPManager() {
        //设置将过程中使用到的命令输出到控制台
        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    }

    public FTPManager(String url, int port, String username, String password) {
        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        this.url = url;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /** */
    /**
     * 连接到FTP服务器
     *
     * @param hostname 主机名
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return 是否连接成功
     * @throws IOException
     */
    public boolean connect(String hostname, int port, String username, String password) throws IOException {
        ftpClient.connect(hostname, port);
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            if (ftpClient.login(username, password)) {
                return true;
            }
        }
        disconnect();
        return false;
    }

    /** */
    /**
     * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
     *
     * @param remote 远程文件路径
     * @param local  本地文件路径
     * @return 上传的状态
     * @throws IOException
     */
    public DownloadStatus download(String remote, String local) throws IOException {
        //设置被动模式
        ftpClient.enterLocalPassiveMode();
        //设置以二进制方式传输 
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(baseWorkDirectory);
        ftpClient.changeWorkingDirectory(remote.substring(0, remote.lastIndexOf("/") + 1));
        DownloadStatus result;
        //检查远程文件是否存在
        FTPFile[] files = ftpClient.listFiles(new String(StringUtils.substringAfterLast(remote,"/").getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1));
        if (files.length != 1) {
            logger.error("远程文件不存在");
            return DownloadStatus.Remote_File_Noexist;
        }

        long lRemoteSize = files[0].getSize();
        File f = new File(local);
        //本地存在文件，进行断点下载 
        if (f.exists()) {
            long localSize = f.length();
            //判断本地文件大小是否大于远程文件大小 
            if (localSize >= lRemoteSize) {
                logger.info("本地文件大于远程文件，下载中止");
                return DownloadStatus.Local_Bigger_Remote;
            }

            //进行断点续传，并记录状态 
            FileOutputStream out = new FileOutputStream(f, true);
            ftpClient.setRestartOffset(localSize);
            InputStream in = ftpClient.retrieveFileStream(new String(StringUtils.substringAfterLast(remote,"/").getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1));
            StreamUtils.copy(in,out);

//            byte[] bytes = new byte[1024];
//            long step = lRemoteSize / 100;
//            long process = localSize / step;
//            int c;
//            while ((c = in.read(bytes)) != -1) {
//                out.write(bytes, 0, c);
//                localSize += c;
//                long nowProcess = localSize / step;
//                if (nowProcess > process) {
//                    process = nowProcess;
//                    if (process % 10 == 0)
//                        System.out.println("下载进度：" + process);
//                    //TODO 更新文件下载进度,值存放在process变量中
//                }
//            }
//            in.close();
//            out.close();
            boolean isDo = ftpClient.completePendingCommand();
            if (isDo) {
                result = DownloadStatus.Download_From_Break_Success;
            } else {
                result = DownloadStatus.Download_From_Break_Failed;
            }
        } else {
            OutputStream out = new FileOutputStream(f);
            InputStream in = ftpClient.retrieveFileStream(new String(StringUtils.substringAfterLast(remote,"/").getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1));
//            StreamUtils.copy(in,out);

            byte[] bytes = new byte[1024];
            long step = lRemoteSize / 100;
            long process = 0;
            long localSize = 0L;
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
                long nowProcess = localSize / step;
                if (nowProcess > process) {
                    process = nowProcess;
                    if (process % 10 == 0)
                        logger.info("下载进度：" + process);
                    //TODO 更新文件下载进度,值存放在process变量中
                }
            }
            in.close();
            out.close();
            boolean upNewStatus = ftpClient.completePendingCommand();
            if (upNewStatus) {
                result = DownloadStatus.Download_New_Success;
            } else {
                result = DownloadStatus.Download_New_Failed;
            }
        }
        return result;
    }

    /** */
    /**
     * 上传文件到FTP服务器，支持断点续传
     *
     * @param local  本地文件名称，绝对路径
     * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext或是 http://www.guihua.org /subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
     * @return 上传结果
     * @throws IOException
     */
    public UploadStatus upload(String local, String remote) throws IOException {
        //设置PassiveMode传输
        ftpClient.enterLocalPassiveMode();
        //设置以二进制流的方式传输 
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setControlEncoding(CHARSET_UTF_8);
        ftpClient.changeWorkingDirectory(baseWorkDirectory);
        UploadStatus result;
        //对远程目录的处理 
        String remoteFileName = remote;
        if (remote.contains("/")) {
            remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
            //创建服务器远程目录结构，创建失败直接返回 
            if (createDirecroty(remote, ftpClient) == UploadStatus.Create_Directory_Fail) {
                return UploadStatus.Create_Directory_Fail;
            }
        }

        //检查远程是否存在文件 
        FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1));
        if (files.length == 1) {
            long remoteSize = files[0].getSize();
            File f = new File(local);
            long localSize = f.length();
            if (remoteSize == localSize) {
                return UploadStatus.File_Exits;
            } else if (remoteSize > localSize) {
                return UploadStatus.Remote_Bigger_Local;
            }

            //尝试移动文件内读取指针,实现断点续传 
            result = uploadFile(remoteFileName, f, ftpClient, remoteSize);

            //如果断点续传没有成功，则删除服务器上文件，重新上传 
            if (result == UploadStatus.Upload_From_Break_Failed) {
                if (!ftpClient.deleteFile(remoteFileName)) {
                    return UploadStatus.Delete_Remote_Faild;
                }
                result = uploadFile(remoteFileName, f, ftpClient, 0);
            }
        } else {
            result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
        }
        return result;
    }

    public UploadStatus upload(InputStream inputStream, String remote) throws IOException {
        //设置PassiveMode传输
        ftpClient.enterLocalPassiveMode();
        //设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setControlEncoding(CHARSET_UTF_8);
        ftpClient.changeWorkingDirectory(baseWorkDirectory);
        UploadStatus result;
        //对远程目录的处理
        String remoteFileName = remote;
        if (remote.contains("/")) {
            remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
            //创建服务器远程目录结构，创建失败直接返回
            if (createDirecroty(remote, ftpClient) == UploadStatus.Create_Directory_Fail) {
                return UploadStatus.Create_Directory_Fail;
            }
        }

        //检查远程是否存在文件
        FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1));
        if (files.length == 1) {
            long remoteSize = files[0].getSize();
            long localSize = inputStream.available();
            if (remoteSize == localSize) {
                return UploadStatus.File_Exits;
            } else if (remoteSize > localSize) {
                return UploadStatus.Remote_Bigger_Local;
            }

            //尝试移动文件内读取指针,实现断点续传
            result = uploadFile(remoteFileName, inputStream, ftpClient, remoteSize);

            //如果断点续传没有成功，则删除服务器上文件，重新上传
            if (result == UploadStatus.Upload_From_Break_Failed) {
                if (!ftpClient.deleteFile(remoteFileName)) {
                    return UploadStatus.Delete_Remote_Faild;
                }
                result = uploadFile(remoteFileName, inputStream, ftpClient, 0);
            }
        } else {
            result = uploadFile(remoteFileName, inputStream, ftpClient, 0);
        }
        return result;
    }


    /**
     * 从FTP服务器上删除文件
     *
     * @param remote 远程文件路径
     * @return 上传的状态
     * @throws IOException
     */
    public UploadStatus delete(String remote) throws IOException {
        //设置被动模式
        ftpClient.enterLocalPassiveMode();
        //设置以二进制方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(baseWorkDirectory);
        ftpClient.changeWorkingDirectory(remote.substring(0, remote.lastIndexOf("/") + 1));
        DownloadStatus result;
        //检查远程文件是否存在
        FTPFile[] files = ftpClient.listFiles(new String(StringUtils.substringAfterLast(remote,"/").getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1));
        if (files.length != 1) {
            logger.error("远程文件不存在");
            return UploadStatus.Delete_Remote_Faild;
        }

        if (files.length >0 && files[0].isFile()){
            ftpClient.deleteFile(new String(files[0].getName().getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1));
        }


        return UploadStatus.Delete_Remote_Faild;
    }

    /** */
    /**
     * 断开与远程服务器的连接
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }

    /** */
    /**
     * 递归创建远程服务器目录
     *
     * @param remote    远程服务器文件绝对路径
     * @param ftpClient FTPClient对象
     * @return 目录创建是否成功
     * @throws IOException
     */
    public UploadStatus createDirecroty(String remote, FTPClient ftpClient) throws IOException {
        UploadStatus status = UploadStatus.Create_Directory_Success;
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        if (!directory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(new String(directory.getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1))) {
            //如果远程目录不存在，则递归创建远程服务器目录 
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1);
                if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                    if (ftpClient.makeDirectory(subDirectory)) {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else {
                        logger.error("创建目录失败");
                        return UploadStatus.Create_Directory_Fail;
                    }
                }

                start = end + 1;
                end = directory.indexOf("/", start);

                //检查所有目录是否创建完毕 
                if (end <= start) {
                    break;
                }
            }
        }
        return status;
    }

    /** */
    /**
     * 上传文件到服务器,新上传和断点续传
     *
     * @param remoteFile  远程文件名，在上传之前已经将服务器工作目录做了改变
     * @param localFile   本地文件File句柄，绝对路径
     * @param ftpClient   FTPClient引用
     * @param remoteSize 远端文件大小
     * @return
     * @throws IOException
     */
    public UploadStatus uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize) throws IOException {
        UploadStatus status;
        //显示进度的上传 
        long step = localFile.length() / 100;
        long process = 0;
        long localreadbytes = 0L;
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1));

        //断点续传
        if (remoteSize > 0) {
            ftpClient.setRestartOffset(remoteSize);
            process = remoteSize / step;
            raf.seek(remoteSize);
            localreadbytes = remoteSize;
        }
        byte[] bytes = new byte[1024];
        int c;
        while ((c = raf.read(bytes)) != -1) {
            out.write(bytes, 0, c);
//            localreadbytes += c;
//            if (localreadbytes / step != process) {
//                process = localreadbytes / step;
//                System.out.println("上传进度:" + process);
//                //TODO 汇报上传状态
//            }
        }
        out.flush();
        raf.close();
        out.close();
        boolean result = ftpClient.completePendingCommand();
        if (remoteSize > 0) {
            status = result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed;
        } else {
            status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
        }
        return status;
    }

    /**
     * 上传文件到服务器,新上传和断点续传
     *
     * @param remoteFile  远程文件名，在上传之前已经将服务器工作目录做了改变
     * @param inputStream   inputStream
     * @param ftpClient   FTPClient引用
     * @param remoteSize 远端文件大小
     * @return
     * @throws IOException
     */
    public UploadStatus uploadFile(String remoteFile, InputStream inputStream, FTPClient ftpClient, long remoteSize) throws IOException {
        UploadStatus status;
        //显示进度的上传
        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes(CHARSET_UTF_8), CHARSET_ISO8859_1));
        FileCopyUtils.copy(inputStream,out);
        boolean result = ftpClient.completePendingCommand();
        if (remoteSize > 0) {
            status = result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed;
        } else {
            status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
        }
        return status;
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


    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    public long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }

    public static void main(String[] args) {
    }

    @Override
    public void init() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                boolean init = false;
                isconnect = false;
                while (!init || (!isconnect && reconnect)){
                    init = true;
                    try {
                        logger.info("连接FTP服务器");
                        connect(url, port, username, password);
                        isconnect = true;
                    } catch (IOException e) {
                        isconnect = false;
                        logger.error("连接FTP服务器失败.");
                        try {
                            Thread.sleep(connectTime);
                        } catch (InterruptedException e1) {
                        }
                    }
                }

            }
        };
        thread.start();
    }




    public void destroy(){
        try {
            disconnect();
        } catch (IOException e) {
            logger.error("断开FTP服务器失败.");
        }
    }
    @Override
    public UploadStatus saveFile(String path, String inputFilename, boolean coverFile) throws IOException {
        return upload(inputFilename,path);
    }

    @Override
    public UploadStatus saveFile(String path, InputStream input, boolean coverFile) throws IOException {
        return upload(input,path);
    }


    @Override
    public DownloadStatus loadFile(String path,String localPath) throws IOException {
        return download(path,localPath);
    }

    @Override
    public UploadStatus deleteFile(String path) throws IOException{
        delete(path);
        return UploadStatus.Delete_Remote_Faild;
    }

    @Override
    public String getStorePath(Folder folder, String userId, String fileName) {
        String path = DiskUtils.getFTPStoreDir(folder, userId);
        String code = FileUploadUtils.encodingFilenamePrefix(userId.toString(),
                fileName);
        return path + "/" + code+"_"+ fileName;
    }
}