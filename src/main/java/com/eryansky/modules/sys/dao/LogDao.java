/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.persistence.CrudDao;
import com.eryansky.modules.sys.mapper.Log;

import java.util.List;
import java.util.Map;


/**
 *日志DAO接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2015-9-26
 */
@MyBatisDao
public interface LogDao extends CrudDao<Log> {
	/**
	 * 清除
	 * @return
	 */
	int remove(String id);
	/**
	 * 删除所有 清空
	 * @return
	 */
	int removeAll();

	/**
	 * 清空有效期之外的日志
	 * @return
	 */
	int clearInvalidLog(Map<String,Object> parameter);

	/**
	 * 用户登录测试
	 * @param parameter
	 * @return
	 */
	Long getUserLoginCount(Map<String,Object> parameter);

	/**
	 * 查询标题为null的数据
	 * @return
	 */
	List<Log> findNullData();

	/**
	 * 根据“module”查询标题不为null的数据
	 * @param module
	 * @return
	 */
	Log getNotNullData(String module);
}
