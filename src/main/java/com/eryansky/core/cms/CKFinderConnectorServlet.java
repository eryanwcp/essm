/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.core.cms;

import com.ckfinder.connector.ConnectorServlet;
import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.utils.AppConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CKFinderConnectorServlet
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-01-15
 */
public class CKFinderConnectorServlet extends ConnectorServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		prepareGetResponse(request, response, false);
		super.doGet(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		prepareGetResponse(request, response, true);
		super.doPost(request, response);
	}
	
	private void prepareGetResponse(final HttpServletRequest request,
			final HttpServletResponse response, final boolean post) throws ServletException {
		String command = request.getParameter("command");
		String type = request.getParameter("type");
		// 初始化时，如果startupPath文件夹不存在，则自动创建startupPath文件夹
		if ("Init".equals(command)){
			SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
			if (sessionInfo!=null){
				String startupPath = request.getParameter("startupPath");// 当前文件夹可指定为模块名
				if (startupPath!=null){
					String[] ss = startupPath.split(":");
					if (ss.length==2){
//						String path = CKFinderConfig.CK_BASH_URL+user.getId()+"/"+ss[0]+ss[1];
//						String realPath = request.getSession().getServletContext().getRealPath(path);
//                        FileUtils.createDirectory(realPath);

						String realPath = AppConstants.getCkBaseDir()+CKFinderConfig.CK_BASH_URL+sessionInfo.getUserId()+"/"+ss[0]+ss[1];
						FileUtils.createDirectory(realPath);
					}
				}
			}
		}
		// 快捷上传，自动创建当前文件夹，并上传到该路径
		else if ("QuickUpload".equals(command) && type!=null){
            User user = SecurityUtils.getCurrentUser();
			if (user!=null){
				String currentFolder = request.getParameter("currentFolder");// 当前文件夹可指定为模块名
//				String path = CKFinderConfig.CK_BASH_URL+user.getId()+"/"+type+(currentFolder!=null?currentFolder:"");
//				String realPath = request.getSession().getServletContext().getRealPath(path);
//                FileUtils.createDirectory(realPath);

				String realPath = AppConstants.getCkBaseDir()+CKFinderConfig.CK_BASH_URL+user.getId()+"/"+type+(currentFolder!=null?currentFolder:"");
				FileUtils.createDirectory(realPath);
			}
		}
//		System.out.println("------------------------");
//		for (Object key : request.getParameterMap().keySet()){
//			System.out.println(key + ": " + request.getParameter(key.toString()));
//		}
	}
	
}
