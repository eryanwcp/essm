/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package test.utils;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.ThreadUtils;
import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.common.utils.io.IoUtils;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.j2cache.CacheChannel;
import com.eryansky.j2cache.CacheObject;
import com.eryansky.modules.sys.utils.SystemSerialNumberUtils;
import com.eryansky.utils.AppUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地静态内容展示与下载的Servlet.
 * <p/>
 * 使用J2Cache缓存静态内容基本信息（可支持数据信息）, 演示文件高效读取,客户端缓存控制及Gzip压缩传输.
 * <p/>
 * 演示访问地址为：
 * static-content?contentPath=img/logo.jpg
 * static-content?contentPath=img/logo.jpg&download=true   下载
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
        "classpath:applicationContext-mybatis.xml",
        "classpath:applicationContext-quartz.xml",
        "classpath:applicationContext-j2cache.xml" })
public class StaticContentTest {

    private static final long serialVersionUID = 1L;

    /** 需要被Gzip压缩的Mime类型. */
	private static final String[] GZIP_MIME_TYPES = { "text/html", "application/xhtml+xml", "text/plain", "text/css",
			"text/javascript", "application/x-javascript", "application/json" , "application/javascript"};

	/** 需要被Gzip压缩的最小文件大小. */
	private static final int GZIP_MINI_LENGTH = 512;

	private String cacheKey = "contentInfoCache";
	private boolean cacheFileData = true;
	@Autowired
	private CacheChannel cacheChannel;
	private static final String  PATH = "E:\\MyProject\\GitHub\\essm\\target\\essm";
    private ExecutorService executorService = Executors.newFixedThreadPool(100);

	@Test
	public void test(){
        String contentPath = "/static/js/jquery/jquery-2.1.4.js";

        Date d1 = Calendar.getInstance().getTime();

        for(int i=0;i<10000;i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        doGet(contentPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                System.out.println("执行完毕！");
                Date d2 = Calendar.getInstance().getTime();
                System.out.println(d2.getTime() - d1.getTime());
                break;
            }
            ThreadUtils.sleep(200);
        }
    }


    protected void doGet(String contentPath) throws Exception {
        //获取请求内容的基本信息.
        ContentInfo contentInfo = getContentInfoFromCache(contentPath);


        OutputStream output = new FileOutputStream("D:\\temp\\"+ StringUtils.substringAfterLast(contentPath,"/"));
        //高效读取文件内容并输出.
        FileInputStream input = null;
        try {
            //基于byte数组读取文件并直接写入OutputStream, 数组默认大小为4k.
            if(cacheFileData && contentInfo.fileData != null && contentInfo.fileData.length >0 ){
                output.write(contentInfo.fileData);
            }else{
                input = new FileInputStream(contentInfo.file);
                IoUtils.copy(input, output);
            }
            output.flush();
        } finally {
            //保证Input/Output Stream的关闭.
            IoUtils.closeQuietly(input);
            IoUtils.closeQuietly(output);
        }
    }

    /**
     * 从缓存中获取Content基本信息, 如不存在则进行创建.
     */
    private ContentInfo getContentInfoFromCache(String path) {
        CacheObject cacheObject = cacheChannel.get(cacheKey,path);
        if (cacheObject == null || cacheObject.getValue() == null) {
            ContentInfo content = createContentInfo(path);
            cacheChannel.set(cacheKey,content.contentPath,content);
            return content;
        }
        return (ContentInfo) cacheObject.getValue();
    }

    /**
     * 创建Content基本信息.
     */
    private ContentInfo createContentInfo(String contentPath) {
        ContentInfo contentInfo = new ContentInfo();

        String realFilePath = PATH + contentPath;
        File file = new File(realFilePath);

        contentInfo.contentPath = contentPath;
        contentInfo.file = file;
        try {
            if(cacheFileData){
                contentInfo.fileData = FileUtils.readFileToByteArray(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        contentInfo.fileName = file.getName();
        contentInfo.length = (int) file.length();

        contentInfo.lastModified = file.lastModified();
        contentInfo.etag = "W/\"" + contentInfo.lastModified + "\"";

        String mimeType = "application/javascript";
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
    static class ContentInfo implements Serializable{
    	protected String contentPath;
    	protected File file;
    	protected byte[] fileData;
    	protected String fileName;
    	protected int length;
    	protected String mimeType;
    	protected long lastModified;
    	protected String etag;
    	protected boolean needGzip;
    }
}

