package com.eryansky.codegen.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.eryansky.codegen.util.DBType;
import com.eryansky.codegen.vo.Column;
import com.eryansky.codegen.vo.DbConfig;
import com.eryansky.codegen.vo.Table;

/**
 * 用于数据库操作。主要包括读取数据库表相关信息
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-7-15 下午1:38:27 
 * @version 1.0
 */
public abstract class DataSource {

	protected String[] DEFAULT_TYPES = new String[] { "TABLE" };
	protected Connection conn = null;
    protected DbConfig dbConfig;


    public DataSource(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
        DbConnection dbConnection = new DbConnection(dbConfig);
        this.conn = dbConnection.getConn();
    }

    /**
     * 数据库类型
     * @see
     */
    public abstract DBType getDbType() throws SQLException;

    /**
     * JDBC默认连接模式
     */
    public abstract String getSchema() throws SQLException;


    /**
     * JDBC默认连接目录
     */
    public abstract String getCatalog() throws SQLException;

	/**
	 * 根据表列信息
	 * 
	 * @param namePattern
	 * @return
	 * @throws java.sql.SQLException
	 */
	public abstract List<Column> getColumns( String namePattern) throws SQLException;

	public abstract List<Column> getPrimaryKeys(String namePattern) throws SQLException;

	public abstract List<Column> getForeignKeys( String namePattern) throws SQLException;

	/**
	 * 根据namePattern获取表名
	 * 
	 * @param namePattern
	 * @return
	 * @throws java.sql.SQLException
	 */
	public abstract List<Table> getTables(String namePattern) throws SQLException;

	protected void connectionTest(Connection conn) throws SQLException {
		if (conn == null)
			throw new SQLException(DataSource.class.getName() + ":>>>>数据库未连接！");
	}

	protected void close(Connection conn, PreparedStatement pstmt, ResultSet rs) throws SQLException {
		if (rs != null)
			rs.close();
		if (pstmt != null)
			pstmt.close();
		if (conn != null)
			conn.close();
	}

	protected void close(PreparedStatement pstmt, ResultSet rs) throws SQLException {
		if (rs != null)
			rs.close();
		if (pstmt != null)
			pstmt.close();
	}

	public void close() throws SQLException {
		if (conn != null)
			conn.close();
	}

    /**
     * 获取新连接
     * @return
     */
    public Connection getNewConn() {
        return new DbConnection(dbConfig).getConn();
    }

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

    public DbConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

}
