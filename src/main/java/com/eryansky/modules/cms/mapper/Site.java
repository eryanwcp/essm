/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.mapper;

import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.cms.utils.CmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Transient;


/**
 * 站点Entity
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-05-15
 */
public class Site extends DataEntity<Site> {
	
	private static final long serialVersionUID = 1L;
	private String name;	// 站点名称
	private String code;	// 站点编码
	private String title;	// 站点标题
	private String logo;	// 站点logo
	private String description;// 描述，填写有助于搜索引擎优化
	private String keywords;// 关键字，填写有助于搜索引擎优化
	private String theme;	// 主题
	private String copyright;// 版权信息
	private String customIndexView;// 自定义首页视图文件
	private String domain;

	public Site() {
		super();
	}
	
	public Site(String id){
		this();
		this.id = id;
	}

	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=1, max=64)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Length(min=1, max=100)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Length(min=0, max=255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Length(min=0, max=255)
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@Length(min=1, max=255)
	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getCustomIndexView() {
		return customIndexView;
	}

	public void setCustomIndexView(String customIndexView) {
		this.customIndexView = customIndexView;
	}

	/**
	 * 获取默认站点ID
	 */
	@Transient
	public static String defaultSiteCode(){
		return "1";
	}

	/**
	 * 判断是否为默认（主站）站点
	 */
	@Transient
	public static boolean isDefault(String siteCode){
		return siteCode != null && siteCode.equals(defaultSiteCode());
	}


	/**
	 * 设置当前站点
	 * @param siteCode
	 */
	public static void setCurrentSiteCode(String siteCode){
		CmsUtils.putCache("siteCode",siteCode);
	}


	/**
	 * 获取当前编辑的站点编号
	 */
	@Transient
	public static String getCurrentSiteCode(){
		String siteCode = (String) CmsUtils.getCache("siteCode");
		return StringUtils.isNotBlank(siteCode)?siteCode:defaultSiteCode();
	}


	/**
   	 * 模板路径
   	 */
   	public static final String TPL_BASE = "/WEB-INF/views/modules/cms/front/themes";

    /**
   	 * 获得模板方案路径。如：/WEB-INF/views/modules/cms/front/themes/jeesite
   	 *
   	 * @return
   	 */
   	public String getSolutionPath() {
   		return TPL_BASE + "/" + getTheme();
   	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}



}