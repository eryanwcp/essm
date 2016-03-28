/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.core.cms;

import com.ckfinder.connector.configuration.Configuration;
import com.ckfinder.connector.data.AccessControlLevel;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.utils.AppConstants;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

/**
 * CKFinder配置
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 * @version 2013-01-15
 */
public class CKFinderConfig extends Configuration {

	public static final String CK_BASH_URL = "/userfiles/";

	public CKFinderConfig(ServletConfig servletConfig) {
        super(servletConfig);  
    }
	
	@Override
    protected Configuration createConfigurationInstance() {
		boolean isView = SecurityUtils.isPermitted("cms:ckfinder:view");
		boolean isUpload = SecurityUtils.isPermitted("cms:ckfinder:upload");
		boolean isEdit = SecurityUtils.isPermitted("cms:ckfinder:edit");
		AccessControlLevel alc = this.getAccessConrolLevels().get(0);
		alc.setFolderView(isView);
		alc.setFolderCreate(isEdit);
		alc.setFolderRename(isEdit);
		alc.setFolderDelete(isEdit);
		alc.setFileView(isView);
		alc.setFileUpload(isUpload);
		alc.setFileRename(isEdit);
		alc.setFileDelete(isEdit);
//		for (AccessControlLevel a : this.getAccessConrolLevels()){
//			System.out.println(a.getRole()+", "+a.getResourceType()+", "+a.getFolder()
//					+", "+a.isFolderView()+", "+a.isFolderCreate()+", "+a.isFolderRename()+", "+a.isFolderDelete()
//					+", "+a.isFileView()+", "+a.isFileUpload()+", "+a.isFileRename()+", "+a.isFileDelete());
//		}
		AccessControlUtil.getInstance(this).loadACLConfig();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		try {
			this.baseDir = AppConstants.getCkBaseDir()+CK_BASH_URL+
					(sessionInfo.getUserId())+"/";
			this.baseURL = SpringMVCHolder.getRequest().getContextPath()+ CK_BASH_URL +
					(sessionInfo.getUserId())+"/";
			/*Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();
			String parentDir = principal != null ? principal.getId() : "0";
			this.baseURL = ServletContextFactory.getServletContext().getContextPath() + CK_BASH_URL + parentDir + "/";
			this.baseDir = Global.getCkBaseDir() + parentDir + File.separator;*/
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new CKFinderConfig(this.servletConf);
    }

    @Override  
    public boolean checkAuthentication(final HttpServletRequest request) {
        return SecurityUtils.getCurrentSessionInfo()!=null;
    }

}
