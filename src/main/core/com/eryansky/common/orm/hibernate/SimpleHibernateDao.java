/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm.hibernate;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.annotation.Delete;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.reflection.ReflectionUtils;
import org.hibernate.*;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.util.Assert;

import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 封装Hibernate原生API的DAO泛型基类.
 * 
 * 可在Service层直接使用, 也可以扩展泛型DAO子类使用, 见两个构造函数的注释.
 * 参考Spring2.5自带的Petlinc例子, 取消了HibernateTemplate, 直接使用Hibernate原生API.
 * 
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
@SuppressWarnings("unchecked")
public class SimpleHibernateDao<T, PK extends Serializable> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected SessionFactory sessionFactory;

	protected Class<T> entityClass;

	/**
	 * 用于Dao层子类使用的构造函数.
	 * 通过子类的泛型定义取得对象类型Class.
	 * eg.
	 * public class UserDao extends SimpleHibernateDao<User, Long>
	 */
	public SimpleHibernateDao() {
		this.entityClass = ReflectionUtils.getClassGenricType(getClass());
	}

	/**
	 * 用于用于省略Dao层, 在Service层直接使用通用SimpleHibernateDao的构造函数.
	 * 在构造函数中定义对象类型Class.
	 * eg.
	 * SimpleHibernateDao<User, Long> userDao = new SimpleHibernateDao<User, Long>(sessionFactory, User.class);
	 */
	public SimpleHibernateDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
	}

	/**
	 * 取得sessionFactory.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * 采用@Autowired按类型注入SessionFactory, 当有多个SesionFactory的时候在子类重载本函数.
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * 取得当前Session.
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	/**
	 * 取得当前Connection.
	 */
	public Connection getConnection() {
	    try {
			return SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	/**
	 * 保存新增的对象.
	 */
	public void save(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		preperSave(entity);
		getSession().save(entity);
		logger.debug("save entity: {}", entity);
	}
	
	/**
	 * 保存修改的对象.
	 */
	public void update(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		preperSave(entity);
		getSession().update(entity);
		logger.debug("update entity: {}", entity);
	}

    /**
     * 更新
     *
     * @param hql
     * @param parameter
     * @return
     */
    public int update(String hql, Parameter parameter) {
        return createQuery(hql, parameter).executeUpdate();
    }

	public void preperSave(T entity){
		try {
			// 获取实体编号
			Object id = null;
			for (Method method : entity.getClass().getMethods()) {
				Id idAnn = method.getAnnotation(Id.class);
				if (idAnn != null) {
					id = method.invoke(entity);
					break;
				}
			}
			// 插入前执行方法
			boolean flag = (id == null);
			if(id != null && id instanceof String){
				flag = StringUtils.isBlank((String) id);
			}
			if (flag) {
				for (Method method : entity.getClass().getMethods()) {
					PrePersist pp = method.getAnnotation(PrePersist.class);
					if (pp != null) {
						method.invoke(entity);
						break;
					}
				}
			}
			// 更新前执行方法
			else {
				for (Method method : entity.getClass().getMethods()) {
					PreUpdate pu = method.getAnnotation(PreUpdate.class);
					if (pu != null) {
						method.invoke(entity);
						break;
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

    /**
     * 保存新增或修改的对象.
     * <br>自定义保存实体方法 内部使用saveOrUpdate
     * <br>注:保存之前会清空session 调用了clear()
     */
    public void saveEntity(final T entity) {
        Assert.notNull(entity, "entity不能为空");
		preperSave(entity);
        clear();//清空缓存
        getSession().saveOrUpdate(entity);
        logger.debug("saveOrUpdate entity: {}", entity);
    }

	/**
	 * 保存新增或修改的对象.
	 */
	public void saveOrUpdate(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		preperSave(entity);
		getSession().saveOrUpdate(entity);
		logger.debug("saveOrUpdate entity: {}", entity);
	}
	
	/**
	 * 保存新增或修改的对象集合.
	 */
	public void saveOrUpdate(final Collection<T> entitys) {
		Assert.notNull(entitys, "entitys不能为空");
		for (T entity : entitys) {
			this.saveOrUpdate(entity);
		}
	}
	
	
	/**
	 * 刷新对象.
	 */
	public void refresh(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().refresh(entity);
		logger.debug("refresh entity: {}", entity);
	}
	
	/**
	 * 刷新对象.
	 * <br>参数lockOptions可为null
	 * @param entity 操作对象
	 * @param lockOptions Hibernate LockOptions 
	 */
	public void refresh(T entity,LockOptions lockOptions) {
		if (lockOptions == null) {
			refresh(entity);
		} else {
			getSession().refresh(entity, lockOptions);
		}
	}
	
	/**
	 * 将对象变为游离状态.
	 */
	public void evict(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().evict(entity);
		logger.debug("evict entity: {}", entity);
	}

	/**
	 * 合并修改的对象.
	 */
	public void merge(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().merge(entity);
		logger.debug("merge entity: {}", entity);
	}
	
	/**
	 * 如果session中存在相同持久化识别的实例，用给出的对象的状态覆盖持久化实例
	 * 
	 * @param entityName 持久化对象名称
	 * @param entity 持久化实例
	 */
	public void merge(String entityName,T entity) {
		getSession().merge(entityName, entity);
	}

	/**
	 * 删除对象.
	 * <br>逻辑删除 实体包含StatusDelete注解的,将对象标记为注解的删除状态值.
	 * @param entity 对象必须是session中的对象或含id属性的transient对象.
	 */
	public void delete(final T entity) {
		Assert.notNull(entity, "entity不能为空");
		Delete delete = ReflectionUtils.getAnnotation(entityClass,Delete.class);
		if (delete != null) {
			Object value = ConvertUtils.convertToObject(delete.value(), delete.type().getValue());
			ReflectionUtils.invokeSetter(entity, delete.propertyName(), value);
			update(entity);
		} else {
			getSession().delete(entity);
		}
		logger.debug("delete entity: {}", entity);
	}
	
	/**
	 * 删除全部的.
	 * 
	 * @param entitys
	 */
	public void deleteAll(Collection<T> entitys) {
		for (Object entity : entitys) {
			getSession().delete(entity);
			getSession().flush();
		}
	}

	/**
	 * 按id删除对象.
	 */
	public void delete(final PK id) {
		Assert.notNull(id, "id不能为空");
		delete(get(id));
		logger.debug("delete entity {},id is {}", entityClass.getSimpleName(), id);
	}

	/**
	 * 按id获取对象(直接返回实体类).
	 */
	public T get(final PK id) {
		Assert.notNull(id, "id不能为空");
		return (T) getSession().get(entityClass, id);
	}

    public T getByHql(String qlString, Parameter parameter) {
        Query query = createQuery(qlString, parameter);
        return (T) query.uniqueResult();
    }

	/**
	 * 按id获取对象(实体的代理类实例,延迟缓存).
	 */
	public T load(final PK id) {
		Assert.notNull(id, "id不能为空");
		return (T) getSession().load(entityClass, id);
	}
	/**
	 * 按id获取对象(实体的代理类实例,是否枷锁加).
	 * @param id
	 * @param lockOptions
	 * @return
	 */
	public T load(Serializable id, LockOptions lockOptions) {
		Assert.notNull(id);
		T entity = null;
		if (lockOptions !=null) {
			entity = (T) getSession().load(entityClass, id,lockOptions);
		} else {
			entity = (T) getSession().load(entityClass, id);
		}
		return entity;
	}
	
	/**
	 * 按id列表获取对象列表.
	 */
	public List<T> get(final Collection<PK> ids) {
		return find(Restrictions.in(getIdName(), ids));
	}
	

	/**
	 *	获取全部对象.
	 */
	public List<T> getAll() {
		return find();
	}

	/**
	 *	获取全部对象, 支持按属性行序.
	 */
	public List<T> getAll(String orderByProperty, boolean isAsc) {
		Criteria c = createCriteria();
		if (isAsc) {
			c.addOrder(Order.asc(orderByProperty));
		} else {
			c.addOrder(Order.desc(orderByProperty));
		}
		return c.list();
	}
	
	/**
	 * 获取全部对象, 支持按属性行序.
	 * 
	 * @param orderBy
	 *            排序字段 多个排序字段时用','分隔.
	 * @param order
	 *            排序方式"asc"、"desc" 中间以","分割
	 */
	public List<T> getAll(String orderBy, String order) {
		Criteria c = createCriteria();
		//设置排序
		setPageParameterToCriteria(c, orderBy, order);
		return c.list();
	}

	/**
	 * 按属性查找对象列表, 匹配方式为相等.
	 */
	public List<T> findBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return find(criterion);
	}

	/**
	 * 按属性查找唯一对象, 匹配方式为相等.
	 */
	public T findUniqueBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return (T) createCriteria(criterion).uniqueResult();
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	public <X> List<X> find(final String hql, final Object... values) {
		return createQuery(hql, values).list();
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	public <X> List<X> find(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).list();
	}


    /**
     * 按HQL查询对象列表.
     *
     * @param hql
     * @param parameter
     * @return
     */
    @SuppressWarnings("unchecked")
    public <X> List<X> find(String hql, Parameter parameter) {
        Query query = createQuery(hql, parameter);
        return query.list();
    }

	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	public <X> X findUnique(final String hql, final Object... values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	public <X> X findUnique(final String hql, final Map<String, ?> values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 执行HQL进行批量修改/删除操作.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 * @return 更新记录数.
	 */
	public int batchExecute(final String hql, final Object... values) {
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * 执行HQL进行批量修改/删除操作.
	 * 
	 * @param values 命名参数,按名称绑定.
	 * @return 更新记录数.
	 */
	public int batchExecute(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * 执行HQL进行批量修改/删除操作.
	 *
	 * @param parameter 命名参数,按名称绑定.
	 * @return 更新记录数.
	 */
	public int batchExecute(final String hql, final Parameter parameter) {
		return createQuery(hql, parameter).executeUpdate();
	}
	
	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	public Query createQuery(final String queryString, final Object... values) {
		Assert.hasText(queryString, "queryString不能为空");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}
	
	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param values 命名参数,按名称绑定.
	 */
	public Query createQuery(final String queryString, final Map<String, ?> values) {
		Assert.hasText(queryString, "queryString不能为空");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			query.setProperties(values);
		}
		return query;
	}

	/**
	 * 按Criteria查询对象列表.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	public List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}
	
	/**
	 * 按Criteria查询对象列表.
	 * 
	 * @param orderBy 多个排序字段时用','分隔.
	 * @param orderBy 排序字段 多个排序字段时用','分隔.
	 * @param order 排序方式"asc"、"desc" 中间以","分割
	 * @return
	 */
	public List<T> find(final String orderBy, final String order,final Criterion... criterions) {
		Criteria c = createCriteria(criterions);
		//设置排序
		setPageParameterToCriteria(c, orderBy, order);
		return c.list();
	}

	/**
	 * 按Criteria查询唯一对象.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	public T findUnique(final Criterion... criterions) {
		return (T) createCriteria(criterions).uniqueResult();
	}

	/**
	 * 根据Criterion条件创建Criteria.
	 * 与find()函数可进行更加灵活的操作.
	 * 
	 * @param criterions 数量可变的Criterion.
	 */
	public Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 * 初始化对象.
	 * 使用load()方法得到的仅是对象Proxy, 在传到View层前需要进行初始化.
	 * 如果传入entity, 则只初始化entity的直接属性,但不会初始化延迟加载的关联集合和属性.
	 * 如需初始化关联属性,需执行:
	 * Hibernate.initialize(user.getRoles())，初始化User的直接属性和关联集合.
	 * Hibernate.initialize(user.getDescription())，初始化User的直接属性和延迟加载的Description属性.
	 */
	public void initProxyObject(Object proxy) {
		Hibernate.initialize(proxy);
	}

	/**
	 * Flush当前Session.
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * clear当前Session.
	 */
	public void clear() {
		getSession().clear();
	}
	
	/**
	 * 设置FlushMode.
	 */
	public void setFlushMode(FlushMode mode) {
		getSession().setFlushMode(mode);
	}
	
	/**
	 * 为Query添加distinct transformer.
	 * 预加载关联对象的HQL会引起主对象重复, 需要进行distinct处理.
	 */
	public Query distinct(Query query) {
		query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return query;
	}

	/**
	 * 为Criteria添加distinct transformer.
	 * 预加载关联对象的HQL会引起主对象重复, 需要进行distinct处理.
	 */
	public Criteria distinct(Criteria criteria) {
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria;
	}

	/**
	 * 取得对象的主键名.
	 */
	public String getIdName() {
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		return meta.getIdentifierPropertyName();
	}
	
	/**
	 * 获取实体名称
	 * 
	 * @return String
	 */
	public String getEntityName() {
		ClassMetadata meta = sessionFactory.getClassMetadata(entityClass);
		return meta.getEntityName();
	}

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
	 */
	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue) {
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		Object object = findUniqueBy(propertyName, newValue);
		return (object == null);
	}
	//原生SQL
	/**
	 * 根据查询SQL与参数列表创建SQLQuery对象.
	 * 
	 * @param values 数量可变的参数,按顺序绑定.
	 */
	public SQLQuery createSQLQuery(final String sql, final Object... values) {
		Assert.hasText(sql, "sql不能为空");
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				sqlQuery.setParameter(i, values[i]);
			}
		}
		return sqlQuery;
	}
	
	/**
	 * 根据查询SQL与参数列表创建SQLQuery对象.
	 * 
	 * @param sql
	 * @param values 命名参数,按名称绑定.
	 * @return
	 */
	public SQLQuery createSQLQuery(final String sql,final Map<String, ?> values) {
		Assert.hasText(sql, "sql不能为空");
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		if (values != null) {
			Set<String> set = values.keySet();
			for(String s:set){
				sqlQuery.setParameter(s,values.get(s));
			}
		}
		return sqlQuery;
	}
	
	/**
	 * 执行原生sql.
	 */
	public void executeJdbcUpdate(String sql) throws DaoException{
		Assert.hasText(sql, "sql不能为空");
		try {
			getConnection().prepareStatement(sql).execute();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}
	
	/**
	 * 执行原生sql查询.
	 */
	public ResultSet executeJdbcQuery(String sql) throws DaoException{
		Assert.hasText(sql, "sql不能为空");
		try {
			return getConnection().prepareStatement(sql).executeQuery();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}
	
	/**
	 * 判断entity实例是否已经与session缓存关联,是返回true,否则返回false
	 * 
	 * @param entity 实例
	 * 
	 * @return boolean
	 */
	public boolean contains(Object entity) {
		return getSession().contains(entity);
	}
	
	
	/**
	 * 设置分页参数到Criteria对象,辅助函数.
	 */
	protected Criteria setPageParameterToCriteria(final Criteria c,
			final String orderBy,final String order) {
		if (StringUtils.isNotBlank(orderBy) && StringUtils.isNotBlank(order)) {
			String[] orderByArray = StringUtils.split(orderBy, ',');
			String[] orderArray = StringUtils.split(order, ',');

			Assert.isTrue(orderByArray.length == orderArray.length,
					"分页多重排序参数中,排序字段与排序方向的个数不相等");

			for (int i = 0; i < orderByArray.length; i++) {
				if (Page.ASC.equals(orderArray[i])) {
					c.addOrder(Order.asc(orderByArray[i]));
				} else {
					c.addOrder(Order.desc(orderByArray[i]));
				}
			}
		}
		return c;
	}

    /**
     * SQL 查询
     *
     * @param sqlString
     * @return
     */
    public <E> List<E> findBySql(String sqlString) {
        return findBySql(sqlString, null, null);
    }

    /**
     * SQL 查询
     *
     * @param sqlString
     * @param parameter
     * @return
     */
    public <E> List<E> findBySql(String sqlString, Parameter parameter) {
        return findBySql(sqlString, parameter, null);
    }

    /**
     * SQL 查询
     *
     * @param sqlString
     * @param resultClass
     * @param parameter
     * @return
     */
    @SuppressWarnings("unchecked")
    public <E> List<E> findBySql(String sqlString, Parameter parameter, Class<?> resultClass) {
        SQLQuery query = createSqlQuery(sqlString, parameter);
        setResultTransformer(query, resultClass);
        return query.list();
    }

    /**
     * SQL 更新
     *
     * @param sqlString
     * @param parameter
     * @return
     */
    public int updateBySql(String sqlString, Parameter parameter) {
        return createSqlQuery(sqlString, parameter).executeUpdate();
    }


    /**
     * 创建 SQL 查询对象
     *
     * @param sql
     * @param parameter
     * @return
     */
    public SQLQuery createSqlQuery(String sql, Parameter parameter) {
        SQLQuery query = getSession().createSQLQuery(sql);
        setParameter(query, parameter);
        return query;
    }

    /**
     * 创建 QL 查询对象
     *
     * @param hql
     * @param parameter
     * @return
     */
    public Query createQuery(String hql, Parameter parameter) {
        Query query = getSession().createQuery(hql);
        setParameter(query, parameter);
        return query;
    }


    // -------------- Query Tools --------------

    /**
     * 设置查询结果类型
     *
     * @param query
     * @param resultClass
     */
    protected void setResultTransformer(SQLQuery query, Class<?> resultClass) {
        if (resultClass != null) {
            if (resultClass == Map.class) {
                query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            } else if (resultClass == List.class) {
                query.setResultTransformer(Transformers.TO_LIST);
            } else {
                query.addEntity(resultClass);
            }
        }
    }


    /**
     * 设置查询参数
     *
     * @param query
     * @param parameter
     */
    protected void setParameter(Query query, Parameter parameter) {
        if (parameter != null) {
            Set<String> keySet = parameter.keySet();
            for (String string : keySet) {
                Object value = parameter.get(string);
                // 这里考虑传入的参数是什么类型，不同类型使用的方法不同
                if (value instanceof Collection<?>) {
                    query.setParameterList(string, (Collection<?>) value);
                } else if (value instanceof Object[]) {
                    query.setParameterList(string, (Object[]) value);
                } else {
                    query.setParameter(string, value);
                }
            }
        }
    }

    /**
     * 去除qlString的select子句。
     *
     * @param qlString
     * @return
     */
    protected String removeSelect(String qlString) {
        int beginPos = qlString.toLowerCase().indexOf("from");
        return qlString.substring(beginPos);
    }

    /**
     * 去除hql的orderBy子句。
     *
     * @param qlString
     * @return
     */
    protected String removeOrders(String qlString) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(qlString);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }
}