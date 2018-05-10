/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.mybatis.dao;

import com.eryansky.common.persistence.CrudDao;
import com.eryansky.core.orm.mybatis.entity.TreeEntity;

import java.util.List;

/**
 * DAO支持类实现
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-05-16
 * @param <T>
 */
public interface TreeDao<T extends TreeEntity<T>> extends CrudDao<T> {

	/**
	 * 找到所有子节点
	 * @param entity
	 * @return
	 */
	List<T> findByParentIdsLike(T entity);

	/**
	 * 更新所有父节点字段
	 * @param entity
	 * @return
	 */
	int updateParentIds(T entity);
	
}