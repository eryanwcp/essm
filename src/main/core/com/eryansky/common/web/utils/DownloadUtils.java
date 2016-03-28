/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package com.eryansky.common.web.utils;

import com.eryansky.common.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 文件下载工具类
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-05-05 22:50
 */
public class DownloadUtils {

    /**
     * 下载文件
     * @param request
     * @param response
     * @param filePath 文件路径
     * @throws IOException
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, String filePath) throws IOException {
        download(request, response, filePath, "");
    }

    /**
     * 下载文件
     * @param request
     * @param response
     * @param filePath 文件路径
     * @param displayName 下载显示的文件名
     * @throws IOException
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, String filePath, String displayName) throws IOException {
        File file = new File(filePath);
        if(StringUtils.isEmpty(displayName)) {
            displayName = file.getName();
        }
        if (!file.exists() || !file.canRead()) {
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("您下载的文件不存在！");
            return;
        }

        download(request,response, new FileInputStream(file),displayName);
    }

    /**
     * 下载文件
     * @param request
     * @param response
     * @param displayName 下载显示的文件名
     * @param bytes 文件字节
     * @throws IOException
     */
    public static void download(HttpServletRequest request, HttpServletResponse response, byte[] bytes, String displayName) throws IOException {
        if (ArrayUtils.isEmpty(bytes)) {
            response.setContentType("text/html;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("您下载的文件不存在！");
            return;
        }
        download(request,response,new ByteArrayInputStream(bytes),displayName);
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

        response.setContentType("application/x-download");
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