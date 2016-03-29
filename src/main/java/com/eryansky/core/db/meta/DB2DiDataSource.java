/**
 *  Copyright (c) 2013 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.meta;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.db.utils.DBType;
import com.eryansky.core.db.vo.ColumnVo;
import com.eryansky.core.db.vo.DbConfig;
import com.eryansky.core.db.vo.SchemaVo;
import com.eryansky.core.db.vo.TableVo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * DB2数据源
 * User: 尔演&Eryan eryanwcp@gmail.com
 * Date: 13-12-26 上午11:19
 */
public class DB2DiDataSource extends DiDataSource {

    public DB2DiDataSource(DbConfig dbConfig) throws SQLException {
        super(dbConfig);
    }

    public DBType getDbType() throws SQLException{
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
                return rs.getString(1);
            }

        } catch (SQLException e){
            throw e;
        }finally {
            super.close(preparedStatement, rs);
        }
        return null;
    }

    @Override
    public List<SchemaVo> getSchemaVos() throws SQLException {
        ResultSet rs = null;
        List<SchemaVo> schemaVos = new ArrayList<SchemaVo>();
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();
            rs = dmd.getSchemas();
            while (rs.next()) {
                SchemaVo schemaVo = new SchemaVo();
                schemaVo.setName(rs.getString(1));
                schemaVo.setCode(rs.getString(1));
                schemaVos.add(schemaVo);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            throw e;
        }

        return schemaVos;
    }

    @Override
    public String getCatalog() throws SQLException {
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();
            return dmd.getConnection().getCatalog();
        } catch (SQLException e) {
//            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<TableVo> getTableVos(String namePattern) throws SQLException {
         return getTableVos(getSchema(), namePattern);
    }

    @Override
    public List<TableVo> getTableVos(String schemaPattern, String namePattern) throws SQLException {
         return getTableVos(getCatalog(),schemaPattern,  namePattern);
    }

    @Override
    public List<TableVo> getTableVos(String catalog,String schemaPattern,  String namePattern) throws SQLException {
        List<TableVo> tableVos = new ArrayList<TableVo>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();// 获取数据库的MataData信息
            rs = dmd.getTables(catalog,schemaPattern, namePattern, DEFAULT_TYPES);
            while (rs.next()) {
                TableVo tableVo = new TableVo();
                tableVo.setCode(rs.getString("TABLE_NAME"));
                tableVo.setSchema(rs.getString("TABLE_SCHEM"));
                tableVo.setCatalog(rs.getString("TABLE_CAT"));
                tableVo.setTableType(rs.getString("TABLE_TYPE"));
                tableVo.setComment(rs.getString("REMARKS"));
                tableVos.add(tableVo);
                // System.out.println(rs.getString("TABLE_CAT") + "\t"
                // + rs.getString("TABLE_SCHEM") + "\t"
                // + rs.getString("TABLE_NAME") + "\t"
                // + rs.getString("TABLE_TYPE"));

            }

        } catch (SQLException e){
            throw e;
        }finally {
            super.close(null, rs);
        }
        return tableVos;
    }

    @Override
    public List<ColumnVo> getColumnVos(String namePattern) throws SQLException {
       return getColumnVos(getSchema(), namePattern);
    }

    @Override
    public List<ColumnVo> getColumnVos(String schemaPattern, String namePattern) throws SQLException {
        return getColumnVos(getCatalog(),schemaPattern,  namePattern);
    }

    @Override
    public List<ColumnVo> getColumnVos(String catalog, String schemaPattern, String namePattern) throws SQLException {
        List<ColumnVo> columnVos = new ArrayList<ColumnVo>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();
            rs = dmd.getColumns(catalog, schemaPattern, namePattern, "%");
            List<ColumnVo> primaryKeys = getPrimaryKeys(catalog,schemaPattern,  namePattern);
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
        return columnVos;
    }

    /**
     * 获取主键
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("unused")
    private List<ColumnVo> getPks(ResultSet rs) throws SQLException {
        List<ColumnVo> pks = new ArrayList<ColumnVo>();
        while (rs.next()) {
            ColumnVo pk = new ColumnVo();
            pk.setCode(rs.getString("COLUMN_NAME"));
            pks.add(pk);
        }
        return pks;
    }



    @Override
    public List<ColumnVo> getForeignKeys(String namePattern) throws SQLException {
        return null;
    }

    @Override
    public String getPageSql(String sql,int offset, int limit) throws SQLException {
        sql = sql.trim();
        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);

        pagingSelect
                .append("SELECT * FROM (SELECT PAGE_B.*, ROWNUMBER() OVER() AS RN FROM ( ");

        pagingSelect.append(sql);

//        pagingSelect.append(" ) AS PAGE_B )AS PAGE_A WHERE PAGE_A.RN BETWEEN ").append(offset).append(" AND ").append(offset + limit - 1);
        pagingSelect.append(" ) AS PAGE_B )AS PAGE_A WHERE PAGE_A.RN BETWEEN ? AND ?");

        return pagingSelect.toString();
    }

    @Override
    public List<ColumnVo> getPrimaryKeys(String namePattern) throws SQLException {
        return getPrimaryKeys(getCatalog(),getSchema() , namePattern);
    }

    @Override
    public List<ColumnVo> getPrimaryKeys(String catalog,String schemaPattern,  String namePattern) throws SQLException {
        List<ColumnVo> primaryKey = new ArrayList<ColumnVo>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();// 获取数据库的MataData信息
            rs = dmd.getPrimaryKeys(catalog, schemaPattern, namePattern);
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

    public void prepareConn() {
        Connection conn= super.conn;
        try {
            if (StringUtils.isNotBlank(super.getDbConfig().getSchema())) {
                Statement stamt = conn.createStatement();
                stamt.execute("SET CURRENT SCHEMA = " + super.getDbConfig().getSchema().trim());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
