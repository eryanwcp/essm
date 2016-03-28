/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.persistence.CrudDao;
import com.eryansky.modules.sys.mapper.Config;

import java.util.List;


/**
 *日志DAO接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2015-9-26
 */
@MyBatisDao
public interface ConfigDao extends CrudDao<Config> {
	/**
	 * 根据指定查询条件查询
	 * @return
	 */
	Config getBy(Config config);

	/**
	 *
	 * @param parameter
	 * @return
	 */
	List<Config> findQueryList(Parameter parameter);
}
