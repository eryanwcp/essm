/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db.meta;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.db.utils.DBType;
import com.eryansky.core.db.vo.ColumnVo;
import com.eryansky.core.db.vo.DbConfig;
import com.eryansky.core.db.vo.SchemaVo;
import com.eryansky.core.db.vo.TableVo;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MySQLDataSource extends DiDataSource {

	public MySQLDataSource() throws SQLException {
		super();
	}
	
	public MySQLDataSource(DbConfig dbConfig) throws SQLException {
		super(dbConfig);
	}

	@Override
	public DBType getDbType() throws SQLException {
		return DBType.MySQL;
	}

	@Override
	public String getSchema() throws SQLException {
		String schema;
        schema = getConn().getMetaData().getUserName();
        return schema;
	}

	@Override
	public List<SchemaVo> getSchemaVos() throws SQLException {
		ResultSet rs = null;
        List<SchemaVo> schemaVos = new ArrayList<SchemaVo>();
        DatabaseMetaData dmd = super.getConn().getMetaData();
        rs = dmd.getSchemas();
        while (rs.next()) {
            SchemaVo schemaVo = new SchemaVo();
            schemaVo.setCode(rs.getString(1));
            schemaVo.setName(rs.getString(1));
            schemaVos.add(schemaVo);
        }
        return schemaVos;
	}

	@Override
	public String getCatalog() throws SQLException {
		return getConn().getCatalog();
	}

	@Override
	public List<TableVo> getTableVos(String namePattern) throws SQLException {
		return getTableVos(getCatalog(),getSchema(), namePattern);
	}

	@Override
	public List<TableVo> getTableVos(String schemaPattern, String namePattern)
			throws SQLException {
		return  getTableVos(getCatalog(),schemaPattern, namePattern);
	}

	@Override
	public List<TableVo> getTableVos(String catalog, String schemaPattern,
			String namePattern) throws SQLException {
		List<TableVo> tableVos = new ArrayList<TableVo>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.getConn().getMetaData();// 获取数据库的MataData信息
            rs = dmd.getTables(catalog, schemaPattern, namePattern, DEFAULT_TYPES);
            while (rs.next()) {
                TableVo tableVo = new TableVo();
                tableVo.setCode(rs.getString("TABLE_NAME"));
                tableVo.setSchema(rs.getString("TABLE_SCHEM"));
                tableVo.setCatalog(rs.getString("TABLE_CAT"));
                tableVo.setTableType(rs.getString("TABLE_TYPE"));
                tableVo.setComment(rs.getString("REMARKS"));
                tableVos.add(tableVo);
            }

        } catch (SQLException e){
            throw e;
        } finally {
            super.close(null, rs);
        }
        return tableVos;
	}

	@Override
	public List<ColumnVo> getColumnVos(String namePattern) throws SQLException {
		return null;
	}

	@Override
	public List<ColumnVo> getColumnVos(String schemaPattern, String namePattern)
			throws SQLException {
		return getColumnVos(getCatalog(),schemaPattern, namePattern);
	}

	@Override
	public List<ColumnVo> getColumnVos(String catalog, String schemaPattern,
			String namePattern) throws SQLException {
        List<ColumnVo> columnVos = new ArrayList<ColumnVo>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.getConn().getMetaData();
            rs = dmd.getColumns(catalog, schemaPattern, namePattern, "%");
            List<ColumnVo> primaryKeys = getPrimaryKeys(catalog,schemaPattern ,namePattern);
            while (rs.next()) {
                ColumnVo col = new ColumnVo();
                col.setCode(rs.getString("COLUMN_NAME"));
                col.setName(col.getCode());
                col.setDataType(rs.getString("TYPE_NAME"));
                col.setDataLength(rs.getInt("COLUMN_SIZE"));
                col.setNotNull(!rs.getBoolean("NULLABLE"));
                col.setPrecision(rs.getInt("DECIMAL_DIGITS"));
                col.setScale(rs.getInt("NUM_PREC_RADIX"));
                String defaultValue = rs.getString("COLUMN_DEF");
                if(StringUtils.isNotBlank(defaultValue) && defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
                    defaultValue = defaultValue.substring(1,defaultValue.length()-1);
                }
                col.setDefaultValue(defaultValue);
                col.setComment(rs.getString("REMARKS"));

                //判断是否是主键
                for(ColumnVo primaryKey:primaryKeys) {
                    if(primaryKey.getCode().equalsIgnoreCase(col.getCode())){
                        col.setPrimaryKey(true);
                    }
                }
                columnVos.add(col);
            }
        } catch (SQLException e) {
            throw e;
        }finally {
            super.close(null, rs);
        }
        return columnVos;
	}

	@Override
	public List<ColumnVo> getPrimaryKeys(String namePattern)
			throws SQLException {
		return getPrimaryKeys(getCatalog(),getSchema(),  namePattern);
	}

	@Override
	public List<ColumnVo> getPrimaryKeys(String catalog, String schemaPattern,
			String namePattern) throws SQLException {
        List<ColumnVo> primaryKey = new ArrayList<ColumnVo>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.getConn().getMetaData();// 获取数据库的MataData信息
            rs = dmd.getPrimaryKeys(catalog,schemaPattern, namePattern);
            while (rs.next()) {
                ColumnVo pk = new ColumnVo();
                pk.setCode(rs.getString("COLUMN_NAME"));
                primaryKey.add(pk);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            super.close(null, rs);
        }
        return primaryKey;
	}

	@Override
	public List<ColumnVo> getForeignKeys(String namePattern)
			throws SQLException {
		return null;
	}

	@Override
	public String getPageSql(String sql, int offset, int limit)
			throws SQLException {

        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        pagingSelect.append(sql).append(" limit ?,?");
        return pagingSelect.toString();
	}

	@Override
	public void prepareConn() {

	}

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
