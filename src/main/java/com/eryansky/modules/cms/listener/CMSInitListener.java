/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.listener;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.web.listener.DefaultSystemInitListener;
import com.eryansky.modules.cms.service.ArticleSeachService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;

/**
 * CMS初始化 建立全文检索索引
 */
public class CMSInitListener extends DefaultSystemInitListener{

	private static final Logger logger = LoggerFactory.getLogger(CMSInitListener.class);


	public CMSInitListener() {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("建立文章索引start");
		ArticleSeachService articleSeachService = SpringContextHolder.getBean(ArticleSeachService.class);
		articleSeachService.createIndex();
		logger.info("建立文章索引end");
	}
}
