/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.dao;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.collections.MapUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.db.exception.ConstraintViolationException;
import com.eryansky.core.db.meta.DiDataSource;
import com.eryansky.core.db.meta.DiDataSourceFactory;
import com.eryansky.core.db.utils.DBType;
import com.eryansky.core.db.utils.LoadType;
import com.eryansky.core.db.utils.OSinfo;
import com.eryansky.core.db.vo.DbConfig;
import com.eryansky.core.db.vo.Package;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ETL - 存储DAO
 * Author: 温春平 wencp@jx.tobacco.gov.cn
 * Date: 2014-02-14 15:15
 */
//@Repository
public class LoadDao extends DiDao {

	public LoadDao(){
    }
	
    /**
     * @param loadDbDatasource 抽取数据源VO
     * @throws Exception
     */
    public LoadDao(DbConfig loadDbDatasource) throws Exception {
        super(loadDbDatasource);
    }
    
    public LoadDao(DiDataSource dataSource) {
		super(dataSource);
	}
    
    
    public LoadDao(DiDataSourceFactory diDataSourceFactory) {
		super(diDataSourceFactory);
	}

    /**
     * 生成表主键名称及对应的主键值对象。
     *
     * @param tableName     表名
     * @param mapRow        行记录Map对象
     * @param lstPrimaryKey PrimaryKey名称列表
     * @param withTable 返回值是否包含表名
     * @return 主键名称及对应的主键值Map对象
     */
    private Map<String, Object> generateTablePrimaryKeyValue(String tableName,
                                                             Map<String, String> mapRow, List<String> lstPrimaryKey,boolean withTable)
            throws DaoException, SystemException {
        Map<String, Object> mapPrimaryKeyValue = new HashMap<String, Object>(
                lstPrimaryKey.size());
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // 生成SQL语句
            StringBuffer sbSQL = new StringBuffer(512);
            sbSQL.append("select ");
            // 如果没有指定主键，则从数据库中获取
            if (Collections3.isEmpty(lstPrimaryKey)) {
                lstPrimaryKey = super.getTPrimaryKeys(tableName);
            }
            if (Collections3.isEmpty(lstPrimaryKey)) {
                throw new SystemException("未指定表[" + tableName + "]主键，并且物理表中不存在主键.");
            }
            boolean bFieldAppend = false;
            for (Iterator<String> iter = lstPrimaryKey.iterator(); iter
                    .hasNext(); ) {
                String fieldName = iter.next();
                if (bFieldAppend) {
                    sbSQL.append(",");
                }
                sbSQL.append(fieldName);
                bFieldAppend = true;
            }
            sbSQL.append(" from ").append(tableName).append(" where 1=0");
            // 打开空记录集
            String sql = sbSQL.toString();
            if(logger.isDebugEnabled()){
                logger.debug(sql);
            }
            Connection conn = super.getDataSource().getConn();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            // 生成PrimaryKeys值对象并放入mapPrimaryKey容器
            for (int columnIndex = 1; columnIndex <= rs.getMetaData()
                    .getColumnCount(); columnIndex++) {
                int columnType = rs.getMetaData().getColumnType(columnIndex);
                String tName = rs.getMetaData().getTableName(columnIndex);
                String fieldName = rs.getMetaData().getColumnName(columnIndex);
                String tf = null;
                if(withTable){
                    tf = tName+"."+fieldName;
                }else{
                    tf = fieldName;
                }

                String fieldValue = mapRow.get(tf);
                if (fieldValue != null) {
                    Object objValue = super.convertResultSetValueToObject(
                            columnType, fieldValue);
                    mapPrimaryKeyValue.put(tf, objValue);
                }
            }
            // 判断数据库表的PrimaryKeys和rowMap中PrimaryKeys是否一致
            if (lstPrimaryKey.size() != mapPrimaryKeyValue.size()) {
                throw new SystemException("数据项与数据库表的主键字段不一致。");
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage(), ex);
        } finally {
            try {
                super.getDataSource().close(ps, rs);
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        return mapPrimaryKeyValue;
    }

    /**
     * 设置当前记录集列值。
     *
     * @param rs     记录集
     * @param mapRow 行记录Map对象
     * @param mapLob 行记录中用到的Lob值Map对象
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    private void setCurrentResultSetValues(ResultSet rs,
                                           Map<String, String> mapRow, Map<String, byte[]> mapLob,
                                           List<Map<String, String>> lstField, Map<String, String> realPath)
            throws DaoException, SystemException {
        try {
            int columnCount = rs.getMetaData().getColumnCount();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                int columnType = rs.getMetaData().getColumnType(columnIndex);
                String columnName = rs.getMetaData().getColumnName(columnIndex);
                String tName = rs.getMetaData().getTableName(columnIndex);
                String tc = tName +"."+columnName;
                String mapValue = mapRow.get(tc);
                if (mapValue == null) {
                    rs.updateNull(columnIndex);
                    continue;
                }
                String fieldType = null;
                if (lstField != null) {
                    for (Map<String, String> mapField : lstField) {
                        String fieldName = mapField.get(Package.ATTRIB_NAME);
                        // 是否是附件字段
                        if (mapField.containsKey(Package.IS_ATTAPATH)) {
                            // 如果是附件字段列
                            if (tc.equals(fieldName)) {
                                String url = null;// 存储路径
                                // 如果是绝对路径，则更新值为目标端所设置的值
                                if (mapField.containsKey(Package.IS_REAL_PATH)) {
                                    // 更新字段值
                                    String fileName = null;
                                    if (mapValue.contains("/")) {
                                        fileName = mapValue.substring(mapValue.lastIndexOf("/") + 1);
                                    } else {
                                        fileName = mapValue.substring(mapValue.lastIndexOf("\\") + 1);
                                    }
                                    mapValue = realPath.get(tc) + Identities.uuid() + "_" + fileName;
                                    url = mapValue;
                                } else {
                                    // 判断当前系统,相对路径字符转换
                                    if (mapValue.contains("/")) {
                                        // 路径转成win分隔符
                                        if (OSinfo.isWindows()) {
                                            mapValue = mapValue.replaceAll("/", "\\\\");
                                        }
                                    } else {
                                        // 路径转成linux分隔符
                                        if (OSinfo.isLinux() || OSinfo.isAix()) {
                                            mapValue = mapValue.replaceAll("\\\\", "/");
                                        }
                                    }

                                    url = realPath.get(tc) + mapValue;
                                }

                                // 把附件还原文件
                                String virtualAttaField = tc + Package.ATTA_SUFFIX;// 目标虚拟附件列名
                                String sid = mapRow.get(virtualAttaField);// 文件标识
                                url = url.trim();
                                if (mapLob != null && mapLob.size() > 0) {
                                    byte[] binaryLob = mapLob.get(sid);
                                    this.writeFile(binaryLob, url);// 写入文件
                                }

                            }

                        }

                        if (tc.equals(fieldName)) {
                            fieldType = mapField.get(Package.ATTRIB_TYPE);
                            break;
                        }

                    }
                }
                switch (columnType) {
                    case Types.TINYINT: // XML_DATA_TYPE_NUMBER;
                        if (mapValue.length() > 0) {
                            Byte bvalue = new Byte(mapValue);
                            rs.updateByte(columnIndex, bvalue.byteValue());
                        } else {
                            rs.updateNull(columnIndex);
                        }
                        break;
                    case Types.SMALLINT: // XML_DATA_TYPE_NUMBER;
                        if (mapValue.length() > 0) {
                            Short svalue = new Short(mapValue);
                            rs.updateShort(columnIndex, svalue.shortValue());
                        } else {
                            rs.updateNull(columnIndex);
                        }
                        break;
                    case Types.INTEGER: // XML_DATA_TYPE_NUMBER;
                        if (mapValue.length() > 0) {
                            Integer ivalue = new Integer(mapValue);
                            rs.updateInt(columnIndex, ivalue.intValue());
                        } else {
                            rs.updateNull(columnIndex);
                        }
                        break;
                    case Types.BIGINT: // XML_DATA_TYPE_NUMBER;
                        if (mapValue.length() > 0) {
                            Long lvalue = new Long(mapValue);
                            rs.updateLong(columnIndex, lvalue.longValue());
                        } else {
                            rs.updateNull(columnIndex);
                        }
                        break;
                    case Types.REAL: // XML_DATA_TYPE_NUMBER;
                        if (mapValue.length() > 0) {
                            Float fvalue = new Float(mapValue);
                            rs.updateFloat(columnIndex, fvalue.floatValue());
                        } else {
                            rs.updateNull(columnIndex);
                        }
                        break;
                    case Types.NUMERIC: // XML_DATA_TYPE_NUMBER;
                    case Types.DOUBLE: // XML_DATA_TYPE_NUMBER;
                    case Types.FLOAT: // XML_DATA_TYPE_NUMBER;
                        if (mapValue.length() > 0) {
                            Double dvalue = new Double(mapValue);
                            rs.updateDouble(columnIndex, dvalue.doubleValue());
                        } else {
                            rs.updateNull(columnIndex);
                        }
                        break;
                    case Types.DECIMAL: // XML_DATA_TYPE_NUMBER;
                        if (mapValue.length() > 0) {
                            java.math.BigDecimal bdvalue = new java.math.BigDecimal(mapValue);
                            rs.updateBigDecimal(columnIndex, bdvalue);
                        } else {
                            rs.updateNull(columnIndex);
                        }
                        break;
                    case Types.TIME: // XML_DATA_TYPE_DATE;
                        if (mapValue.length() > 0) {
                            SimpleDateFormat tsdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Time tdate = new Time(tsdFormat.parse(mapValue).getTime());
                            rs.updateTime(columnIndex, tdate);
                        } else {
                            rs.updateNull(columnIndex);
                        }
                        break;
                    case Types.TIMESTAMP: // XML_DATA_TYPE_DATE;
                    case Types.DATE: // XML_DATA_TYPE_DATE;
                        if (mapValue.length() == 0) {
                            rs.updateNull(columnIndex);
                        } else if (mapValue.length() == 8) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
                            Date date = new Date(sdFormat.parse(mapValue).getTime());
                            rs.updateDate(columnIndex, date);
                        } else if (mapValue.length() == 10) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date(sdFormat.parse(mapValue).getTime());
                            rs.updateDate(columnIndex, date);
                        } else if (mapValue.length() == 13) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        }else if (mapValue.length() == 14) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        } else if (mapValue.length() == 16) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        } else if (mapValue.length() == 19) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        } else if (mapValue.length() == 21) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        } else if (mapValue.length() == 22) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        } else if (mapValue.length() == 23) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        } else if (mapValue.length() == 24) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        }else if (mapValue.length() == 25) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        }else if (mapValue.length() == 26) {
                            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                            Timestamp date = new Timestamp(sdFormat.parse(mapValue).getTime());
                            rs.updateTimestamp(columnIndex, date);
                        } else {
                            throw new SystemException("列 " + tc + " 的值 "
                                    + mapValue + " 为不可认识的日期时间格式。");
                        }
                        break;
                    case Types.LONGVARCHAR: // XML_DATA_TYPE_BLOB;
                    case Types.CLOB:
                    case Types.BINARY: // XML_DATA_TYPE_BLOB;
                    case Types.VARBINARY: // XML_DATA_TYPE_BLOB;
                    case Types.LONGVARBINARY: // XML_DATA_TYPE_BLOB;
                    case Types.BLOB:
                        if (Package.TYPE_BLOB.equals(fieldType)
                                || Package.TYPE_CLOB.equals(fieldType)) {
                            if (mapLob != null && mapLob.size() > 0) {
                                byte[] binaryLob = mapLob.get(mapValue);
                                if (binaryLob != null && binaryLob.length > 0) {
                                    ByteArrayInputStream bais = new ByteArrayInputStream(binaryLob);

                                    // 新增CLOB处理
                                    if (Package.TYPE_CLOB.equals(fieldType)) {
                                        BufferedReader buf = new BufferedReader(new InputStreamReader(bais));
                                        rs.updateCharacterStream(columnIndex, buf, binaryLob.length);
                                    } else {
                                        rs.updateBinaryStream(columnIndex, bais, binaryLob.length);
                                    }
                                }
                            } else {
                                byte binaryBlob[] = Base64.decodeBase64(mapValue.getBytes());
                                ByteArrayInputStream bais = new ByteArrayInputStream(binaryBlob);
                                rs.updateBinaryStream(columnIndex, bais, binaryBlob.length);
                            }
                        } else {
                            byte binaryLob[] = mapValue.getBytes("utf-8");
                            ByteArrayInputStream bais = new ByteArrayInputStream(binaryLob);
                            rs.updateBinaryStream(columnIndex, bais, binaryLob.length);
                        }
                        break;
                    case Types.BIT: // XML_DATA_TYPE_NUMBER;
                    case Types.BOOLEAN: // XML_DATA_TYPE_STRING;
                        if ("TRUE".equals(mapValue.toUpperCase())) {
                            rs.updateBoolean(columnIndex, true);
                        } else if ("FALSE".equals(mapValue.toUpperCase())) {
                            rs.updateBoolean(columnIndex, false);
                        }
                        break;
                    case Types.CHAR:
                    case Types.VARCHAR:
                        rs.updateString(columnIndex, mapValue);
                        break;
                    case Types.ARRAY: // XML_DATA_TYPE_BLOB;
                        break;
                    case Types.NULL: // XML_DATA_TYPE_STRING;
                        break;
                    case Types.OTHER: // XML_DATA_TYPE_STRING;
                        break;
                    case Types.JAVA_OBJECT: // XML_DATA_TYPE_STRING;
                        break;
                    case Types.DISTINCT: // XML_DATA_TYPE_STRING;
                        break;
                    case Types.STRUCT: // XML_DATA_TYPE_STRING;
                        break;
                    case Types.REF: // XML_DATA_TYPE_STRING;
                        break;
                    case Types.DATALINK: // XML_DATA_TYPE_STRING;
                        break;
                }
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage(), ex);
        }
    }

    /**
     * 新增行记录.
     *
     * @param tableName     表名
     * @param lstField      字段名列表
     * @param mapRow        行记录Map对象
     * @param mapLob        行记录中用到的Lob值Map对象
     * @param lstPrimaryKey PrimaryKey名称列表
     * @param bThrowConstraintViolationException
     *                      是否抛出数据约束插入失败异常
     * @param realPath      附件路径
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws ConstraintViolationException
     * @throws SQLException
     */
    public Map<String, String> insertRow(String tableName,
                                         List<Map<String, String>> lstField, Map<String, String> mapRow,
                                         Map<String, byte[]> mapLob, List<String> lstPrimaryKey,
                                         boolean bThrowConstraintViolationException,
                                         Map<String, String> realPath,Connection connection)
            throws DaoException, SystemException, ConstraintViolationException {
        List<Map<String, String>> mapRows = Lists.newArrayList();
        mapRows.add(mapRow);
        List<Map<String, String>> list = this.insertRow(tableName, lstField, mapRows, mapLob, lstPrimaryKey, bThrowConstraintViolationException, realPath,connection);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * @param tableName     表名
     * @param lstField      字段名列表
     * @param mapRows       行记录Map对象集合
     * @param mapLob        行记录中用到的Lob值Map对象
     * @param lstPrimaryKey PrimaryKey名称列表
     * @param bThrowConstraintViolationException
     *                      是否抛出数据约束插入失败异常
     * @param realPath      附件路径
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws ConstraintViolationException
     */
    public List<Map<String, String>> insertRow(String tableName,
                                               List<Map<String, String>> lstField, List<Map<String, String>> mapRows,
                                               Map<String, byte[]> mapLob, List<String> lstPrimaryKey,
                                               boolean bThrowConstraintViolationException,
                                               Map<String, String> realPath,Connection connection) throws DaoException, SystemException, ConstraintViolationException {
        List<Map<String, String>> mapPrimaryKeyValues = Lists.newArrayList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // 如果没有指定主键，则从数据库中获取
            List<String> tablePKeys = Lists.newArrayList();
            if (Collections3.isEmpty(lstPrimaryKey)) {
                lstPrimaryKey = super.getTPrimaryKeys(tableName);
            }

            // 生成SQL语句
            StringBuffer sbSQL = new StringBuffer();
            sbSQL.append("select ");
            boolean bAppendField = false;
            for (Iterator<Map<String, String>> iter = lstField.iterator(); iter
                    .hasNext(); ) {
                Map<String, String> mapField = iter.next();
                if (bAppendField) {
                    sbSQL.append(",");
                }
                sbSQL.append(mapField.get(Package.ATTRIB_NAME));
                bAppendField = true;
            }
            sbSQL.append(" from ").append(tableName).append(" where 1=0");
            // 保存行记录
            String sql = sbSQL.toString();

            Connection conn = null;
            if(connection == null){
                conn = super.getDataSource().getConn();
            }else{
                conn = connection;
            }
            ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            if (logger.isDebugEnabled()) {
                logger.debug(sql);
            }

            rs = ps.executeQuery();
            for (Map<String, String> mapRow : mapRows) {
                rs.moveToInsertRow();
                this.setCurrentResultSetValues(rs, mapRow, mapLob, lstField,
                        realPath);
                rs.insertRow();

                // 附件
                // 针对SqlServer特殊处理
                if (super.getDataSource().getDbType().equals(DBType.mssql)) {
                    rs.last();
                }
                // 生成返回PrimaryKey值Map对象
                if (!Collections3.isEmpty(lstPrimaryKey)) {
                    Map<String, String> mapPrimaryKeyValue = Maps.newHashMap();
                    for (Iterator<String> iter = lstPrimaryKey.iterator(); iter.hasNext(); ) {
                        String fieldName = iter.next();
//                        int columnIndex = rs.findColumn(fieldName);
//                        String fieldValue = this.getCurrentResultSetValueString(rs,columnIndex, null);
                        String fieldValue = mapRow.get(fieldName);
                        mapPrimaryKeyValue.put(fieldName, fieldValue);
                    }
                    mapPrimaryKeyValues.add(mapPrimaryKeyValue);
                }
            }


        } catch (SQLException ex) {
            if (bThrowConstraintViolationException) {
                try {
                    DBType dbType = super.getDataSource().getDbType();
                    if (dbType.equals(DBType.mssql) && ex.getErrorCode() == 2627) {
                        throw new ConstraintViolationException(ex);
                    }else if (dbType.equals(DBType.db2) && ex.getErrorCode() == -803) {
                        throw new ConstraintViolationException(ex);
                    }
                    // 针对不同数据库生成主键重复异常???
                    else {
                        throw new DaoException(ex);
                    }
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            } else {
                throw new DaoException(ex);
            }
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        } finally {
            try {
                super.getDataSource().close(ps, null);
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        return mapPrimaryKeyValues;
    }

    /**
     * 新增行记录。
     *
     * @param tableName     表名
     * @param lstField      字段名列表
     * @param mapRow        行记录Map对象
     * @param mapLob        行记录中用到的Lob值Map对象
     * @param lstPrimaryKey PrimaryKey名称列表
     * @param realPath      附件路径
     * @return Map - PrimaryKeys值Map对象
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws ConstraintViolationException
     */
    public List<Map<String, String>> insertRow(String tableName,
                                               List<Map<String, String>> lstField, Map<String, String> mapRow,
                                               Map<String, byte[]> mapLob, List<String> lstPrimaryKey,
                                               Map<String, String> realPath,Connection connection) throws DaoException, SystemException {
        List<Map<String, String>> mapPrimaryKeyValues = null;
        try {
            List<Map<String, String>> mapRows = Lists.newArrayList();
            mapRows.add(mapRow);
            mapPrimaryKeyValues = this.insertRow(tableName, lstField, mapRows,
                    mapLob, lstPrimaryKey, false, realPath,connection);
        } catch (ConstraintViolationException ex) {
            throw new DaoException(ex);
        }
        return mapPrimaryKeyValues;
    }

    /**
     * @param tableName     表名
     * @param lstField      字段名列表
     * @param mapRows       行记录Map对象集合
     * @param mapLob        行记录中用到的Lob值Map对象
     * @param lstPrimaryKey PrimaryKey名称列表
     * @param realPath      附件路径
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public List<Map<String, String>> insertRow(String tableName,
                                               List<Map<String, String>> lstField, List<Map<String, String>> mapRows,
                                               Map<String, byte[]> mapLob, List<String> lstPrimaryKey,
                                               Map<String, String> realPath,Connection connection) throws DaoException, SystemException {
        List<Map<String, String>> mapPrimaryKeyValues = null;
        try {
            mapPrimaryKeyValues = this.insertRow(tableName, lstField, mapRows,
                    mapLob, lstPrimaryKey, false, realPath,connection);
        } catch (ConstraintViolationException ex) {
            throw new DaoException(ex);
        }
        return mapPrimaryKeyValues;
    }

    /**
     * 更新行记录。
     *
     * @param tableName     表名
     * @param lstField      字段名列表
     * @param mapRow        行记录
     * @param mapLob        行记录中用到的Lob值Map对象
     * @param lstPrimaryKey PrimaryKey名称列表
     * @return Map - PrimaryKeys值Map对象
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public Map<String, String> updateRow(String tableName,
                                          List<Map<String, String>> lstField, Map<String, String> mapRow,
                                          Map<String, byte[]> mapLob, List<String> lstPrimaryKey,
                                          boolean bNotExistThrowException, Map<String, String> realPath,Connection connection)
            throws DaoException, SystemException {
        List<Map<String, String>> mapRows = Lists.newArrayList();
        mapRows.add(mapRow);
        List<Map<String, String>> list = updateRow(tableName, lstField, mapRows, mapLob, lstPrimaryKey, bNotExistThrowException, realPath,connection);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 更新行记录。
     *
     * @param tableName               表名
     * @param lstField                字段名列表
     * @param mapRows                 行记录集合
     * @param mapLob                  行记录中用到的Lob值Map对象
     * @param lstPrimaryKey           PrimaryKey名称列表
     * @param bNotExistThrowException 是否抛出不存在记录集更新异常
     * @param realPath
     * @return Map PrimaryKeys值Map对象
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public List<Map<String, String>> updateRow(String tableName,
                                               List<Map<String, String>> lstField, List<Map<String, String>> mapRows,
                                               Map<String, byte[]> mapLob, List<String> lstPrimaryKey,
                                               boolean bNotExistThrowException, Map<String, String> realPath,Connection connection)
            throws DaoException, SystemException {
        List<Map<String, String>> mapPrimaryKeyValues = Lists.newArrayList();
        Map<String, String> mapPrimaryKeyValue = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // 如果没有指定主键，则从数据库中获取
            if (Collections3.isEmpty(lstPrimaryKey)) {
                lstPrimaryKey = super.getTPrimaryKeys(tableName);
            }
            // 不支持对没有设置主键的表进行更新记录操作
            if (Collections3.isEmpty(lstPrimaryKey)) {
                throw new SystemException("不支持对没有设置主键的表进行更新记录操作。");
            }
            Connection conn = null;
            if(connection == null){
                conn = super.getDataSource().getConn();
            }else{
                conn = connection;
            }
            if (!Collections3.isEmpty(mapRows)) {
                for (Map<String, String> mapRow : mapRows) {
                    // 生成主键值对象
                    Map<String, Object> mapPrimaryKeyValueObject = this.generateTablePrimaryKeyValue(tableName, mapRow,
                            lstPrimaryKey,true);
                    // 生成SQL语句
                    StringBuffer sbSQL = new StringBuffer();
                    sbSQL.append("select ");
                    boolean bFieldAppend = false;
                    for (Iterator<Map<String, String>> iter = lstField.iterator(); iter
                            .hasNext(); ) {
                        Map<String, String> mapField = iter.next();
                        if (bFieldAppend) {
                            sbSQL.append(",");
                        }
                        sbSQL.append(mapField.get(Package.ATTRIB_NAME));
                        bFieldAppend = true;
                    }
                    if (!bFieldAppend) {
                        throw new SystemException("没有可更新的非主键字段。");
                    }
                    sbSQL.append(" from ").append(tableName).append(" where ");
                    bFieldAppend = false;
                    for (Iterator<String> iter = lstPrimaryKey.iterator(); iter
                            .hasNext(); ) {
                        String fieldName = iter.next();
                        if (bFieldAppend) {
                            sbSQL.append(" and ");
                        }
                        sbSQL.append(fieldName).append("=?");
                        bFieldAppend = true;
                    }
                    // 打开记录集
                    String sql = sbSQL.toString();
                    ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_UPDATABLE);
                    if(logger.isDebugEnabled()){
                        logger.debug(sql);
                    }
                    int indexPrimaryKey = 1;
                    for (Iterator<String> iter = lstPrimaryKey.iterator(); iter
                            .hasNext(); indexPrimaryKey++) {
                        String fieldName = iter.next();
                        Object primaryKeyValue = mapPrimaryKeyValueObject
                                .get(fieldName);
                        ps.setObject(indexPrimaryKey, primaryKeyValue);
                    }
                    rs = ps.executeQuery();
                    // 更新记录集
                    if (rs.next()) {
                        this.setCurrentResultSetValues(rs, mapRow, mapLob, lstField,
                                realPath);
                        rs.updateRow();
                        // 生成返回PrimaryKey值Map对象
                        mapPrimaryKeyValue = new HashMap<String, String>();
                        for (Iterator<String> iter = lstPrimaryKey.iterator(); iter
                                .hasNext(); indexPrimaryKey++) {
                            String fieldName = iter.next();
                            String fieldValue = mapRow.get(fieldName);
                            mapPrimaryKeyValue.put(fieldName, fieldValue);
                        }
                        mapPrimaryKeyValues.add(mapPrimaryKeyValue);
                    } else if (bNotExistThrowException) {
                        throw new SystemException("对应的记录不存在，更新失败。");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        } finally {
            try {
                super.getDataSource().close(ps, null);
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        return mapPrimaryKeyValues;
    }

    /**
     * 更新行记录。
     *
     * @param tableName     表名
     * @param lstField      字段名列表
     * @param mapRow        行记录
     * @param mapLob        行记录中用到的Lob值Map对象
     * @param lstPrimaryKey -
     *                      PrimaryKey名称列表
     * @return Map - PrimaryKeys值Map对象
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public Map<String, String> updateRow(String tableName,
                                         List<Map<String, String>> lstField, Map<String, String> mapRow,
                                         Map<String, byte[]> mapLob, List<String> lstPrimaryKey,
                                         Map<String, String> realPath,Connection connection) throws DaoException, SystemException, SQLException {
        List<Map<String, String>> mapRows = Lists.newArrayList();
        mapRows.add(mapRow);
        List<Map<String, String>> list = this.updateRow(tableName, lstField, mapRows, mapLob,
                lstPrimaryKey, true, realPath,connection);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 更新行记录。
     *
     * @param tableName     表名
     * @param lstField      字段名列表
     * @param mapRows       行记录
     * @param mapLob        行记录中用到的Lob值Map对象
     * @param lstPrimaryKey
     * @param realPath      真实路径
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public List<Map<String, String>> updateRow(String tableName,
                                               List<Map<String, String>> lstField, List<Map<String, String>> mapRows,
                                               Map<String, byte[]> mapLob, List<String> lstPrimaryKey,
                                               Map<String, String> realPath,Connection connection) throws DaoException, SystemException {
        return this.updateRow(tableName, lstField, mapRows, mapLob,
                lstPrimaryKey, true, realPath,connection);
    }

    /**
     * 删除行记录。
     *
     * @param tableName     表名
     * @param mapRow        行记录Map对象
     * @param lstPrimaryKey PrimaryKey名称列表
     * @return Map - PrimaryKeys值Map对象
     */
    public Map<String, String> deleteRow(String tableName,
                                         Map<String, String> mapRow, List<String> lstPrimaryKey,Connection connection)
            throws DaoException, SystemException {
        List<Map<String, String>> mapRows = Lists.newArrayList();
        mapRows.add(mapRow);
        List<Map<String, String>> list = this.deleteRow(tableName, mapRows, lstPrimaryKey,connection);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 删除行记录。
     *
     * @param tableName     表名
     * @param mapRows       行记录Map对象
     * @param lstPrimaryKey PrimaryKey名称列表
     * @return Map - PrimaryKeys值Map对象
     */
    public List<Map<String, String>> deleteRow(String tableName,
                                               List<Map<String, String>> mapRows, List<String> lstPrimaryKey,Connection connection)
            throws DaoException, SystemException {
        List<Map<String, String>> mapPrimaryKeyValues = Lists.newArrayList();
        Map<String, String> mapPrimaryKeyValue = null;
        PreparedStatement ps = null;
        try {
            // 如果没有指定主键，则从数据库中获取
            if (Collections3.isEmpty(lstPrimaryKey)) {
                lstPrimaryKey = super.getTPrimaryKeys(tableName);
            }
            // 不支持对没有设置主键的表进行删除记录操作
            if (!(lstPrimaryKey != null && lstPrimaryKey.size() > 0)) {
                throw new SystemException("不支持对没有设置主键的表进行删除记录操作。");
            }
            // 生成SQL语句
            StringBuffer sbSQL = new StringBuffer();
            sbSQL.append("delete from ").append(tableName).append(" where ");
            boolean bAppend = false;
            for (Iterator<String> iter = lstPrimaryKey.iterator(); iter
                    .hasNext(); ) {
                String fieldName = iter.next();
                if (bAppend) {
                    sbSQL.append(" and ");
                }
                sbSQL.append(fieldName).append("=?");
                bAppend = true;
            }
            Connection conn = null;
            if(connection == null){
                conn = super.getDataSource().getConn();
            }else{
                conn = connection;
            }
            if (!Collections3.isEmpty(mapRows)) {
                for (Map<String, String> mapRow : mapRows) {
                    // 得到PrimaryKey的值对象
                    Map<String, Object> mapPrimaryKeyValueObject = this
                            .generateTablePrimaryKeyValue(tableName, mapRow,
                                    lstPrimaryKey,true);
                    // 执行SQL语句
                    String sql = sbSQL.toString();
                    if(logger.isDebugEnabled()){
                        logger.debug(sql);
                    }
                    ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_UPDATABLE);
                    int indexPrimaryKey = 1;
                    for (Iterator<String> iter = lstPrimaryKey.iterator(); iter
                            .hasNext(); indexPrimaryKey++) {
                        String fieldName = iter.next();
                        Object primaryKeyValue = mapPrimaryKeyValueObject
                                .get(fieldName);
                        ps.setObject(indexPrimaryKey, primaryKeyValue);
                    }
                    int count = ps.executeUpdate();
                    if (count > 0) {
                        mapPrimaryKeyValue = new HashMap<String, String>();
                        for (Iterator<String> iter = lstPrimaryKey.iterator(); iter
                                .hasNext(); indexPrimaryKey++) {
                            String fieldName = iter.next();
                            String fieldValue = mapRow.get(fieldName);
                            mapPrimaryKeyValue.put(fieldName, fieldValue);
                        }
                        mapPrimaryKeyValues.add(mapPrimaryKeyValue);
                    }
                }
            }

        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        } finally {
            try {
                super.getDataSource().close(ps, null);
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        return mapPrimaryKeyValues;
    }

    /**
     * 保存行记录。
     *
     * @param tableName     表名
     * @param lstField      字段名列表
     * @param mapRow        行记录Map对象
     * @param mapLob        行记录中用到的Lob值Map对象
     * @param lstPrimaryKey PrimaryKey名称列表
     * @param loadType      存储策略 如果为null 则默认：更新模式
     *                      @see LoadType
     * @return Map - PrimaryKeys值Map对象
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public Map<String, String> saveRow(String tableName,
                                       List<Map<String, String>> lstField, Map<String, String> mapRow,
                                       Map<String, byte[]> mapLob, List<String> lstPrimaryKey,
                                       Map<String, String> realPath, LoadType loadType,Connection connection) throws DaoException, SystemException, ConstraintViolationException {
        Map<String, String> mapPrimaryKeyValue = null;
        try {
            if (loadType != null && loadType.equals(LoadType.Insert)) {
                mapPrimaryKeyValue = this.insertRow(tableName, lstField,
                        mapRow, mapLob, lstPrimaryKey,true, realPath,connection);
            } else {
                mapPrimaryKeyValue = this.updateRow(tableName, lstField,
                        mapRow, mapLob, lstPrimaryKey, false, realPath,connection);
                if (MapUtils.isEmpty(mapPrimaryKeyValue)) {
                    mapPrimaryKeyValue = this.insertRow(tableName, lstField,
                            mapRow, mapLob, lstPrimaryKey, true, realPath,connection);
                }
            }
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        } catch (ConstraintViolationException ex) {
            throw ex;
        }
        return mapPrimaryKeyValue;
    }


    /**
     * 字节数组写入文件
     *
     * @param binaryLob
     * @param url
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public void writeFile(byte[] binaryLob, String url) throws DaoException,
            SystemException {
        try {
            if (binaryLob != null && binaryLob.length > 0) {
                File file = new File(url);

                // 创建目录
                // String parentPaht = file.getParentFile().toString();
                String parentPaht = url.substring(0, url
                        .lastIndexOf(File.separator) + 1);
                File parentFile = new File(parentPaht);
                if (!parentFile.exists()) {
                    if (!parentFile.mkdirs()) {
                        throw new SystemException("父目录无法创建!");
                    }
                }

                // 创建文件
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        throw new SystemException("文件无法创建!");
                    }
                }

                OutputStream out = new FileOutputStream(file);
                BufferedOutputStream stream = new BufferedOutputStream(out);
                stream.write(binaryLob);
                stream.flush();
                stream.close();
                out.close();
            }
        } catch (DaoException ex) {
            throw ex;
        } catch (SystemException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage(), ex);
        }

    }

}
