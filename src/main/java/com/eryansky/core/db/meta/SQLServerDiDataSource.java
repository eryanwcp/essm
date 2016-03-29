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
 * SQL Server数据源
 * User: 尔演&Eryan eryanwcp@gmail.com
 * Date: 13-12-26 上午11:19
 */
public class SQLServerDiDataSource extends DiDataSource{


    public SQLServerDiDataSource(DbConfig dbConfig) throws SQLException {
        super(dbConfig);
    }

    public DBType getDbType() throws SQLException{
        return DBType.mssql;
    }

    /**
     * 获取模式当前连接默认模式名Schema 默认返回 “dbo”
     * @return
     * @throws SQLException
     */
    @Override
    public String getSchema() throws SQLException{
        String schema = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();// 获取数据库的MataData信息
            //U:表 V：视图
            preparedStatement = conn.prepareStatement("SELECT TOP (1) sys.schemas.name from　 sys.objects,sys.schemas " +
                    "WHERE (sys.objects.type = 'U' OR sys.objects.type = 'V') and　 sys.objects.schema_id = sys.schemas.schema_id");
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                schema =  rs.getString(1);
            }

        } catch (SQLException e){
            throw e;
        }finally {
            super.close(preparedStatement, rs);
        }
        if(StringUtils.isNotBlank(schema)){
            return schema;
        }
        //默认返回 “dbo”
        return "dbo";
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
                schemaVo.setCode(rs.getString(1));
                schemaVo.setName(rs.getString(1));
                schemaVos.add(schemaVo);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            throw e;
        }

        return schemaVos;
    }

    @Override
    public String getCatalog() throws SQLException{
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
       return getTableVos(getCatalog(),getSchema(), namePattern);
    }

    @Override
    public List<TableVo> getTableVos(String schemaPattern, String namePattern) throws SQLException {
        return  getTableVos(getCatalog(),schemaPattern, namePattern);
    }

    @Override
    public List<TableVo> getTableVos(String catalog, String schemaPattern, String namePattern) throws SQLException {
        List<TableVo> tableVos = new ArrayList<TableVo>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();// 获取数据库的MataData信息
            rs = dmd.getTables(catalog, schemaPattern, namePattern, DEFAULT_TYPES);
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
        } finally {
            super.close(null, rs);
        }
        return tableVos;
    }

    @Override
    public List<ColumnVo> getColumnVos(String namePattern) throws SQLException {
        return getColumnVos(getCatalog(),getSchema(), namePattern);
    }

    @Override
    public List<ColumnVo> getColumnVos(String schemaPattern, String namePattern) throws SQLException {
        return getPrimaryKeys(getCatalog(),schemaPattern, namePattern);
    }

    @Override
    public List<ColumnVo> getColumnVos(String catalog,String schemaPattern,  String namePattern) throws SQLException {
        List<ColumnVo> columnVos = new ArrayList<ColumnVo>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = super.conn.getMetaData();
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
        boolean isForUpdate = false;
        if (sql.toLowerCase().endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }
        boolean hasOffset = false;
        if(offset > 0){
            hasOffset = true;
        }

        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        String sqlOrderExp="order by (select 0)";//针对没有排序的
//        if (sqlOrder!=null&&!"".equals(sqlOrder.trim())){
//            sqlOrderExp="Order by "+sqlOrder;
//        }

        if (hasOffset) {
            pagingSelect.append("select * from ( select *, Row_Number() over("+sqlOrderExp+") as RowId from ( ");
        } else {
            pagingSelect.append("select top ? * from ( ");
        }
        pagingSelect.append(sql);
        if (hasOffset) {
            pagingSelect.append(" ) t) row_ where row_.RowId between  ? and  ?");
        } else {
            pagingSelect.append(" ) t");
        }

        if (isForUpdate) {
            pagingSelect.append(" with(rowlock)");
        }

        return pagingSelect.toString();
    }

    @Override
    public void prepareConn() {
       Connection conn= super.conn;
        try {
            if (StringUtils.isNotBlank(super.getDbConfig().getSchema())) {
                Statement stamt = conn.createStatement();
//                1)ALTER USER user WITH DEFAULT_SCHEMA =dbo ;
//                2)ALTER schema dbo TRANSFER schema.table
//                3)EXECUTE AS USER = 'lc0019999';
                stamt.execute("EXECUTE AS USER = '" + super.getDbConfig().getSchema().trim()+"'");
//                stamt.execute("ALTER USER "+super.getDbDatasource().getUsername()+" WITH DEFAULT_SCHEMA = " + super.getDbDatasource().getSchema().trim());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ColumnVo> getPrimaryKeys(String namePattern) throws SQLException {
        return getPrimaryKeys(getSchema(), getCatalog(), namePattern);
    }

    @Override
    public List<ColumnVo> getPrimaryKeys(String catalog, String schemaPattern, String namePattern) throws SQLException {
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
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
