/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.core.orm.mybatis.dao.TreeDao;
import com.eryansky.modules.cms.mapper.Category;

import java.util.List;
import java.util.Map;


/**
 * 栏目DAO接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-8-23
 */
@MyBatisDao
public interface CategoryDao extends TreeDao<Category> {

	/**
	 * 根据站点编码查找
	 * @param category
	 * @return
	 */
	Category getByCode(Category category);

	List<Category> findModule(Category category);

	public List<Category> findByParentIdsLike(Category category);

	List<Category> findByModule(String module);

	List<Category> findByParentId(String parentId, String isMenu);


	List<Category> findByParentIdAndSiteId(Category entity);
	
	List<Map<String, Object>> findStats(String sql);

}
