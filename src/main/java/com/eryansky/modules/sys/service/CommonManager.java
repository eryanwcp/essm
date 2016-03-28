/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.hibernate.DefaultEntityManager;
import com.eryansky.common.orm.jdbc.JdbcDao;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;

/**
 * Service层实现类. <br>
 * 提供常用的共同方法.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-4 下午10:17:15
 * 
 */
@Service
public class CommonManager extends DefaultEntityManager<Object,String>{
	/**
	 * Spring jdbc工具类.
	 */
	@Autowired
	protected JdbcDao jdbcDao;
	
	/**
	 * 根据表名、字段名、字段值返回该对象id 无改对象则返回null.
	 * @param tableName 表名
	 * @param fieldName 字段名
	 * @param fieldValue 字段值
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public String getIdByTFO(String tableName, String fieldName,
			Object fieldValue) throws DaoException, SystemException,
			ServiceException {
		Assert.notNull(tableName, "参数[tableName]为空!");
		Assert.notNull(fieldName, "参数[fieldName]为空!");
		Map<String, Object> map = Maps.newHashMap();
	    map.put(fieldName, fieldValue);
	    StringBuilder sb = new StringBuilder();
	    sb.append("select id from ").append(tableName).append(" where ").append(fieldName).append(" = ?");
	    Map<String, Object> result = jdbcDao.queryForMap(sb.toString(), fieldValue);
		String id = null;
	    if(result!=null){
	    	id =  (String) result.get("id");
	    }
	    return id;
	}

	/**
	 * 根据某个属性得到单个对象.
	 *
	 * @param entityName
	 *            BO名称 例如: "Resource"
	 * @param propertyName
	 *            属性名 例如:"name"
	 * @param propertyValue
	 *            属性值
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public String getIdByProperty(String entityName, String propertyName,
			Object propertyValue) throws DaoException, SystemException,
			ServiceException {
		Assert.notNull(entityName, "参数[entityName]为空!");
		Assert.notNull(propertyName, "参数[propertyName]为空!");
		StringBuilder sb = new StringBuilder();
		sb.append("select e.id from e.").append(entityName).append(" e where ")
				.append(propertyName).append(" = ?");
		logger.debug(sb.toString());
		Iterator<?> iterator = entityDao.createQuery(sb.toString(),
				new Object[] { propertyValue }).iterate();
		String id = null;
		while (iterator.hasNext()) {
			id = (String) iterator.next();
		}
		return id;
	}

}
