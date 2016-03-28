/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.servlet;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.eryansky.common.web.utils.WebUtils;


import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 本地静态内容展示与下载的Servlet.
 * <p/>
 * 使用EhCache缓存静态内容基本信息, 演示文件高效读取,客户端缓存控制及Gzip压缩传输.
 * <p/>
 * 演示访问地址为：
 * static-content?contentPath=img/logo.jpg
 * static-content?contentPath=img/logo.jpg&download=true   下载
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class StaticContentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** 需要被Gzip压缩的Mime类型. */
	private static final String[] GZIP_MIME_TYPES = { "text/html", "application/xhtml+xml", "text/plain", "text/css",
			"text/javascript", "application/x-javascript", "application/json" , "application/javascript"};

	/** 需要被Gzip压缩的最小文件大小. */
	private static final int GZIP_MINI_LENGTH = 512;

	private MimetypesFileTypeMap mimetypesFileTypeMap;

    /**
     * Content基本信息缓存.
     */
    private Cache contentInfoCache;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        String cacheManager = config.getInitParameter("CacheManager");
        CacheManager ehcacheManager = (CacheManager) context.getBean(cacheManager);
        String cacheKey = config.getInitParameter("cacheKey");
        contentInfoCache = ehcacheManager.getCache(cacheKey);

        //初始化mimeTypes, 默认缺少css的定义,添加之.
        mimetypesFileTypeMap = new MimetypesFileTypeMap();
        mimetypesFileTypeMap.addMimeTypes("text/css css");
    }


    /**
     * 在初始化函数中创建内容信息缓存.
     */
    @Override
    public void init() throws ServletException {

    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取请求内容的基本信息.
        String contentPath = request.getParameter("contentPath");
        ContentInfo contentInfo = getContentInfoFromCache(contentPath);

        //根据Etag或ModifiedSince Header判断客户端的缓存文件是否有效, 如仍有效则设置返回码为304,直接返回.
        if (!WebUtils.checkIfModifiedSince(request, response, contentInfo.lastModified)
                || !WebUtils.checkIfNoneMatchEtag(request, response, contentInfo.etag)) {
            return;
        }

        //设置Etag/过期时间
        WebUtils.setExpiresHeader(response, WebUtils.ONE_YEAR_SECONDS);
        WebUtils.setLastModifiedHeader(response, contentInfo.lastModified);
        WebUtils.setEtag(response, contentInfo.etag);

        //设置MIME类型
        response.setContentType(contentInfo.mimeType);

        //如果是下载请求,设置下载Header
        if (request.getParameter("download") != null) {
            WebUtils.setDownloadableHeader(response, contentInfo.fileName);
        }

        //构造OutputStream
        OutputStream output;
        if (WebUtils.checkAccetptGzip(request) && contentInfo.needGzip) {
            //使用压缩传输的outputstream, 使用http1.1 trunked编码不设置content-length.
            output = WebUtils.buildGzipOutputStream(response);
        } else {
            //使用普通outputstream, 设置content-length.
            response.setContentLength(contentInfo.length);
            output = response.getOutputStream();
        }

        //高效读取文件内容并输出.
        FileInputStream input = new FileInputStream(contentInfo.file);
        try {
            //基于byte数组读取文件并直接写入OutputStream, 数组默认大小为4k.
            IOUtils.copy(input, output);
            output.flush();
        } finally {
            //保证Input/Output Stream的关闭.
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * 从缓存中获取Content基本信息, 如不存在则进行创建.
     */
    private ContentInfo getContentInfoFromCache(String path) {
        Element element = contentInfoCache.get(path);
        if (element == null) {
            ContentInfo content = createContentInfo(path);
            element = new Element(content.contentPath, content);
            contentInfoCache.put(element);
        }
        return (ContentInfo) element.getObjectValue();
    }

    /**
     * 创建Content基本信息.
     */
    private ContentInfo createContentInfo(String contentPath) {
        ContentInfo contentInfo = new ContentInfo();

        String realFilePath = getServletContext().getRealPath(contentPath);
        File file = new File(realFilePath);

        contentInfo.contentPath = contentPath;
        contentInfo.file = file;
        contentInfo.fileName = file.getName();
        contentInfo.length = (int) file.length();

        contentInfo.lastModified = file.lastModified();
        contentInfo.etag = "W/\"" + contentInfo.lastModified + "\"";

        String mimeType = getServletContext().getMimeType(realFilePath);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        contentInfo.mimeType = mimeType;

        if (contentInfo.length >= GZIP_MINI_LENGTH && ArrayUtils.contains(GZIP_MIME_TYPES, contentInfo.mimeType)) {
            contentInfo.needGzip = true;
        } else {
            contentInfo.needGzip = false;
        }

        return contentInfo;
    }

    /**
     * 定义Content的基本信息.
     */
    static class ContentInfo {
    	protected String contentPath;
    	protected File file;
    	protected String fileName;
    	protected int length;
    	protected String mimeType;
    	protected long lastModified;
    	protected String etag;
    	protected boolean needGzip;
    }
}

