/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.web;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.security.LogUtils;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.utils.AppConstants;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  KindEditor编辑器文件上传以及管理
 *  @see FileKindEditorController
 */
@Deprecated
@Controller
@RequestMapping(value = "${adminPath}/disk/kindeditor")
public class KindEditorController extends SimpleController{

    //图片mime类型
    private static final String[] IMAGE_EXTENSION = FileUploadUtils.IMAGE_EXTENSION;

    //附件mime类型
    private static final String[] ATTACHMENT_EXTENSION = FileUploadUtils.DEFAULT_ALLOWED_EXTENSION;

    //flash mime类型
    private static final String[] FLASH_EXTENSION = FileUploadUtils.FLASH_EXTENSION;

    //swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb
    private static final String[] MEDIA_EXTENSION = FileUploadUtils.MEDIA_EXTENSION;

    //最大上传大小 字节为单位
    private long maxSize = FileUploadUtils.DEFAULT_MAX_SIZE;
    //文件上传下载的父目录
    private String baseDir = FileUploadUtils.getDefaultBaseDir();


    @Autowired
    private MessageSource messageSource;


    /**
     * 传入的数据
     * dir 表示类型  file image flash media
     * <p/>
     * 返回的数据格式
     * 出错时
     * {
     * error : 1
     * message : 出错时的消息
     * }
     * <p/>
     * 正确时
     * {
     * error:0
     * url:上传后的地址
     * title:标题
     * }
     *
     * @param response
     * @param request
     * @param dir
     * @param file
     * @return
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> upload(
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestParam(value = "dir", required = false) String dir,
            @RequestParam(value = "imgFile", required = false) MultipartFile file) {
//        response.setContentType("text/html; charset=UTF-8");

        String[] allowedExtension = extractAllowedExtension(dir);

        try {
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            String basePath = DiskUtils.getKindEditorRelativePath(sessionInfo.getUserId());
            basePath +=  File.separator + FileUploadUtils.datePath();//添加日期目录
            String url = FileUploadUtils.upload(request, AppConstants.getDiskBaseDir() + File.separator + basePath, file, allowedExtension, maxSize, false, null);
            return successResponse(request, file.getOriginalFilename(), url);
        } catch (IOException e) {
            LogUtils.logError("file upload error", e);
            return errorResponse("服务器故障，请稍后重试！");
        } catch (InvalidExtensionException.InvalidImageExtensionException e) {
            return errorResponse("上传的图片类型不允许！<br/>允许的图片类型包括：图片("+ JsonMapper.getInstance().toJson(IMAGE_EXTENSION)+")格式。");
        } catch (InvalidExtensionException.InvalidFlashExtensionException e) {
            return errorResponse("上传的flash类型不允许！<br/>允许的flash类型包括："+JsonMapper.getInstance().toJson(FLASH_EXTENSION)+"格式。");
        } catch (InvalidExtensionException.InvalidMediaExtensionException e) {
            return errorResponse("上传的媒体类型不允许！<br/>允许的媒体类型包括："+JsonMapper.getInstance().toJson(MEDIA_EXTENSION)+"格式。");
        } catch (InvalidExtensionException e) {
            return errorResponse("上传的文件类型不允许！允许的类型，请参考页面提示。");
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            return errorResponse("上传的文件大小超出限制的文件大小！<br/>允许的文件最大大小是："+e.getPermittedSize()+"！");
        } catch (FileNameLengthLimitExceededException e) {
            return errorResponse("上传的文件名最长"+e.getLength()+"个字符");
        }

    }

    /**
     * 传入的数据
     * path 当前访问的目录（相对路径） 【..】需要排除掉
     * order 排序  NAME SIZE TYPE
     * dir 文件类型 file image flash media
     * <p/>
     * <p/>
     * 返回的数据格式
     * 出错时（前台无错误提示，todo 可以自己改造下 显示错误信息，并停留在当前页面）
     * "字符串"
     * <p/>
     * 正确时
     * {"current_url":当前地址（绝对）,
     * "current_dir_path":当前目录（相对）,
     * "moveup_dir_path":上级目录（可以根据当前目录算出来）,
     * "file_list":[//文件列表
     * 文件名                  文件大小     文件类型      是否包含文件    是否是目录   是否是照片        时间
     * {"filename":"My Pictures","filesize":0,"filetype":"","has_file":true,"is_dir":true,"is_photo":false,"datetime":"2013-03-09 11:41:17"}
     * ],
     * "total_count":文件及目录总数
     * }
     *
     * @return
     */
    @RequestMapping(value = "filemanager", method = RequestMethod.GET)
    @ResponseBody
    public Object fileManager(
            HttpServletRequest request,
            @RequestParam(value = "order", required = false, defaultValue = "NAME") String order,
            @RequestParam(value = "dir", required = false, defaultValue = "file") String dir,
            @RequestParam(value = "path", required = false, defaultValue = "") String currentDirPath) {

        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String basePath = DiskUtils.getKindEditorRelativePath(sessionInfo.getUserId());
        String diskBaseDir = AppConstants.getDiskBaseDir();
        //根目录路径
        String rootPath = FileUploadUtils.extractUploadDir(request) + diskBaseDir + File.separator +  basePath + File.separator ;
        String rootUrl = request.getContextPath()+ "/"+ diskBaseDir + "/" + basePath.replace("\\","/");

        //上一级目录
        String moveupDirPath = "";
        if (!"".equals(currentDirPath)) {
            String str = currentDirPath.substring(0, currentDirPath.length() - 1);
            moveupDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : "";
        }

        //不允许使用..移动到上一级目录
        if (currentDirPath.indexOf("..") >= 0) {
            return "访问拒绝，目录中不能出现..";
        }
        //最后一个字符不是/
        if (!"".equals(currentDirPath) && !currentDirPath.endsWith("/")) {
            return "参数不合法，目录需要以 / 结尾";
        }
        //目录不存在或不是目录
        File currentPathFile = new File(rootPath + File.separator + currentDirPath);
        if (!currentPathFile.exists() || !currentPathFile.isDirectory()) {
            String errorDir = "目录不存在！";
            logger.warn(errorDir);
//            return errorDir;
        }

        //遍历目录取的文件信息
        List<Map<String, Object>> fileMetaInfoList = Lists.newArrayList();

        List<String> allowedExtension = Arrays.asList(extractAllowedExtension(dir));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (currentPathFile.listFiles() != null) {
            for (File file : currentPathFile.listFiles()) {

                Map<String, Object> fileMetaInfo = Maps.newHashMap();
                String fileName = file.getName();

                if (file.isDirectory()) {
                    fileMetaInfo.put("is_dir", true);
                    fileMetaInfo.put("has_file", (file.listFiles() != null));
                    fileMetaInfo.put("filesize", 0L);
                    fileMetaInfo.put("is_photo", false);
                    fileMetaInfo.put("filetype", "");
                } else if (file.isFile()) {
                    String fileExt = FilenameUtils.getExtension(fileName);
                    if (!allowedExtension.contains(FilenameUtils.getExtension(fileName))) {
                        continue;
                    }
                    fileMetaInfo.put("is_dir", false);
                    fileMetaInfo.put("has_file", false);
                    fileMetaInfo.put("filesize", file.length());
                    fileMetaInfo.put("is_photo", Arrays.<String>asList(FileUploadUtils.IMAGE_EXTENSION).contains(fileExt));
                    fileMetaInfo.put("filetype", fileExt);
                }
                fileMetaInfo.put("filename", fileName);
                fileMetaInfo.put("datetime", df.format(file.lastModified()));

                fileMetaInfoList.add(fileMetaInfo);
            }
        }

        if ("size".equalsIgnoreCase(order)) {
            Collections.sort(fileMetaInfoList, new SizeComparator());
        } else if ("type".equalsIgnoreCase(order)) {
            Collections.sort(fileMetaInfoList, new TypeComparator());
        } else {
            Collections.sort(fileMetaInfoList, new NameComparator());
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("moveup_dir_path", moveupDirPath);
        result.put("current_dir_path", currentDirPath);
        result.put("current_url", rootUrl + "/" + currentDirPath);
        result.put("total_count", fileMetaInfoList.size());
        result.put("file_list", fileMetaInfoList);
        return result;
    }


    private Map<String,Object> successResponse(HttpServletRequest request, String filename, String url) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("error",0);
        map.put("url",request.getContextPath() + "/" + url);
        map.put("title", filename);
        return map;
    }

    private Map<String,Object> errorResponse(String errorCode) {
//        String message = messageSource.getMessage(errorCode, null, null);
        String message = errorCode;
        if (message.contains("<br/>")) {
            message = message.replace("<br/>", "\\n");
        }
        Map<String,Object> map = Maps.newHashMap();
        map.put("error",1);
        map.put("message",message);
        System.out.println(map);
        return map;
    }

    private String[] extractAllowedExtension(String dir) {
        if ("image".equals(dir)) {
            return IMAGE_EXTENSION;
        } else if ("flash".equals(dir)) {
            return FLASH_EXTENSION;
        } else if ("media".equals(dir)) {
            return MEDIA_EXTENSION;
        } else {
            return ATTACHMENT_EXTENSION;
        }

    }


    public class NameComparator implements Comparator {
        public int compare(Object a, Object b) {
            Map mapA = (Map) a;
            Map mapB = (Map) b;
            if (((Boolean) mapA.get("is_dir")) && !((Boolean) mapB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) mapA.get("is_dir")) && ((Boolean) mapB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) mapA.get("filename")).compareTo((String) mapB.get("filename"));
            }
        }
    }

    public class SizeComparator implements Comparator {
        public int compare(Object a, Object b) {
            Map mapA = (Map) a;
            Map mapB = (Map) b;
            if (((Boolean) mapA.get("is_dir")) && !((Boolean) mapB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) mapA.get("is_dir")) && ((Boolean) mapB.get("is_dir"))) {
                return 1;
            } else {
                if (((Long) mapA.get("filesize")) > ((Long) mapB.get("filesize"))) {
                    return 1;
                } else if (((Long) mapA.get("filesize")) < ((Long) mapB.get("filesize"))) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    public class TypeComparator implements Comparator {
        public int compare(Object a, Object b) {
            Map mapA = (Map) a;
            Map mapB = (Map) b;
            if (((Boolean) mapA.get("is_dir")) && !((Boolean) mapB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) mapA.get("is_dir")) && ((Boolean) mapB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) mapA.get("filetype")).compareTo((String) mapB.get("filetype"));
            }
        }
    }


}