/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.mybatis;

import java.io.Serializable;
import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.eryansky.common.orm.Page;

/**
 * MyBatis的Dao基类.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-7-28 下午7:38:46
 */
public class MyBatisSuportDao extends SqlSessionDaoSupport {
	/**
	 * 保存对象
	 * 
	 * @param key
	 * @param object
	 *            对象
	 * @date 2012-7-28 下午7:38:53
	 */
	public void save(String key, Object object) {
		getSqlSession().insert(key, object);
	}

	/**
	 * 删除对象
	 * 
	 * @param key
	 * @param id
	 *            主键id
	 * @date 2012-7-28 下午7:39:26
	 */
	public void delete(String key, Serializable id) {
		getSqlSession().delete(key, id);
	}

	/**
	 * 删除对象
	 * 
	 * @param key
	 * @param object
	 *            对象
	 * @date 2012-7-28 下午7:39:46
	 */
	public void delete(String key, Object object) {
		getSqlSession().delete(key, object);
	}

	/**
	 * 查询得到单个对象
	 * 
	 * @param key
	 * @param params
	 *            查询参数
	 * @return 
	 * @date 2012-7-28 下午7:40:08
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Object params) {
		return (T) getSqlSession().selectOne(key, params);
	}

	/**
	 * 查询得到对象集合 （无参数查询方式）
	 * 
	 * @param key
	 * @return
	 * @date 2012-7-28 下午7:41:01
	 */
	public <T> List<T> getList(String key) {
		return getSqlSession().selectList(key);
	}

	/**
	 * 查询得到对象集合 （根据参数查询方式）
	 * 
	 * @param key
	 * @param params
	 *            查询参数
	 * @return
	 * @date 2012-7-28 下午7:43:02
	 */
	public <T> List<T> getList(String key, Object params) {
		return getSqlSession().selectList(key, params);
	}

	/**
	 * 分页查询（参数）
	 * 
	 * @param key
	 * @param offset
	 *            偏移量
	 * @param limit
	 *            单页大小
	 * @param params
	 *            参数 (Map<String,Object>)
	 * @return List<T>
	 * @date 2012-7-29 上午1:35:32
	 */
	public <T> List<T> getList(String key, int offset, int limit, Object params) {
		return getSqlSession().selectList(key, params,
				new RowBounds(offset, limit));
	}

	/**
	 * 分页查询（参数）
	 * 
	 * @param key
	 * @param page
	 * @param params
	 *            参数 (Map<String,Object>)
	 * @return Page<T>
	 * @date 2012-7-29 上午3:50:26
	 */
	public <T> Page<T> getPage(String key, Page<T> page, Object params) {

		int offset = Page.countOffset(page.getPageSize(), page.getPageNo());

		List<T> list = getList(key, params);
		if (!list.isEmpty() && list.size() > 0) {
			page.setTotalCount(list.size());
		}

		page.setTotalPage(Page.countTotalPage(page.getPageSize(),
				page.getTotalCount()));

		List<T> result = getSqlSession().selectList(key, params,
				new RowBounds(offset, page.getPageSize()));
		page.setResult(result);

		return page;
	}

}
