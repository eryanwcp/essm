/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.web.upload;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.encode.MD5Util;
import com.eryansky.core.security.LogUtils;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.utils.AppConstants;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 文件上传工具类
 * <p>Date: 2014-5-5 下午8:32
 * <p>Version: 1.0
 */
public class FileUploadUtils {

    //默认大小 50M
    public static final long DEFAULT_MAX_SIZE = 52428800;

    //默认上传的地址
    private static String defaultBaseDir = "disk";

    //默认的文件名最大长度
    public static final int DEFAULT_FILE_NAME_LENGTH = 200;

    public static final String[] IMAGE_EXTENSION = {
            "bmp", "gif", "jpg", "jpeg", "png"
    };

    public static final String[] FLASH_EXTENSION = {
            "swf", "flv"
    };

    public static final String[] MEDIA_EXTENSION = {
            "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb"
    };

    public static final String[] DEFAULT_ALLOWED_EXTENSION = {
            //图片
            "bmp", "gif", "jpg", "jpeg", "png",
            //word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "html", "htm", "txt",
            //压缩文件
            "rar", "zip", "gz", "bz2",
            //pdf
            "pdf"
    };


    private static int counter = 0;


    public static void setDefaultBaseDir(String defaultBaseDir) {
        FileUploadUtils.defaultBaseDir = defaultBaseDir;
    }

    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param request 当前请求
     * @param file    上传的文件
     * @param result  添加出错信息
     * @return
     */
    public static final String upload(HttpServletRequest request, MultipartFile file, BindingResult result) {
        return upload(request, file, result, DEFAULT_ALLOWED_EXTENSION);
    }


    /**
     * 以默认配置进行文件上传
     *
     * @param request          当前请求
     * @param file             上传的文件
     * @param result           添加出错信息
     * @param allowedExtension 允许上传的文件类型
     * @return
     */
    public static final String upload(HttpServletRequest request, MultipartFile file, BindingResult result, String[] allowedExtension) {
        try {
            return upload(request, getDefaultBaseDir(), file, allowedExtension, DEFAULT_MAX_SIZE, true,null);
        } catch (IOException e) {
            LogUtils.logError("file upload error", e);
            result.reject("upload.server.error");
        } catch (InvalidExtensionException.InvalidImageExtensionException e) {
            result.reject("upload.not.allow.image.extension");
        } catch (InvalidExtensionException.InvalidFlashExtensionException e) {
            result.reject("upload.not.allow.flash.extension");
        } catch (InvalidExtensionException.InvalidMediaExtensionException e) {
            result.reject("upload.not.allow.media.extension");
        } catch (InvalidExtensionException e) {
            result.reject("upload.not.allow.extension");
        } catch (FileSizeLimitExceededException e) {
            result.reject("upload.exceed.maxSize");
        } catch (FileNameLengthLimitExceededException e) {
            result.reject("upload.filename.exceed.length");
        }
        return null;
    }


    /**
     * 文件上传
     *
     * @param request                   当前请求 从请求中提取 应用上下文根
     * @param dir                   当request不为空,入参为相对应用的基目录;当为空时,入参为除配置路径外的文件夹相对路径
     * @param file                      上传的文件
     * @param allowedExtension          允许的文件类型 null 表示允许所有
     * @param maxSize                   最大上传的大小 -1 表示不限制
     * @param needDatePathAndRandomName 是否需要日期目录和随机文件名前缀
     * @param _prefix 文件名前缀 建议在needDatePathAndRandomName为false时使用
     * @return 返回上传成功的文件名
     * @throws com.eryansky.core.web.upload.exception.InvalidExtensionException            如果MIME类型不允许
     * @throws org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException       如果超出最大大小
     * @throws com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException 文件名太长
     * @throws java.io.IOException                          比如读写文件出错时
     */
    public static final String upload(HttpServletRequest request, String dir,
                                      MultipartFile file, String[] allowedExtension, long maxSize,
                                      boolean needDatePathAndRandomName, String _prefix)
            throws InvalidExtensionException, FileSizeLimitExceededException,
            IOException, FileNameLengthLimitExceededException {

        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(
                    file.getOriginalFilename(), fileNamelength,
                    FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }
        File desc = null;
        String filename = null;
        assertAllowed(file, allowedExtension, maxSize);
        if (request != null) {
            filename = extractFilename(file, dir, needDatePathAndRandomName,
                    _prefix);
            desc = getAbsoluteFile(extractUploadDir(request), filename);
        } else {
            filename = extractFilename(file, dir, needDatePathAndRandomName,
                    _prefix);
            String fileBasePath = getBasePath(filename);
            desc = getAbsoluteFile(fileBasePath);
        }

        file.transferTo(desc);
        return filename;
    }


    public static final String upload(HttpServletRequest request, String dir,
                                      File file, String[] allowedExtension, long maxSize,
                                      boolean needDatePathAndRandomName, String _prefix)
            throws InvalidExtensionException, FileSizeLimitExceededException,
            IOException, FileNameLengthLimitExceededException {

        int fileNamelength = file.getName().length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(
                    file.getName(), fileNamelength,
                    FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }
        File desc = null;
        String filename = null;
        assertAllowed(file, allowedExtension, maxSize);
        if (request != null) {
            filename = extractFilename(file, dir, needDatePathAndRandomName,
                    _prefix);
            desc = getAbsoluteFile(extractUploadDir(request), filename);
        } else {
            filename = extractFilename(file, dir, needDatePathAndRandomName,
                    _prefix);
            String fileBasePath = getBasePath(filename);
            desc = getAbsoluteFile(fileBasePath);
        }

        if(!file.isDirectory()){
            FileUtils.copyFile(file,desc);
        }
        return filename;
    }


    public static final void upload(String path,String local)
            throws IOException{
        FileUtils.copyFile(new File(local),new File(path));
    }

    public static void upload(String path,InputStream inputStream)
            throws IOException{
        FileUtils.copyInputStreamToFile(inputStream, new File(path));
    }

    /**
     * 根据相对路径创建绝对路径
     *
     * @param relativePath
     *            相对路径
     * @return
     */
    public static String getBasePath(String relativePath) {
        StringBuffer path = new StringBuffer();
        if (StringUtils.isNotBlank(relativePath)) {
            path.append(AppConstants.getDiskBasePath())
                    .append(File.separator).append(relativePath);
        }

        return path.toString();
    }

    /**
     * 获取文件的据对路径
     * @param uploadDir 相对应用的基目录
     * @param filename 文件名
     * @return
     * @throws java.io.IOException
     */
    private static final File getAbsoluteFile(String uploadDir, String filename) throws IOException {

        uploadDir = FilenameUtils.normalizeNoEndSeparator(uploadDir);

        File desc = new File(uploadDir + File.separator + filename);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    public static final File getAbsoluteFile(String fileName) throws IOException {
        File desc = new File(fileName);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }



    /**
     * 获取文件绝对路径
     * @param request HTTP请求对象
     * @param filename 文件名
     * @return
     * @throws java.io.IOException
     */
    public static final File getAbsoluteFile(HttpServletRequest request,String filename) throws IOException{
        return  getAbsoluteFile(extractUploadDir(request), filename);
    }

    /**
     * 提取文件名
     * @param file
     * @param baseDir
     * @param needDatePathAndRandomName
     * @param _prefix
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public static final String extractFilename(MultipartFile file,
                                               String baseDir, boolean needDatePathAndRandomName, String _prefix)
            throws UnsupportedEncodingException {
        String fileAllName = file.getOriginalFilename();

        return extractFilename(fileAllName, baseDir, needDatePathAndRandomName,
                _prefix);
    }

    public static final String extractFilename(File file,
                                               String baseDir, boolean needDatePathAndRandomName, String _prefix)
            throws UnsupportedEncodingException {
        String fileAllName = file.getName();

        return extractFilename(fileAllName, baseDir, needDatePathAndRandomName,
                _prefix);
    }



    public static final String extractFilename(String fileAllName,
                                               String baseDir, boolean needDatePathAndRandomName, String _prefix)
            throws UnsupportedEncodingException {
        int slashIndex = fileAllName.indexOf("/");
        if (slashIndex >= 0) {
            fileAllName = fileAllName.substring(slashIndex + 1);
        }
        if (StringUtils.isNotBlank(_prefix)) {
            fileAllName = _prefix + "_" + fileAllName;
        }
        if (needDatePathAndRandomName) {
            fileAllName = baseDir + File.separator + FileUploadUtils.datePath()
                    + File.separator
                    + FileUploadUtils.encodingFilename(fileAllName);
        } else {
            fileAllName = baseDir + File.separator + fileAllName;
        }

        return fileAllName;
    }

    /**
     * 编码文件名,默认格式为：
     * 1、'_'替换为 ''
     * 2、hex(md5(filename + now nano time + counter++))
     * 3、[2]_原始文件名
     *
     * @param filename 原始文件名
     * @return
     */
    public static final String encodingFilename(String filename) {
        filename = encodingFilenamePrefix("",filename) + "_" + filename;
        return filename;
    }

    /**
     * 生成文件名前缀
     * 1、'_'替换为 ''
     * 2、hex(md5(filename + now nano time + counter++))
     * 3、[2]_
     * @param filename 原始文件名
     * @return
     */
    public static final String encodingFilenamePrefix(String userId,String filename) {
        filename = filename.replace("_", "");
        filename = userId + "_" + MD5Util.hash(filename + System.nanoTime() + counter++) ;
        return filename;
    }

    /**
     * 日期路径 即年/月/日  如2013/01/03
     *
     * @return
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "MM");
    }


    /**
     * 是否允许文件上传
     *
     * @param file             上传的文件
     * @param allowedExtension 文件类型  null 表示允许所有
     * @param maxSize          最大大小 字节为单位 -1表示不限制
     * @return
     * @throws com.eryansky.core.web.upload.exception.InvalidExtensionException      如果MIME类型不允许
     * @throws org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException 如果超出最大大小
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension, long maxSize)
            throws InvalidExtensionException, FileSizeLimitExceededException {

        String filename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension, filename);
            } else if (allowedExtension == FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension, filename);
            } else if (allowedExtension == MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension, filename);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, filename);
            }
        }

        long size = file.getSize();
        if (maxSize != -1 && size > maxSize) {
            throw new FileSizeLimitExceededException("not allowed upload upload", size, maxSize);
        }
    }


    public static final void assertAllowed(File file, String[] allowedExtension, long maxSize)
            throws InvalidExtensionException, FileSizeLimitExceededException {

        String filename = file.getName();
        String extension = FilenameUtils.getExtension(file.getName());

        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension, filename);
            } else if (allowedExtension == FLASH_EXTENSION) {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension, filename);
            } else if (allowedExtension == MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension, filename);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, filename);
            }
        }

        long size = file.length();
        if (maxSize != -1 && size > maxSize) {
            throw new FileSizeLimitExceededException("not allowed upload upload", size, maxSize);
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 提取上传的根目录 默认是应用的根
     *
     * @param request
     * @return
     */
    public static final String extractUploadDir(HttpServletRequest request) {
        return request.getSession().getServletContext().getRealPath("/");
    }


    /**
     * request 为空 入参fileName为除配置路径外的附件相对路径; 不为空则为servlet下附件的绝对路径
     *
     * @param request
     * @return
     */
    public static final void delete(HttpServletRequest request, String fileName)
            throws IOException {
        if (StringUtils.isEmpty(fileName)) {
            return;
        }
        File desc = null;
        if (request == null) {
            String fileAbsoluteName = getBasePath(fileName);
            desc = getAbsoluteFile(fileAbsoluteName);
        } else {
            desc = getAbsoluteFile(extractUploadDir(request), fileName);
        }

        if (desc.exists()) {
            desc.delete();
        }
    }
}