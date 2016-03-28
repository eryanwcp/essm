/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package com.eryansky.codegen.db;

import com.eryansky.codegen.util.DBType;
import com.eryansky.codegen.vo.Column;
import com.eryansky.codegen.vo.DbConfig;
import com.eryansky.codegen.vo.Table;
import com.eryansky.common.utils.StringUtils;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-05-22 22:35
 */
public class DB2DataSource extends DataSource {
	private final static String DB2_YES_VALUE = "YES";
	private final static String DB2_NO_VALUE = "NO";

    public DB2DataSource(DbConfig dbConfig) {
        super(dbConfig);
    }

    @Override
    public DBType getDbType() throws SQLException {
        return DBType.db2;
    }

    @Override
    public String getSchema() throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();// 获取数据库的MataData信息
            preparedStatement = conn.prepareStatement("select current schema from sysibm.sysdummy1");
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
				return rs.getString(1).trim();
            }

        } catch (SQLException e){
            throw e;
        }finally {
            super.close(preparedStatement, rs);
        }
        return null;
    }

    @Override
    public String getCatalog() throws SQLException {
        return getConn().getCatalog();
    }

    @Override
    public List<Column> getColumns(String namePattern) throws SQLException {
        List<Column> columns = new ArrayList<Column>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();
            rs = dmd.getColumns(getCatalog(), getSchema(), namePattern, "%");
            List<Column> primaryKeys = getPrimaryKeys(namePattern);
            while (rs.next()) {
                Column col = new Column();
                col.setColumnName(rs.getString("COLUMN_NAME"));
                col.setJdbcType(rs.getString("TYPE_NAME"));
                col.setLength(rs.getInt("COLUMN_SIZE"));
                col.setNullable(rs.getBoolean("NULLABLE"));
                col.setDigits(rs.getInt("DECIMAL_DIGITS"));
                String defaultValue = rs.getString("COLUMN_DEF");
                if(StringUtils.isNotBlank(defaultValue) && defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
                    defaultValue = defaultValue.substring(1,defaultValue.length()-1);
                }
                /**
                 * 指示此列是否是自动递增
                 * YES -- 该列是自动递增的
                 * NO -- 该列不是自动递增
                 * 空字串--- 不能确定该列是否自动递增
                 */
                String autoIncrement = rs.getString("IS_AUTOINCREMENT");
				if (DB2_YES_VALUE.equalsIgnoreCase(autoIncrement)) {
					col.setAutoIncrement(true);
				} else if (DB2_NO_VALUE.equalsIgnoreCase(autoIncrement)) {
					col.setAutoIncrement(false);
				}
                col.setDefaultValue(defaultValue);
                col.setComment(rs.getString("REMARKS"));

                //判断是否是主键
                for(Column primaryKey:primaryKeys) {
                    if(primaryKey.getColumnName().equalsIgnoreCase(col.getColumnName())){
                        col.setPrimaryKey(true);
                    }
                }

                columns.add(col);

                /** 所有的列信息。如下
                 *  TABLE_CAT String => 表类别（可为 null）
                 TABLE_SCHEM String => 表模式（可为 null）
                 TABLE_NAME String => 表名称
                 COLUMN_NAME String => 列名称
                 DATA_TYPE int => 来自 java.sql.Types 的 SQL 类型
                 TYPE_NAME String => 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
                 COLUMN_SIZE int => 列的大小。
                 BUFFER_LENGTH 未被使用。
                 DECIMAL_DIGITS int => 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null。
                 NUM_PREC_RADIX int => 基数（通常为 10 或 2）
                 NULLABLE int => 是否允许使用 NULL。
                 columnNoNulls - 可能不允许使用 NULL 值
                 columnNullable - 明确允许使用 NULL 值
                 columnNullableUnknown - 不知道是否可使用 null
                 REMARKS String => 描述列的注释（可为 null）
                 COLUMN_DEF String => 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
                 SQL_DATA_TYPE int => 未使用
                 SQL_DATETIME_SUB int => 未使用
                 CHAR_OCTET_LENGTH int => 对于 char 类型，该长度是列中的最大字节数
                 ORDINAL_POSITION int => 表中的列的索引（从 1 开始）
                 IS_NULLABLE String => ISO 规则用于确定列是否包括 null。
                 YES --- 如果参数可以包括 NULL
                 NO --- 如果参数不可以包括 NULL
                 空字符串 --- 如果不知道参数是否可以包括 null
                 SCOPE_CATLOG String => 表的类别，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
                 SCOPE_SCHEMA String => 表的模式，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
                 SCOPE_TABLE String => 表名称，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
                 SOURCE_DATA_TYPE short => 不同类型或用户生成 Ref 类型、来自 java.sql.Types 的 SQL 类型的源类型（如果 DATA_TYPE 不是 DISTINCT 或用户生成的 REF，则为 null）
                 IS_AUTOINCREMENT String => 指示此列是否自动增加
                 YES --- 如果该列自动增加
                 NO --- 如果该列不自动增加
                 空字符串 --- 如果不能确定该列是否是自动增加参数

                 */
            }
        } catch (SQLException e) {
            throw e;
        }finally {
            super.close(null, rs);
        }
        return columns;
    }

    @Override
    public List<Column> getPrimaryKeys(String namePattern) throws SQLException {
        List<Column> primaryKeys = new ArrayList<Column>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = getConn().getMetaData();// 获取数据库的MataData信息
            rs = dmd.getPrimaryKeys(getCatalog(), getSchema(), namePattern);
            while (rs.next()) {
                Column pk = new Column();
                pk.setColumnName(rs.getString("COLUMN_NAME"));
                primaryKeys.add(pk);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            super.close(null, rs);
        }
        return primaryKeys;
    }

    @Override
    public List<Column> getForeignKeys(String namePattern) throws SQLException {
        return null;
    }

    @Override
    public List<Table> getTables(String namePattern) throws SQLException {
        List<Table> tables = new ArrayList<Table>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = getConn().getMetaData();// 获取数据库的MataData信息
            rs = dmd.getTables(getCatalog(),getSchema(), namePattern, DEFAULT_TYPES);
            while (rs.next()) {
                Table table = new Table();
                table.setTableName(rs.getString("TABLE_NAME"));
                table.setSchema(rs.getString("TABLE_SCHEM"));
                table.setCatalog(rs.getString("TABLE_CAT"));
                table.setTableType(rs.getString("TABLE_TYPE"));
                table.setRemark(rs.getString("REMARKS"));
                tables.add(table);
            }

        } catch (SQLException e){
            throw e;
        }finally {
            super.close(null, rs);
        }
        return tables;
    }
}
