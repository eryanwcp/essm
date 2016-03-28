/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.hibernate;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PropertyFilter;
import com.eryansky.common.orm.PropertyFilter.MatchType;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.utils.collections.Collections3;
import org.hibernate.criterion.Criterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Service层领域对象业务管理类基类.
 * 使用HibernateDao<T,PK>进行业务对象的CRUD操作,子类需重载getEntityDao()函数提供该DAO.
 * 
 * @param <T>
 *            领域对象类型
 * @param <PK>
 *            领域对象的主键类型
 * 
 *            eg. public class UserManager extends EntityManager<User, Long>{ }
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
@Transactional
public abstract class EntityManager<T, PK extends Serializable> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected abstract HibernateDao<T, PK> getEntityDao();

	// CRUD函数 //

	/**
	 * 保存新增的对象.
	 * 
	 * @param entity
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void save(final T entity) throws DaoException, SystemException,
			ServiceException {
		getEntityDao().save(entity);
	}

	/**
	 * 保存修改的对象.
	 * 
	 * @param entity
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void update(final T entity) throws DaoException, SystemException,
			ServiceException {
		getEntityDao().update(entity);
	}

    /**
     * 保存新增或修改的对象.
     * <br>自定义保存实体方法 内部使用saveOrUpdate
     * <br>注:保存之前会清空session 调用了clear()
     * @param entity
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public void saveEntity(final T entity) throws DaoException,
            SystemException, ServiceException {
        getEntityDao().saveEntity(entity);
    }

	/**
	 * 保存新增或修改的对象.
	 * 
	 * @param entity
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void saveOrUpdate(final T entity) throws DaoException,
			SystemException, ServiceException {
		getEntityDao().saveOrUpdate(entity);
	}

	/**
	 * 保存新增或修改的对象集合.
	 * 
	 * @param entitys
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void saveOrUpdate(final Collection<T> entitys) throws DaoException,
			SystemException, ServiceException {
		getEntityDao().saveOrUpdate(entitys);
	}
	
	/**
	 * 清除当前session.
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void clear() throws DaoException,
		    SystemException, ServiceException {
	    getEntityDao().clear();
	}
	
	/**
	 * 将对象变为游离状态.
	 * @param entity
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void evict(T entity) throws DaoException,
		    SystemException, ServiceException {
		getEntityDao().evict(entity);
	}

    /**
	 * 修改合并.
	 * 
	 * @param entity
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void merge(final T entity) throws DaoException, SystemException,
			ServiceException {
		getEntityDao().merge(entity);
	}

	/**
	 * 删除(根据主键ID).
	 * 
	 * @param id
	 *            主键ID
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void deleteById(final PK id) throws DaoException, SystemException,
			ServiceException {
		getEntityDao().delete(id);
	}

	/**
	 * 删除(根据对象).
	 * 
	 * @param entity
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void delete(final T entity) throws DaoException, SystemException,
			ServiceException {
		Assert.notNull(entity, "参数[entity]为空!");
		getEntityDao().delete(entity);
	}

	/**
	 * 删除全部的.
	 * 
	 * @param entitys
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public void deleteAll(final Collection<T> entitys) throws DaoException,
			SystemException, ServiceException {
		if (!Collections3.isEmpty(entitys)) {
			for (T entity : entitys) {
				getEntityDao().delete(entity);
			}
		} else {
			logger.warn("参数[entitys]为空.");
		}
	}

	/**
	 * 根据id集合删除全部的.
	 * 
	 * @param ids
	 *            id集合
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void deleteByIds(final List<PK> ids) throws DaoException,
			SystemException, ServiceException {
		if (!Collections3.isEmpty(ids)) {
			for (PK id : ids) {
				getEntityDao().delete(id);
			}
		} else {
			logger.warn("参数[ids]为空.");
		}
	}

	/**
	 * 按id获取对象(代理 延迟加载).
	 * 
	 * @param id
	 *            主键ID
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public T loadById(final PK id) throws DaoException, SystemException,
			ServiceException {
		return getEntityDao().load(id);
	}

	/**
	 * 按id获取对象(直接返回实体类).
	 * 
	 * @param id
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public T getById(final PK id) throws DaoException, SystemException,
			ServiceException {
		return getEntityDao().get(id);
	}

	/**
	 * 自定义属性查找.
	 * 
	 * @param propertyName
	 *            属性名称
	 * @param propertyValue
	 *            属性值
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public List<T> findBy(String propertyName, Object propertyValue)
			throws DaoException, SystemException, ServiceException {
		Assert.hasText(propertyName, "参数[entityName]为空!");
		return getEntityDao().findBy(propertyName, propertyValue);
	}

	/**
	 * 自定义属性查找唯一值.
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public T findUniqueBy(String propertyName, Object value)
			throws DaoException, SystemException, ServiceException {
		return getEntityDao().findUniqueBy(propertyName, value);
	}

	/**
	 * 自定义属性查找(like全匹配方式)
	 * 
	 * @param propertyName
	 *            属性名称
	 * @param value
	 *            属性值(无需加+"%")
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public List<T> findByLike(String propertyName, String value)
			throws DaoException, SystemException, ServiceException {
		return getEntityDao().findBy(propertyName, value, MatchType.LIKE);
	}

	/**
	 * 查询所有分页.
	 * 
	 * @param page
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public Page<T> findPage(final Page<T> page) throws DaoException,
			SystemException, ServiceException {
		return getEntityDao().getAll(page);
	}

	/**
	 * 查询所有.
	 * 
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public List<T> getAll() throws DaoException, SystemException,
			ServiceException {
		return getEntityDao().getAll();
	}

	/**
	 * 查询所有(排序).
	 * 
	 * @param orderBy
	 *            排序字段 多个排序字段时用','分隔.
	 * @param order
	 *            排序方式"asc"、"desc" 中间以","分割
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public List<T> getAll(String orderBy, String order) throws DaoException,
			SystemException, ServiceException {
		return getEntityDao().getAll(orderBy, order);
	}


	/**
	 * 过滤器查询.
	 * 
	 * @param filters
	 *            属性过滤器
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public List<T> find(final List<PropertyFilter> filters)
			throws DaoException, SystemException, ServiceException {
		return getEntityDao().find(filters);
	}

	/**
	 * 过滤器查询.
	 * 
	 * @param filters
	 *            属性过滤器
	 * @param orderBy
	 *            排序字段 多个排序字段时用','分隔.
	 * @param order
	 *            排序方式"asc"、"desc" 中间以","分割
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public List<T> find(final List<PropertyFilter> filters,
			final String orderBy, final String order) throws DaoException,
			SystemException, ServiceException {
		return getEntityDao().find(filters, orderBy, order);
	}

	/**
	 * 自定义hql分页查询.
	 * 
	 * @param page 分页对象
	 * @param hql HQL语句
	 * @param values 参数
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public Page<T> findPage(final Page<T> page, final String hql,
			final Object... values) throws DaoException, SystemException,
			ServiceException {
		return getEntityDao().findPage(page, hql, values);
	}

    /**
     * 自定义hql分页查询.
     *
     * @param page 分页对象
     * @param hql HQL语句
     * @param parameter 参数
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @Transactional(readOnly = true)
    public Page<T> findPage(final Page<T> page, final String hql,
                        final Parameter parameter) throws DaoException, SystemException,
            ServiceException {
        return getEntityDao().findPage(page, hql, parameter);
    }

    /**
     * 过滤器分页查询.
     *
     * @param page
     *            分页对象
     * @param filters
     *            属性过滤器
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @Transactional(readOnly = true)
    public Page<T> findPage(final Page<T> page, final List<PropertyFilter> filters)
            throws DaoException, SystemException, ServiceException {
        return this.findPage(page, filters,false);
    }

    /**
     * 过滤器分页查询.
     * @param p 分页对象
     * @param filters
     *            属性过滤器
     * @param isFilterDelete
     *            是否过滤逻辑删除的数据
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @Transactional(readOnly = true)
    public Page<T> findPage(Page<T> p,
                         List<PropertyFilter> filters, boolean isFilterDelete)
            throws DaoException, SystemException, ServiceException {
        // 过滤逻辑删除的数据
        if (isFilterDelete) {
            PropertyFilter normal = new PropertyFilter("NES_status",
                    StatusState.DELETE.getValue() + "");
            filters.add(normal);
        }
        return getEntityDao().findPage(p, filters);
    }


	/**
	 * 自定义Criterion分页查询.
	 * 
	 * @param page
	 *            分页对象.
	 * @param criterions
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public Page<T> findPageByCriteria(Page<T> page, Criterion... criterions)
			throws DaoException, SystemException, ServiceException {
		Assert.notNull(page, "参数[page]为空!");
		Assert.notNull(criterions, "参数[criterions]为空!");
		return getEntityDao().findPage(page, criterions);
	}

	/**
	 * 自定义Criterion查询.
	 * 
	 * @param criterions
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public List<T> findByCriteria(Criterion... criterions) throws DaoException,
			SystemException, ServiceException {
		Assert.notNull(criterions, "参数[criterions]为空!");
		return getEntityDao().find(criterions);
	}

	/**
	 * 判断对象某些属性的值在数据库中是否唯一.
	 * 
	 * @param uniquePropertyNames
	 *            在POJO里不能重复的属性列表,以逗号分割 如"name,loginid,password"
	 * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public boolean isUnique(T entity, String uniquePropertyNames)
			throws DaoException, SystemException, ServiceException {
		return getEntityDao().isUnique(entity, uniquePropertyNames);
	}

	/**
	 * 将PropertyFilter列表转化为Criterion数组.
	 * 
	 * @param filters
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@Transactional(readOnly = true)
	public Criterion[] getCriterionsByFilter(List<PropertyFilter> filters)
			throws DaoException, SystemException, ServiceException {
		return getEntityDao().buildCriterionByPropertyFilter(filters);
	}


    /**
     * 初始化对象.
     * @param proxy   目标对象
     */
    @Transactional(readOnly = true)
    public void initProxyObject(Object proxy) {
        getEntityDao().initProxyObject(proxy);
    }
}