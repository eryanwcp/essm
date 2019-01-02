/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.mybatis.dao;

import com.eryansky.common.orm.persistence.PCrudDao;
import com.eryansky.core.orm.mybatis.entity.PTreeEntity;

import java.io.Serializable;
import java.util.List;

/**
 * DAO支持类实现
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-05-16
 * @param <T>
 */
public interface PTreeDao<T extends PTreeEntity<T,PK>,PK extends Serializable> extends PCrudDao<T,PK> {

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