/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.orm.mybatis.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.persistence.PCrudDao;
import com.eryansky.core.orm.mybatis.entity.PBaseEntity;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.orm.mybatis.entity.PDataEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * Service基类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-05-16
 */
public abstract class PCrudService<D extends PCrudDao<T,PK>, T extends PBaseEntity<T,PK>,PK extends Serializable> extends BaseService {

	/**
	 * 持久层对象
	 */
	@Autowired
	protected D dao;


	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	public T get(PK id) {
		return dao.get(id);
	}

	/**
	 * 获取单条数据
	 * @param entity
	 * @return
	 */
	public T get(T entity) {
		return dao.get(entity);
	}

	/**
	 * 查询列表数据
	 * @param entity
	 * @return
	 */
	public List<T> findList(T entity) {
		return dao.findList(entity);
	}

	/**
	 * 查询分页数据
	 * @param page 分页对象
	 * @param entity
	 * @return
	 */
	public Page<T> findPage(Page<T> page, T entity) {
		entity.setEntityPage(page);
		page.setResult(dao.findList(entity));
		return page;
	}

	/**
	 * 保存数据（插入或更新）
	 * @param entity
	 */
	public void save(T entity) {
		if (entity.getIsNewRecord()){
			entity.prePersist();
			dao.insert(entity);
		}else{
			entity.preUpdate();
			dao.update(entity);
		}
	}

	/**
	 * 删除数据
	 * @param entity
	 */
	public void delete(T entity) {
		dao.delete(entity);
	}


	/**
	 * 删除数据
	 * @param id
	 */
	public void delete(PK id) {
		dao.delete(id);
	}

	/**
	 * 删除或还原删除数据
	 * @param entity
	 * @param isRe 是否还原
	 */
	public void delete(T entity, Boolean isRe) {
		if(isRe != null && isRe){
			if(entity instanceof PDataEntity){
				PDataEntity dataEntity = (PDataEntity) entity;
				dataEntity.setStatus(DataEntity.STATUS_NORMAL);
			}
			save(entity);
		}else{
			delete(entity);
		}
	}

}
