/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db.dao;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.encode.EncodeUtils;
import com.eryansky.common.utils.io.ClobUtil;
import com.eryansky.common.utils.io.StreamTool;
import com.google.common.collect.Lists;
import com.eryansky.core.db.TransactionManager;
import com.eryansky.core.db.meta.DiDataSource;
import com.eryansky.core.db.meta.DiDataSourceFactory;
import com.eryansky.core.db.vo.AttaPathVo;
import com.eryansky.core.db.vo.ColumnVo;
import com.eryansky.core.db.vo.DbConfig;
import com.eryansky.core.db.vo.Package;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC操作(元数据、抽取、存储/装载) DAO
 * User: 尔演&Eryan eryanwcp@gmail.com
 * Date: 13-12-26 下午1:36
 */
public class DiDao{

    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    private volatile boolean pmdKnownBroken = false;     

    protected DiDataSource dataSource;

    public DiDao() {
    }

    public DiDao(DbConfig dbDatasource) throws Exception {
        try {
            dataSource = DiDataSourceFactory.create(dbDatasource);
        } catch (Exception e) {
            throw e;
        }
    }
    
    

    public DiDao(DiDataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}
    
    
    public DiDao(DiDataSourceFactory diDataSourceFactory) {
		super();
		this.dataSource = diDataSourceFactory.getDiDataSource();
	}

	public DiDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DiDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 获取事务管理器
     *
     * @return 事务管理实例
     */
    public synchronized TransactionManager getTranManager() throws SQLException{
        return new TransactionManager(getDataSource().getConnection());
    }

    /**
     * 转换记录集列字符串值成Object值对象。
     *
     * @param columnType - 列值类型
     * @param strValue   - 列字符串值
     * @return Object - 列Object值
     * @throws com.eryansky.common.exception.SystemException
     *
     */
    protected Object convertResultSetValueToObject(int columnType, String strValue) throws SystemException {
        Object objValue = strValue;
        try {
            switch (columnType) {
                case Types.TINYINT:
                    objValue = new Byte(strValue);
                    break;
                case Types.SMALLINT:
                    objValue = new Short(strValue);
                    break;
                case Types.INTEGER:
                    objValue = new Integer(strValue);
                    break;
                case Types.BIGINT:
                    objValue = new Long(strValue);
                    break;
                case Types.REAL:
                    objValue = new Float(strValue);
                    break;
                case Types.NUMERIC:
                case Types.DOUBLE:
                case Types.FLOAT:
                    objValue = new Double(strValue);
                    break;
                case Types.DECIMAL:
                    objValue = new java.math.BigDecimal(strValue);
                    break;
                case Types.TIMESTAMP:
                    objValue = Timestamp.valueOf(strValue);
                    break;
                case Types.DATE:
                case Types.TIME:
                    SimpleDateFormat sdFormat = null;
                    if (strValue.length() == 8) {
                        sdFormat = new SimpleDateFormat("yyyyMMdd");
                    } else if (strValue.length() == 10 && strValue.indexOf("-") >= 0) {
                        sdFormat = new SimpleDateFormat("yyyy-MM-dd");
                    } else if (strValue.length() == 10 && strValue.indexOf("/") >= 0) {
                        sdFormat = new SimpleDateFormat("yyyy/MM/dd");
                    } else if (strValue.length() == 14) {
                        sdFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    } else if (strValue.length() == 19 && strValue.indexOf("/") >= 0) {
                        sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    } else if (strValue.length() == 19 && strValue.indexOf("-") >= 0) {
                        sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }
                    objValue = new Timestamp(sdFormat.parse(strValue).getTime());
                    break;
                case Types.LONGVARCHAR:
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                    break;
                case Types.BIT:
                case Types.BOOLEAN:
                    if ("true".equalsIgnoreCase(strValue)) {
                        objValue = new Boolean(true);
                    } else if ("false".equalsIgnoreCase(strValue)) {
                        objValue = new Boolean(false);
                    }
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                    objValue = strValue;
                    break;
                case Types.BLOB:
                    break;
                case Types.CLOB:
                    break;
                case Types.ARRAY:
                    break;
                case Types.NULL:
                    break;
                case Types.OTHER:
                    break;
                case Types.JAVA_OBJECT:
                    break;
                case Types.DISTINCT:
                    break;
                case Types.STRUCT:
                    break;
                case Types.REF:
                    break;
                case Types.DATALINK:
                    break;
            }
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage(), ex);
        }
        return objValue;
    }

    /**
     * 获取记录的Text内容.
     *
     * @param rs
     * @param columnIndex 列值类型
     * @param mapLob
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private String getResultSetClob(ResultSet rs, int columnIndex, Map<String, byte[]> mapLob) throws IOException, SQLException {
        String value = null;
        Clob clob = (Clob) rs.getClob(columnIndex);
        if (clob != null) {
            byte[] binaryBuffer = ClobUtil.getString(clob).getBytes();

            if (mapLob != null) {
                value = Identities.uuid2();
                mapLob.put(value, binaryBuffer);
            } else {
                value = EncodeUtils.base64Encode(binaryBuffer);
            }
        }
        return value;
    }

    /**
     * 获取记录的大二进制
     *
     * @param @param  rs
     * @param @param  columnIndex
     * @param @param  mapLob
     * @param @return
     * @param @throws SQLException
     * @param @throws IOException    设定文件
     * @return String    返回类型
     * @throws
     * @date 2014-1-13 下午8:02:51
     */
    public String getResultSetBinaryStream(ResultSet rs, int columnIndex, Map<String, byte[]> mapLob)
            throws SQLException, IOException {
        String value = null;
        InputStream is = rs.getBinaryStream(columnIndex);
        if (is != null) {
            byte[] binaryBuffer = new byte[0];
            try {
                binaryBuffer = StreamTool.readInputStream(is);
            } catch (Exception e) {
                throw new IOException(e);
            }
            if (mapLob != null) {
                value = Identities.uuid();
                mapLob.put(value, binaryBuffer);
            } else {
                value = EncodeUtils.base64Encode(binaryBuffer);
            }
        }
        return value;
    }

    /**
     * 得到当前记录集列字符串值。
     *
     * @param rs          - 记录
     * @param columnIndex - 列索引
     * @param mapLob      - 大字段Map对象
     * @return String - 列字符串值
     * @throws com.eryansky.common.exception.DaoException
     *
     * @throws com.eryansky.common.exception.SystemException
     */
    protected String getCurrentResultSetValueString(ResultSet rs, int columnIndex, Map<String, byte[]> mapLob) throws DaoException, SystemException {
        String value = null;
        try {
            int index = 0;
            Timestamp ts = null;
            int type = rs.getMetaData().getColumnType(columnIndex);
            switch (type) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.FLOAT:
                case Types.REAL:
                case Types.DOUBLE:
                case Types.NUMERIC:
                case Types.DECIMAL:
                    value = rs.getString(columnIndex);
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                    value = rs.getString(columnIndex);
                    break;
                case Types.TIMESTAMP:
                    ts = rs.getTimestamp(columnIndex);
                    if (ts != null) {
                        value = ts.toString();
                        index = value.indexOf(".0");
                        if (index > 0) {
                            value = value.substring(0, index);
                        }
                        index = value.indexOf(" 00:00:00");
                        if (index > 0) {
                            value = value.substring(0, index);
                        }
                    }
                    break;
                case Types.TIME:
                case Types.DATE:
                    //Oracle Jdbc驱动的BUG，用rs.getDate()返回只有日期无时间。
                    //java.util.Date date = rs.getDate(columnIndex);
                    ts = rs.getTimestamp(columnIndex);
                    if (ts != null) {
                        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        value = sdFormat.format(ts);
                        index = value.indexOf(" 00:00:00");
                        if (index > 0) {
                            value = value.substring(0, index);
                        }
                    }
                    break;
                case Types.CLOB:            //CLOB
                case Types.LONGVARCHAR:
                    //新增CLOB抽取
                    value = getResultSetClob(rs, columnIndex, mapLob);
                    break;
                case Types.BLOB:            //BLOB
                case Types.BINARY:            //BLOB
                case Types.VARBINARY:        //BLOB
                case Types.LONGVARBINARY:    //BLOB
                    value = this.getResultSetBinaryStream(rs, columnIndex, mapLob);
                    break;
                case Types.ARRAY:
                    break;
                case Types.NULL:
                    break;
                case Types.OTHER:
                    break;
                case Types.JAVA_OBJECT:
                    break;
                case Types.DISTINCT:
                    break;
                case Types.STRUCT:
                    break;
                case Types.REF:
                    break;
                case Types.DATALINK:
                    break;
                case Types.BIT:
                case Types.BOOLEAN:
                    value = (rs.getBoolean(columnIndex) ? "TRUE" : "FALSE");
                    break;
                default: //XML_DATA_TYPE_STRING;
                    value = rs.getString(columnIndex);
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage(), ex);
        }
        return value;
    }


    /**
     * 得到记录集指定列的元数据信息。
     *
     * @param rsmd        - 记录
     * @param columnIndex - 列索引
     * @return String - 列字符串值
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    protected Map<String, String> getResultSetMetaDataColumn(ResultSetMetaData rsmd, int columnIndex, List<String> lstPrimaryKey, AttaPathVo attaPathVo) throws DaoException, SystemException {
        Map<String, String> mapField = new HashMap<String, String>();
        try {
            String tableName = rsmd.getTableName(columnIndex);
            String fieldName = rsmd.getColumnName(columnIndex);
            String tc = tableName + "."+fieldName;
            int fieldType = rsmd.getColumnType(columnIndex);
            //字段名称
            mapField.put(Package.ATTRIB_NAME, tc);
            //是否可为空
            if (ResultSetMetaData.columnNoNulls == rsmd.isNullable(columnIndex)) {
                mapField.put(Package.ATTRIB_ISNULL, "N");
            }
            //是否主键
            if (lstPrimaryKey != null && lstPrimaryKey.contains(tc)) {
                mapField.put(Package.ATTRIB_ISPK, "Y");
            }

            //是否是附件字段
            if (attaPathVo != null && attaPathVo.getLstAttaFieldName() != null
                    && attaPathVo.getLstAttaFieldName().contains(tc)) {
                mapField.put(Package.IS_ATTAPATH, "Y");
            }
            //是否是绝对路径字段
            if (attaPathVo != null && attaPathVo.getLstAbsolutePath() != null
                    && attaPathVo.getLstAbsolutePath().contains(tc)) {
                mapField.put(Package.IS_REAL_PATH, "Y");
            }


            //字段类型和长度
            switch (fieldType) {
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER:
                case Types.BIGINT:
                case Types.FLOAT:
                case Types.REAL:
                case Types.DOUBLE:
                case Types.NUMERIC:
                case Types.DECIMAL:
                    mapField.put(Package.ATTRIB_TYPE, Package.TYPE_NUMBER);
                    int numFieldLen = rsmd.getPrecision(columnIndex);
                    int numFieldScale = rsmd.getScale(columnIndex);
                    if (numFieldLen > 0) {
                        mapField.put(Package.ATTRIB_LENGTH, new Integer(numFieldLen).toString());
                    }
                    if (numFieldScale > 0) {
                        mapField.put(Package.ATTRIB_SCALE, new Integer(numFieldScale).toString());
                    }
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                    mapField.put(Package.ATTRIB_TYPE, Package.TYPE_STRING);
                    int strFieldLen = rsmd.getPrecision(columnIndex);
                    mapField.put(Package.ATTRIB_LENGTH, new Integer(strFieldLen).toString());
                    break;
                case Types.TIMESTAMP:
                    mapField.put(Package.ATTRIB_TYPE, Package.TYPE_TIMESTAMP);
                    break;
                case Types.TIME:
                case Types.DATE:
                    mapField.put(Package.ATTRIB_TYPE, Package.TYPE_DATE);
                    break;
                case Types.CLOB:
                case Types.LONGVARCHAR:
                    mapField.put(Package.ATTRIB_TYPE, Package.TYPE_CLOB);
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.BLOB:
                case Types.LONGVARBINARY:
                    mapField.put(Package.ATTRIB_TYPE, Package.TYPE_BLOB);
                    break;
                case Types.ARRAY:
                case Types.NULL:
                case Types.OTHER:
                case Types.JAVA_OBJECT:
                case Types.DISTINCT:
                case Types.STRUCT:
                case Types.REF:
                case Types.DATALINK:
                case Types.BIT:
                case Types.BOOLEAN:
                default:
                    mapField.put(Package.ATTRIB_TYPE, Package.TYPE_STRING);
                    int defFieldLen = rsmd.getPrecision(columnIndex);
                    if (defFieldLen > 0) {
                        mapField.put(Package.ATTRIB_LENGTH, new Integer(defFieldLen).toString());
                    }
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage(), ex);
        }
        return mapField;
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

    public long count(String tableName,String whereSQL,Object... params) throws DaoException {
        StringBuffer sb = null;
        sb = new StringBuffer();
        sb.append("select count(*) from ");
        sb.append(tableName);
        if (StringUtils.isNotBlank(whereSQL)) {
            sb.append(" where ").append(whereSQL);
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = getDataSource().getConn().prepareStatement(sb.toString());
            // check the parameter count, if we can
            ParameterMetaData pmd = null;
            if (!pmdKnownBroken) {		//支持oracle驱动的判断标志,如果是oracle数据库 这个变量要置为 true
                pmd = stmt.getParameterMetaData();
                int stmtCount = pmd.getParameterCount();
                int paramsCount = params == null ? 0 : params.length;
                if (stmtCount != paramsCount) {
                    throw new SQLException("Wrong number of parameters: expected "+ stmtCount + ", was given " + paramsCount);
                }
            }

            // nothing to do here
            if (params == null) {

            }else{
	            for (int i = 0; i < params.length; i++) {
	                if (params[i] != null) {
	                    stmt.setObject(i + 1, params[i]);
	                } else {
	                    // VARCHAR works with many drivers regardless
	                    // of the actual column type. Oddly, NULL and
	                    // OTHER don't work with Oracle's drivers.
	                    int sqlType = Types.VARCHAR;
	                    if (!pmdKnownBroken) {
	                        try {
	                            /*
	                             * It's not possible for pmdKnownBroken to change from
	                             * true to false, (once true, always true) so pmd cannot
	                             * be null here.
	                             */
	                            sqlType = pmd.getParameterType(i + 1);
	                        } catch (SQLException e) {
	                            pmdKnownBroken = true;
	                        }
	                    }
	                    stmt.setNull(i + 1, sqlType);
	                }
	            }
           }
		  rs = stmt.executeQuery();
		  Number total = (Number)scalarHandler.handle(rs);
		  return total.longValue();
        } catch (SQLException e) {
        	 String causeMessage = e.getMessage();
             if (causeMessage == null) {
                 causeMessage = "";
             }
             StringBuffer msg = new StringBuffer(causeMessage);
             msg.append(" Query: ");
             msg.append(sb.toString());
             msg.append(" Parameters: ");
             if (params == null) {
                 msg.append("[]");
             } else {
                 msg.append(Arrays.deepToString(params));
             }
             SQLException se = new SQLException(msg.toString(), e.getSQLState(),e.getErrorCode());
             se.setNextException(e);
             throw new DaoException(se);
        } finally {
            try {
               this.getDataSource().close(stmt, rs);
            } catch(SQLException se){
            	logger.error("",se);
            }finally {

            }
        }
    }


    /**
     * 根据物理表 得到表主键集合信息
     * @param tableName
     * @return 表名 + "." + 字段名
     * @throws SQLException
     */
    public List<String> getTPrimaryKeys(String tableName) throws SQLException {
        List<String>  tablePKeys = Lists.newArrayList();
        List<ColumnVo> pColumnVos = getDataSource().getPrimaryKeys(tableName);
        if (!Collections3.isEmpty(pColumnVos)) {
            for (ColumnVo pColumnVo : pColumnVos) {
                tablePKeys.add(tableName+"."+pColumnVo.getCode());
            }
        }
        return tablePKeys;
    }

    /**
     * 根据物理表 得到表主键集合信息
     * @param tableName
     * @return 字段名
     * @throws SQLException
     */
    public List<String> getPrimaryKeys(String tableName) throws SQLException {
        List<String>  tablePKeys = Lists.newArrayList();
        List<ColumnVo> pColumnVos = getDataSource().getPrimaryKeys(tableName);
        if (!Collections3.isEmpty(pColumnVos)) {
            for (ColumnVo pColumnVo : pColumnVos) {
                tablePKeys.add(pColumnVo.getCode());
            }
        }
        return tablePKeys;
    }


    /**
     * 得到表的所有元数据信息
     * @param tableName 表名称
     * @param fieldNames 字段名（表名称+"."+字段名） 多个之间以","分割
     * @return
     * @throws com.eryansky.common.exception.DaoException
     */
    public List<Map<String,String>> getMetaFields(String tableName,String fieldNames,AttaPathVo attaPathVo) throws DaoException {
        final String SELECT_ALL = "*";
        if (StringUtils.isBlank(fieldNames)) {
            fieldNames = SELECT_ALL;
//            logger.warn("参数[fieldNames]为空，自动转换为{}", new Object[]{SELECT_ALL});
        }

        List<Map<String, String>> metas = Lists.newArrayList();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            Connection conn = this.getDataSource().getConn();
            StringBuffer sb = new StringBuffer();
            sb.append("select ").append(fieldNames).append(" from ").append(tableName).append(" where 1=0");
            pstm = conn.prepareStatement(sb.toString(), ResultSet.CONCUR_READ_ONLY);
            rs = pstm.executeQuery();
            int columnCount = pstm.getMetaData().getColumnCount();
            List<String> pKeys = this.getTPrimaryKeys(tableName);
            for(int i=1;i<=columnCount;i++){
                Map<String, String> mapField = this.getResultSetMetaDataColumn(rs.getMetaData(), i, pKeys, attaPathVo);
                metas.add(mapField);
            }

        } catch (SQLException e) {
            throw new DaoException(e);
        }finally {
            try {
                this.getDataSource().close(pstm,rs);
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);
            }
        }

        return metas;
    }

}
