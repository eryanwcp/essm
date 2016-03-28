/**
 *  Copyright (c) 2013 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.meta;

import com.eryansky.core.db.utils.DBType;
import com.eryansky.core.db.vo.ColumnVo;
import com.eryansky.core.db.vo.DbConfig;
import com.eryansky.core.db.vo.SchemaVo;
import com.eryansky.core.db.vo.TableVo;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 用于数据库操作。主要包括读取数据库表相关信息
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 1.0
 * @date 2013-7-15 下午1:38:27
 */
public abstract class DiDataSource extends BasicDataSource {

	private Logger logger = LoggerFactory.getLogger(getClass());

	// protected String[] DEFAULT_TYPES = new String[]{"TABLE", "VIEW"};
	protected String[] DEFAULT_TYPES = new String[] { "TABLE" };

	public static final String PATTERN_ALL = "%";
	/**
	 * 存储被对象使用的数据库连接对象 <br/>
	 * 如果要让该对象从新获取数据库连接 可使用datasource自身对象的方法getConnection()
	 */
	protected Connection conn = null;

	protected DbConfig dbConfig;

	public DiDataSource() {
		super();
	}

	public DiDataSource(DbConfig dbConfig) throws SQLException {
		super.setUrl(dbConfig.getUrl());
		super.setUsername(dbConfig.getUsername());
		super.setPassword(dbConfig.getPassword());
		super.setDriverClassName(dbConfig.getDriverClassName());
		this.conn = super.getConnection();
		this.dbConfig = dbConfig;
		this.prepareConn();
	}

	public DiDataSource(DbConfig dbConfig, int initialSize, int maxActive,
			int maxIdle, int maxWait) throws SQLException {
		super.setUrl(dbConfig.getUrl());
		super.setUsername(dbConfig.getUsername());
		super.setPassword(dbConfig.getPassword());
		super.setDriverClassName(dbConfig.getDriverClassName());
		super.setInitialSize(initialSize);
		super.setMaxActive(maxActive);
		super.setMaxIdle(maxIdle);
		super.setMaxWait(maxWait);
		this.conn = super.getConnection();
		this.dbConfig = dbConfig;
		this.prepareConn();
	}

	/**
	 * 数据库类型
	 * 
	 * @see DBType
	 */
	public abstract DBType getDbType() throws SQLException;

	/**
	 * JDBC默认连接模式
	 */
	public abstract String getSchema() throws SQLException;

	/**
	 * 获取所有模式
	 */
	public abstract List<SchemaVo> getSchemaVos() throws SQLException;

	/**
	 * JDBC默认连接目录
	 */
	public abstract String getCatalog() throws SQLException;

	/**
	 * 根据namePattern获取表名
	 * 
	 * @param namePattern
	 *            表匹配
	 * @return
	 * @throws SQLException
	 */
	public abstract List<TableVo> getTableVos(String namePattern)
			throws SQLException;

	/**
	 * 根据模式、表明匹配获取表名
	 *
	 * @param schemaPattern
	 *            模式匹配
	 * @param namePattern
	 *            表匹配
	 * @return
	 * @throws SQLException
	 */
	public abstract List<TableVo> getTableVos(String schemaPattern,
			String namePattern) throws SQLException;

	/**
	 * 根据模式、目录、表明匹配获取表名
	 *
	 * @param catalog
	 *            目录
	 * @param schemaPattern
	 *            模式匹配
	 * @param namePattern
	 *            表匹配
	 * @return
	 * @throws SQLException
	 */
	public abstract List<TableVo> getTableVos(String catalog,
			String schemaPattern, String namePattern) throws SQLException;

	/**
	 * 根据表列信息
	 *
	 * @param namePattern
	 *            表匹配
	 * @return
	 * @throws SQLException
	 */
	public abstract List<ColumnVo> getColumnVos(String namePattern)
			throws SQLException;

	/**
	 * 根据模式、表明匹配得到列信息
	 *
	 * @param schemaPattern
	 *            模式匹配
	 * @param namePattern
	 *            表匹配
	 * @return
	 * @throws SQLException
	 */
	public abstract List<ColumnVo> getColumnVos(String schemaPattern,
			String namePattern) throws SQLException;

	/**
	 * 根据模式、目录、表名匹配得到列信息
	 *
	 * @param catalog
	 *            目录
	 * @param schemaPattern
	 *            模式匹配
	 * @param namePattern
	 *            表匹配
	 * @return
	 * @throws SQLException
	 */
	public abstract List<ColumnVo> getColumnVos(String catalog,
			String schemaPattern, String namePattern) throws SQLException;

	/**
	 * 得到主键信息
	 *
	 * @param namePattern
	 *            表匹配
	 * @return
	 * @throws SQLException
	 */
	public abstract List<ColumnVo> getPrimaryKeys(String namePattern)
			throws SQLException;

	/**
	 * 根据模式、目录、表名匹配得到主键信息
	 *
	 * @param catalog
	 *            目录
	 * @param schemaPattern
	 *            模式匹配
	 * @param namePattern
	 *            表匹配
	 * @return
	 * @throws SQLException
	 */
	public abstract List<ColumnVo> getPrimaryKeys(String catalog,
			String schemaPattern, String namePattern) throws SQLException;

	/**
	 * 得到外键信息
	 *
	 * @param namePattern
	 *            表匹配
	 * @return
	 * @throws SQLException
	 */
	public abstract List<ColumnVo> getForeignKeys(String namePattern)
			throws SQLException;

	/**
	 * 分页语句
	 *
	 * @param sql
	 * @param offset
	 * @param limit
	 * @return
	 * @throws SQLException
	 */
	public abstract String getPageSql(String sql, int offset, int limit)
			throws SQLException;

	/**
	 * @return
	 * @throws SQLException
	 */
	public abstract void prepareConn();

	/**
	 * 得到表的数据总量
	 *
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public long getCount(String tableName) throws SQLException {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		long maxPageSize = 10000;
		try {
			String sql = "select count(*) from " + tableName;
			pstm = getConn().prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
			rs = pstm.executeQuery();
			rs.next();
			return rs.getLong(1);
		} catch (SQLException e) {
			throw e;
		} finally {
			this.close(pstm, rs);
		}
	}

	/**
	 * 检索此数据库是否支持与给定结果集类型结合的给定并发类型
	 *
	 * @return
	 * @throws SQLException
	 */
	public boolean supportsResultSetConcurrency() throws SQLException {
		DatabaseMetaData dmd = getConn().getMetaData();
		boolean flag = false;
		if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 关闭
	 *
	 * @param pstmt
	 * @param rs
	 * @throws SQLException
	 */
	public void close(PreparedStatement pstmt, ResultSet rs)
			throws SQLException {
		DbUtils.close(rs);
		DbUtils.close(pstmt);
	}

	/**
	 * 立即关闭
	 *
	 * @param pstmt
	 * @param rs
	 * @throws SQLException
	 */
	public void closeQuietly(PreparedStatement pstmt, ResultSet rs)
			throws SQLException {
		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(pstmt);
	}

	/**
	 * 关闭连接
	 *
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		DbUtils.close(conn);
	}

	/**
	 * 立即关闭连接
	 *
	 * @throws SQLException
	 */
	public void closeQuietly() throws SQLException {
		DbUtils.closeQuietly(conn);
	}

	/**
	 * 获取被对象的数据库连接 如果要让该对象从新获取数据库连接 可使用datasource自身对象的方法getConnection()
	 * 
	 * @return
	 */
	public synchronized Connection getConn() {
        try {
            int commonTimeout = 150;

            //check the connection, if the connection is not suitable, then get the new connection and check it again
            while(null == conn || conn.isClosed() || !conn.isValid(commonTimeout)) {
                try {
                    if(null != conn && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    logger.error("关闭无效连接失败，"+e.getMessage(),e);
                }
                conn = dataSource.getConnection();
                this.prepareConn();
                logger.info("重新获取连接成功.");
            }

//            if (conn == null || conn.isClosed() || !conn.isValid(commonTimeout)) {
//                this.conn = super.getConnection();
//                this.prepareConn();
//            }
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
        }
        return conn;
	}

    public synchronized Connection getNewConn() {
        try {
            this.conn = super.getConnection();
            this.prepareConn();
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
        }
        return conn;
    }

	public DbConfig getDbConfig() {
		return dbConfig;
	}

	public void setDbConfig(DbConfig dbConfig) {
		this.dbConfig = dbConfig;
	}

}
