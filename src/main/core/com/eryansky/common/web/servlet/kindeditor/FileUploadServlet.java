/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.servlet.kindeditor;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.io.FileUtil;
import com.eryansky.common.web.utils.WebUtils;
import com.google.common.collect.Maps;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * kindeditor文件上传.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-6-2 下午3:51:43 
 * @version 1.0
 */
public class FileUploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//定义允许上传的文件扩展名
	protected HashMap<String, String> extMap = new HashMap<String, String>();
	//最大文件大小
	protected long  maxSize = 1000000;
	//上传文件的保存路径
	protected String configPath = "attached/";
	
	
	public void init() throws ServletException {
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		extMap.put("flash", "swf,flv");
		extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
		extMap.put("file", "doc,docx,xls,xlsx,pdf,ppt,htm,html,txt,zip,rar,gz,bz2"); 
	}
	
	 
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 this.doPost(request, response);
	}
 
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String dirName = request.getParameter("dir");
		if (dirName == null) {
			dirName = "image";
		}
		
		//上传的文件的保存路径
		String uploadPath =  getInitParameter("UPLOAD_PATH");
		if (StringUtils.isNotBlank(uploadPath)) {
			configPath = uploadPath;
		}
		
		if ("image".equals(dirName)) {
			
			//上传的图片大小
			Long size = Long.parseLong(getInitParameter("Img_MAX_SIZE"));
			if (size != null) {
				maxSize = size;
			}
			
			//上传图片的类型(缺省值为gif, jpg, jpeg, png, bmp)
			String type = getInitParameter("Img_YPES");
			if (StringUtils.isNotBlank(type)) {
				extMap.put("image", type);
			}
			 
		}else {
			//上传的图片大小
			Long size = Long.parseLong(getInitParameter("File_MAX_SIZE"));
			if (size != null) {
				maxSize = size;
			}
			
			if ("file".equals(dirName)) {
				//上传文件的类型(doc, xls, ppt, pdf, txt, rar, zip)
				String type = getInitParameter("File_TYPES");
				if (StringUtils.isNotBlank(type)) {
					extMap.put("file", type);
				}
			}
		}
		
		if (StringUtils.isBlank(configPath)) {
            WebUtils.renderText(response,getError("你还没设置上传文件保存的目录路径!"));
			return;
		}
		 
		//文件保存目录路径
		String savePath = this.getServletContext().getRealPath("/") + configPath;

		//文件保存目录URL
		String saveUrl  = request.getContextPath() + "/"+configPath;
  
		if(!ServletFileUpload.isMultipartContent(request)){
            WebUtils.renderText(response,getError("请选择文件。"));
			return;
		}
		//检查目录
		File uploadDir = new File(savePath);
		if(!uploadDir.isDirectory()){
			FileUtil.createDirectory(uploadDir.getPath());
//			ServletUtils.rendText(getError("上传目录不存在。"), response);
//			return;
		}
		//检查目录写权限
		if(!uploadDir.canWrite()){
            WebUtils.renderText(response,getError("传目录没有写权限。"));
			return;
		}
 
		if(!extMap.containsKey(dirName)){
            WebUtils.renderText(response,getError("目录名不正确。"));
			return;
		}
		//创建文件夹
		savePath += dirName + "/";
		saveUrl += dirName + "/";
		File saveDirFile = new File(savePath);
		if (!saveDirFile.exists()) {
			saveDirFile.mkdirs();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String ymd = sdf.format(new Date());
		savePath += ymd + "/";
		saveUrl += ymd + "/";
		File dirFile = new File(savePath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		
		try {
			List items = upload.parseRequest(request);
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				String fileName = item.getName();
				long fileSize = item.getSize();
				if (!item.isFormField()) {
					//检查文件大小
					if(item.getSize() > maxSize){
						WebUtils.renderText(response,getError("上传文件大小超过限制。"));
						return;
					}
					//检查扩展名
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
                        WebUtils.renderText(response,getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。"));
						return;
					}

					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
					try{
						File uploadedFile = new File(savePath, newFileName);
						item.write(uploadedFile);
					}catch(Exception e){
                        WebUtils.renderText(response,getError("上传文件失败。"));
						return;
					}

					Map<String,Object> obj = Maps.newHashMap();
					obj.put("error", 0);
					obj.put("url", saveUrl + newFileName);
					WebUtils.renderText(response,obj);
				}
			}
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		}
		 
	}

	private Map<String,Object> getError(String message) {
		Map<String,Object> obj = Maps.newHashMap();
		obj.put("error", 1);
		obj.put("message", message);
		return obj;
	}
}
