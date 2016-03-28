/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.persistence.CrudDao;
import com.eryansky.modules.cms.mapper.Article;
import com.eryansky.modules.cms.mapper.Category;

import java.util.List;

/**
 * 文章DAO接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-8-23
 */
@MyBatisDao
public interface ArticleDao extends CrudDao<Article> {
	
	public List<Article> findByIdIn(String[] ids);
	
	public int updateHitsAddOne(String id);
	
	public int updateExpiredWeight(Article article);
	
	public List<Category> findStats(Category category);
	
}
