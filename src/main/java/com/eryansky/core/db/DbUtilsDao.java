/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.PageSqlUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 调用Apache Commons DBUtil组件的数据库操作类 采用DBCP作为数据源，数据源在Spring中已经配置好 <br>
 * <br> 支持MySQL、Oracle、Postgresql分页查询.
 * <br>使用 .<code>
 * @Autowired
 * private DbUtilsDao dbUtilsDao;
 * public void setDbUtilsDao(DbUtilsDao dbUtilsDao) {
 *     this.dbUtilsDao = dbUtilsDao;
 * }
 * </code>
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-4-16 下午8:08:51
 * @version 1.0
 */
//@Repository
public class DbUtilsDao {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private DataSource dataSource;
	private QueryRunner queryRunner;

    public DbUtilsDao(){

    }
    public DbUtilsDao(BasicDataSource dataSource){
        this.dataSource = dataSource;
    }

	// 注入DBCP数据源
	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}

    public DataSource getDataSource() {
        return dataSource;
    }

    /**
	 * 执行sql语句,无法保证事务不推荐使用
	 * <br>需要自己控制事务.
	 * <br>Oracle序列:SEQ_NAME.nextval
	 * @param sql
	 *            sql语句
	 * @return 受影响的行数
	 * @deprecated
	 */
	public int update(String sql) throws DaoException{
		return update(sql, null);
	}

	/**
	 * 执行sql语句,无法保证事务不推荐使用 <code>
	 * <br>需要自己控制事务.
	 * executeUpdate("update user set username = 'kitty' where username = ?", "hello kitty");
	 * 
	 * </code>
	 * 
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 受影响的行数
	 * @deprecated
	 */
	public int update(String sql, Object param) throws DaoException{
		return update(sql, new Object[] { param });
	}

	/**
	 * 执行sql语句,无法保证事务不推荐使用
	 * <br>需要自己控制事务.
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 受影响的行数
	 * @deprecated
	 */
	public int update(String sql, Object[] params) throws DaoException {
		queryRunner = new QueryRunner(dataSource);
		int affectedRows = 0;
		try {
			if (params == null) {
				affectedRows = queryRunner.update(sql);
			} else {
				affectedRows = queryRunner.update(sql, params);
			}
		} catch (SQLException e) {
			logger.error("Error occured while attempting to update data", e);
			throw new DaoException(e);
		}
		return affectedRows;
	}

	/**
	 * 执行批量sql语句,无法保证事务不推荐使用
	 * <br>需要自己控制事务.
	 * @param sql
	 *            sql语句
	 * @param params
	 *            二维参数数组
	 * @return 受影响的行数的数组
	 * @deprecated
	 */
	public int[] batchUpdate(String sql, Object[][] params) throws DaoException {
		queryRunner = new QueryRunner(dataSource);
		int[] affectedRows = new int[0];
		try {
			affectedRows = queryRunner.batch(sql, params);
		} catch (SQLException e) {
			logger.error("Error occured while attempting to batch update data",
					e);
			throw new DaoException(e);
		}
		return affectedRows;
	}

	/**
	 * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
	 * 
	 * @param sql
	 *            sql语句
	 * @return 查询结果
	 */
	public List<Map<String, Object>> find(String sql) throws DaoException{
		return find(sql, null);
	}

	/**
	 * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
	 * 
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 查询结果
	 */
	public List<Map<String, Object>> find(String sql, Object param) throws DaoException{
		return find(sql, new Object[] { param });
	}

	/**
	 * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 查询结果
	 */
	public List<Map<String, Object>> find(String sql, Object[] params) throws DaoException{
		queryRunner = new QueryRunner(dataSource);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			if (params == null) {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler());
			} else {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler(), params);
			}
		} catch (SQLException e) {
			logger.error("Error occured while attempting to query data", e);
			throw new DaoException(e);
		}
		return list;
	}

	/**
	 * 执行查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @return 查询结果
	 */
	public <T> List<T> find(Class<T> entityClass, String sql) throws DaoException{
		return find(entityClass, sql, null);
	}

	/**
	 * 执行查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 查询结果
	 */
	public <T> List<T> find(Class<T> entityClass, String sql, Object param) throws DaoException{
		return find(entityClass, sql, new Object[] { param });
	}

	/**
	 * 执行查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 查询结果
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> find(Class<T> entityClass, String sql, Object[] params) throws DaoException{
		queryRunner = new QueryRunner(dataSource);
		List<T> list = new ArrayList<T>();
		try {
			if (params == null) {
				list = (List<T>) queryRunner.query(sql, new BeanListHandler(
						entityClass));
			} else {
				list = (List<T>) queryRunner.query(sql, new BeanListHandler(
						entityClass), params);
			}
		} catch (SQLException e) {
			logger.error("Error occured while attempting to query data", e);
			throw new DaoException(e);
		}
		return list;
	}

	/**
	 * 执行分页查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param page
	 *            页号
	 * @param pageSize
	 *            每页记录条数
	 * @return 查询结果
	 */
	public <T> List<T> find(Class<T> entityClass, String sql, int page,
			int pageSize) throws DaoException{
		return find(entityClass, sql, null, page, pageSize);
	}

	/**
	 * 执行分页查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @param page
	 *            页号
	 * @param pageSize
	 *            每页记录条数
	 * @return 查询结果
	 */
	public <T> List<T> find(Class<T> entityClass, String sql, Object param,
			int page, int pageSize) throws DaoException{
		return find(entityClass, sql, new Object[] { param }, page, pageSize);
	}

	/**
	 * 执行分页查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @param page
	 *            页号
	 * @param pageSize
	 *            每页记录条数
	 * @return 查询结果
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> find(Class<T> entityClass, String sql, Object[] params,
			int page, int pageSize) throws DaoException{
		queryRunner = new QueryRunner(dataSource);
		List<T> list = new ArrayList<T>();
		// int startFlag = (((page < 1 ? 1 : page) - 1) * pageSize);
		String pageSql = PageSqlUtils.createPageSql(sql, page, pageSize);
		try {
			if (params == null) {
				list = (List<T>) queryRunner.query(pageSql,
						new BeanListHandler(entityClass));
			} else {
				list = (List<T>) queryRunner.query(pageSql,
						new BeanListHandler(entityClass), params);
			}
		} catch (SQLException e) {
			logger.error("Error occured while attempting to query data", e);
			throw new DaoException(e);
		}
		return list;
	}

	/**
	 * 执行分页查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中,然后装List封装成PageResult对象
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param page
	 *            页号
	 * @param pageSize
	 *            每页记录条数
	 * @return PageResult对象
	 */
	public <T> Page<T> findPage(Class<T> entityClass, String sql, int page,
			int pageSize) throws DaoException{
		return findPage(entityClass, sql, null, page, pageSize);
	}

	/**
	 * 执行分页查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中,然后装List封装成PageResult对象
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @param page
	 *            页号
	 * @param pageSize
	 *            每页记录条数
	 * @return PageResult对象
	 */
	public <T> Page<T> findPage(Class<T> entityClass, String sql, Object param,
			int page, int pageSize) throws DaoException{
		return findPage(entityClass, sql, new Object[] { param }, page,
				pageSize);
	}

	/**
	 * 执行分页查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中,然后装List封装成PageResult对象
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句 例如: "select * T_SYS_MENU where name = ?"
	 * @param params
	 *            参数数组
	 * @param page
	 *            页号
	 * @param pageSize
	 *            每页记录条数
	 * @return PageResult对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Page<T> findPage(Class<T> entityClass, String sql,
			Object[] params, int page, int pageSize) throws DaoException{
		queryRunner = new QueryRunner(dataSource);
		List<T> list = new ArrayList<T>();
		int startPage = page < 1 ? 1 : page;
		// int startFlag = ((startPage - 1) * pageSize);
		String pageSql = PageSqlUtils.createPageSql(sql, startPage, pageSize);
		try {
			if (params == null) {
				list = (List<T>) queryRunner.query(pageSql,
						new BeanListHandler(entityClass));
			} else {
				list = (List<T>) queryRunner.query(pageSql,
						new BeanListHandler(entityClass), params);
			}
		} catch (SQLException e) {
			logger.error("Error occured while attempting to query data", e);
			throw new DaoException(e);
		}
		// 计算总行数
		long count = getCount(sql, params);
		int newCurrentPage = getBeginPage(startPage, pageSize, count);
		Page<T> p = new Page<T>(pageSize);
		p.setPageNo(newCurrentPage);
		p.setResult(list);
		p.setTotalCount(count);
		return p;
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成对象
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @return 对象
	 */
	public <T> T findFirst(Class<T> entityClass, String sql) throws DaoException{
		return findFirst(entityClass, sql, null);
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成对象
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 对象
	 */
	public <T> T findFirst(Class<T> entityClass, String sql, Object param) throws DaoException{
		return findFirst(entityClass, sql, new Object[] { param });
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成对象
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T findFirst(Class<T> entityClass, String sql, Object[] params) throws DaoException{
		queryRunner = new QueryRunner(dataSource);
		Object object = null;
		try {
			if (params == null) {
				object = queryRunner.query(sql, new BeanHandler(entityClass));
			} else {
				object = queryRunner.query(sql, new BeanHandler(entityClass),
						params);
			}
		} catch (SQLException e) {
			logger.error("Error occured while attempting to query data", e);
			throw new DaoException(e);
		}
		return (T) object;
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成Map对象
	 * 
	 * @param sql
	 *            sql语句
	 * @return 封装为Map的对象
	 */
	public Map<String, Object> findFirst(String sql) throws DaoException{
		return findFirst(sql, null);
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成Map对象
	 * 
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 封装为Map的对象
	 */
	public Map<String, Object> findFirst(String sql, Object param) throws DaoException{
		return findFirst(sql, new Object[] { param });
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成Map对象
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 封装为Map的对象
	 */
	public Map<String, Object> findFirst(String sql, Object[] params) throws DaoException{
		queryRunner = new QueryRunner(dataSource);
		Map<String, Object> map = null;
		try {
			if (params == null) {
				map = (Map<String, Object>) queryRunner.query(sql,
						new MapHandler());
			} else {
				map = (Map<String, Object>) queryRunner.query(sql,
						new MapHandler(), params);
			}
		} catch (SQLException e) {
			logger.error("Error occured while attempting to query data", e);
			throw new DaoException(e);
		}
		return map;
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnName
	 *            列名
	 * @return 结果对象
	 */
	public Object findBy(String sql, String columnName) throws DaoException{
		return findBy(sql, columnName, null);
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnName
	 *            列名
	 * @param param
	 *            参数
	 * @return 结果对象
	 */
	public Object findBy(String sql, String columnName, Object param) throws DaoException{
		return findBy(sql, columnName, new Object[] { param });
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnName
	 *            列名
	 * @param params
	 *            参数数组
	 * @return 结果对象
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object findBy(String sql, String columnName, Object[] params) throws DaoException{
		queryRunner = new QueryRunner(dataSource);
		Object object = null;
		try {
			if (params == null) {
				object = queryRunner.query(sql, new ScalarHandler(columnName));
			} else {
				object = queryRunner.query(sql, new ScalarHandler(columnName),
						params);
			}
		} catch (SQLException e) {
			logger.error("Error occured while attempting to query data", e);
			throw new DaoException(e);
		}
		return object;
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnIndex
	 *            列索引
	 * @return 结果对象
	 */
	public Object findBy(String sql, int columnIndex) throws DaoException{
		return findBy(sql, columnIndex, null);
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnIndex
	 *            列索引
	 * @param param
	 *            参数
	 * @return 结果对象
	 */
	public Object findBy(String sql, int columnIndex, Object param) throws DaoException{
		return findBy(sql, columnIndex, new Object[] { param });
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnIndex
	 *            列索引
	 * @param params
	 *            参数数组
	 * @return 结果对象
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object findBy(String sql, int columnIndex, Object[] params) throws DaoException{
		queryRunner = new QueryRunner(dataSource);
		Object object = null;
		try {
			if (params == null) {
				object = queryRunner.query(sql, new ScalarHandler(columnIndex));
			} else {
				object = queryRunner.query(sql, new ScalarHandler(columnIndex),
						params);
			}
		} catch (SQLException e) {
			logger.error("Error occured while attempting to query data", e);
			throw new DaoException(e);
		}
		return object;
	}

	/**
	 * 查询记录总条数
	 * 
	 * @param sql
	 *            sql语句
	 * @return 记录总数
	 */
	public long getCount(String sql) throws DaoException{
		return getCount(sql, null);
	}

	/**
	 * 查询记录总条数
	 * 
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 记录总数
	 */
	public long getCount(String sql, Object param) throws DaoException{
		return getCount(sql, new Object[] { param });
	}

	/**
	 * 查询记录总条数
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 记录总数
	 */
	public long getCount(String sql, Object[] params) throws DaoException{
		String newSql = "select count(1) from (" + sql + ") _c";
		if (params == null) {
			return ((Long) findBy(newSql, 1)).intValue();
		} else {
			return ((Long) findBy(newSql, 1, params)).intValue();
		}
	}

	private int getBeginPage(int begenPage, int pageSize, long count) {
		if (count == 0) {
			return 1;
		}
		int newCurrentPage = begenPage;
		if (begenPage > 1) {
			if ((begenPage - 1) * pageSize >= count) {
				newCurrentPage = (int) (Math.ceil((count * 1.0) / pageSize));
			}
		}
		return newCurrentPage;
	}


    /**
     * 得到数据库产品名称 Microsoft SQL Server / Oracle ....
     * @return
     * @throws DaoException
     */
    protected String getDatabaseProductName() throws DaoException {
        try {
            Connection conn = this.dataSource.getConnection();
            return conn.getMetaData().getDatabaseProductName();
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    /**
     * 得到当前的Catalog。
     * @return String
     * @throws DaoException
     */
    protected String GetCurrentCatalog() throws DaoException {
        //得到Microsoft SQL Server当前连接数据库名称的SQL语句
        //final String sql = "select db_name()";
        try {
            Connection conn = this.dataSource.getConnection();
            return conn.getCatalog();
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }
    private ScalarHandler scalarHandler = new ScalarHandler() {
        @Override
        public Object handle(ResultSet rs) throws SQLException {
            Object obj = super.handle(rs);
            if (obj instanceof BigInteger)
                return ((BigInteger) obj).longValue();
            return obj;
        }
    };
    public long count(String sql, Object... params) throws DaoException{
        Number num = 0;
        try {
            queryRunner = new QueryRunner(dataSource);
            if (params == null || params.length == 0) {
                num = (Number) queryRunner.query(sql, scalarHandler);
            } else {
                num = (Number) queryRunner.query(sql, scalarHandler, params);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            logger.error("统计记录集异常,SQL:" + sql, e);
            throw new DaoException(e);
        }
        return (num != null) ? num.longValue() : -1;
    }
}