/**
 *  Copyright (c) 2013 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.dao;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.io.StreamTool;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.db.meta.DiDataSource;
import com.eryansky.core.db.meta.DiDataSourceFactory;
import com.eryansky.core.db.utils.DBType;
import com.eryansky.core.db.utils.KeyPair;
import com.eryansky.core.db.vo.AttaPathVo;
import com.eryansky.core.db.vo.DbConfig;
import com.eryansky.core.db.vo.Package;
import com.eryansky.core.db.vo.ResultVo;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 抽取DAO
 * 注：由于使用的数据源是动态创建的  使用DButils QueryRunner执行完sql语句之后，会自动关闭ResultSet或者Statement，但是不会关闭Connection对象，需要手动关闭连接对象
 * User: 温春平 wencp@jx.tobacco.gov.cn
 * Date: 14-1-13 下午4:11
 */
//@Repository
public class ExtractDAO extends DiDao {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ExtractDAO(){
    	
    }
    /**
     * 抽取数据源VO
     * @param extractDatasource
     * @throws Exception
     */
    public ExtractDAO(DbConfig extractDatasource) throws Exception {
        super(extractDatasource);
    }
    
    public ExtractDAO(DiDataSource dataSource) {
		super(dataSource);
	}
    
    
    public ExtractDAO(DiDataSourceFactory diDataSourceFactory) {
    	super(diDataSourceFactory);
    }

    /**
     * 得到行记录集。
     *
     * @param tableName      - 表名
     * @param fieldNames     - 字段名称，用逗号(,)分割
     * @param sqlWhere       - 条件SQL语句，例如：xm=? and csrq=?
     * @param lstParameter   - 条件SQL语句中的参数字符串值，List<Map.Entry<真实的字段名称, 参数值>>
     * @param sqlOrder       - 排序SQL语句
     * @param offset         - 偏移量
     * @param pagesize       - 页大小
     * @param sizeLimitCount - 大小限制数量(单位：字节)
     * @param isDelete       -是否要删除表中业务数据
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public ResultVo getRows(String tableName, String fieldNames, String sqlWhere, List<Map.Entry<String, String>> lstParameter, String sqlOrder, List<String> lstPrimaryKey,
                            int offset, int pagesize, int sizeLimitCount, boolean isDelete, AttaPathVo attaPathVo) throws DaoException, SystemException {
        return this.getRows(tableName, fieldNames, sqlWhere, lstParameter, sqlOrder, lstPrimaryKey, null, offset, pagesize, sizeLimitCount, isDelete, attaPathVo);
    }

    /**
     * 得到行记录集。
     *
     * @param tableName      - 表名
     * @param fieldNames     - 字段名称，用逗号(,)分割
     * @param sqlWhere       - 条件SQL语句，例如：xm=? and csrq=?
     * @param lstParameter   - 条件SQL语句中的参数字符串值，List<Map.Entry<真实的字段名称, 参数值>>
     * @param sqlOrder       - 排序SQL语句
     * @param offset         - 偏移量
     * @param pagesize       - 页大小
     * @param sizeLimitCount - 大小限制数量(单位：字节)
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public ResultVo getRows(String tableName, String fieldNames, String sqlWhere, List<Map.Entry<String, String>> lstParameter, String sqlOrder, List<String> lstPrimaryKey,
                            int offset, int pagesize, int sizeLimitCount, AttaPathVo attaPathVo) throws DaoException, SystemException, SQLException {
        return this.getRows(tableName, fieldNames, sqlWhere, lstParameter, sqlOrder, lstPrimaryKey, null, offset, pagesize, sizeLimitCount, false, attaPathVo);
    }

    /**
     * 得到行记录集。
     *
     * @param tableName      - 表名
     * @param fieldNames     - 字段名称，用逗号(,)分割
     * @param sqlWhere       - 条件SQL语句，例如：xm=? and csrq=?
     * @param lstParameter   - 条件SQL语句中的参数字符串值，List<Map.Entry<真实的字段名称, 参数值>>
     * @param sqlOrder       - 排序SQL语句
     * @param pagesize       - 页大小
     * @param sizeLimitCount - 大小限制数量(单位：字节)
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public ResultVo getRows(String tableName, String fieldNames, String sqlWhere, List<Map.Entry<String, String>> lstParameter, String sqlOrder, List<String> lstPrimaryKey,
                            int pagesize, int sizeLimitCount, AttaPathVo attaPathVo) throws DaoException, SystemException {
        return this.getRows(tableName, fieldNames, sqlWhere, lstParameter, sqlOrder, lstPrimaryKey, null, 0, pagesize, sizeLimitCount, false, attaPathVo);
    }

    /**
     * 得到行记录集。
     *
     * @param tableName      - 表名
     * @param fieldNames     - 字段名称，用逗号(,)分割
     * @param sqlWhere       - 条件SQL语句，例如：xm=? and csrq=?
     * @param lstParameter   - 条件SQL语句中的参数字符串值，List<Map.Entry<真实的字段名称, 参数值>>
     * @param sqlOrder       - 排序SQL语句
     * @param timeFieldName  - 时间戳
     * @param pagesize       - 记录页大小
     * @param sizeLimitCount - 大小限制数量(单位：字节)
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public ResultVo getRows(String tableName, String fieldNames, String sqlWhere, List<Map.Entry<String, String>> lstParameter, String sqlOrder, List<String> lstPrimaryKey,
                            String timeFieldName, int pagesize, int sizeLimitCount, AttaPathVo attaPathVo) throws DaoException, SystemException {
        return this.getRows(tableName, fieldNames, sqlWhere, lstParameter, sqlOrder, lstPrimaryKey, timeFieldName, 0, pagesize, sizeLimitCount, false, attaPathVo);
    }


    /**
     * 得到行记录集。
     *
     * @param tableName      - 表名
     * @param fieldNames     - 字段名称，用逗号(,)分割 查询所有传：null或者"*"
     * @param sqlWhere       - 条件SQL语句，例如：xm=? and csrq=?
     * @param lstParameter   - 条件SQL语句中的参数字符串值，List<Map.Entry<真实的字段名称, 参数值>>
     * @param sqlOrder       - 排序SQL语句
     * @param timeFieldName  - 时间戳
     * @param offset         - 记录偏移量
     * @param pagesize       - 记录页大小
     * @param sizeLimitCount - 大小限制数量(单位：字节)
     * @param isDelete       -是否要删除表中业务数据
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public ResultVo getRows(String tableName, String fieldNames, String sqlWhere, List<Map.Entry<String, String>> lstParameter, String sqlOrder, List<String> lstPrimaryKey,
                            String timeFieldName, int offset, int pagesize, int sizeLimitCount, boolean isDelete, AttaPathVo attaPathVo) throws DaoException, SystemException {
        final String SELECT_ALL = "*";
        if(StringUtils.isBlank(fieldNames)){
            fieldNames = SELECT_ALL;
            logger.warn("参数[fieldNames]为空，自动转换为{}",new Object[]{SELECT_ALL});
        }
        final String TIMESTAMP_FILED_NAME = "DI_TIMESTAMP_____007";
        ResultVo resultVo = new ResultVo();
        List<Map<String, String>> lstRow = Lists.newArrayList();
        List<Map<String, String>> lstMeta = Lists.newArrayList();
        Map<String, byte[]> mapLob = Maps.newHashMap();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        //标识数据库是否支持可更新结果集
        boolean isSupport = false;
        Connection connection = null;
        try {
            // jdbc2.0中，用select
            // *打开的记录集永远都是只读记录集，所以要deleteRow或updateRow记录集，必须要select具体的列名。
            // 得到表列名
            StringBuffer sb = new StringBuffer();
            sb.append("select ").append((SELECT_ALL).equals(fieldNames) ? SELECT_ALL:fieldNames).append(" from ").append(tableName).append(" where 1=0");
            String sql = sb.toString();

            connection = super.getDataSource().getConn();
            //判断数据库是否支持可更新结果集
            if (isDelete) {
                isSupport = super.getDataSource().supportsResultSetConcurrency();
            }
            pstm = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            if(logger.isDebugEnabled()){
                logger.debug(sql);
            }
            rs = pstm.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            // 生成参数值对象
            List<Object> lstParameterObjectValue = Lists.newArrayList();


            List<Map.Entry<String, Object>> pValue = Lists.newArrayList();
            int timeFieldIndex = 0;//时间戳索引号
            boolean isSelectFiledContainTimeField = false;//查询字段是否包含时间戳字段
            sb = new StringBuffer();
            for (int i = 1; i <= columnCount; i++) {
                String tName = rs.getMetaData().getTableName(i);
                String fieldName = rs.getMetaData().getColumnName(i);
                String tableAndFieldName = tName+"."+fieldName;
                if(tableAndFieldName.equals(timeFieldName)){
                    timeFieldIndex = i;
                    if(fieldNames.contains(timeFieldName)){
                        isSelectFiledContainTimeField = true;
                    }
                }

                if (!Collections3.isEmpty(lstParameter)) {
                    for (Map.Entry<String, String> entry : lstParameter) {
                        if(tableAndFieldName.equals(entry.getKey())){
                            int columnType = resultSetMetaData.getColumnType(i);
                            Object objValue = super.convertResultSetValueToObject(columnType, entry.getValue());
                            pValue.add(new KeyPair<String, Object>(entry.getKey(),objValue));
                        }

                    }
                }

                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(tableAndFieldName);
            }

            if (!Collections3.isEmpty(lstParameter)) {
                for(Map.Entry<String, Object> pentry : pValue){
                    for(Map.Entry<String, String> entry : lstParameter){
                        if(pentry.getKey().equals(entry.getKey())){
                            lstParameterObjectValue.add(pentry.getValue());
                            lstParameter.remove(entry);
                            break;
                        }
                    }
                }
            }


            String selectFields = sb.toString();

            // 生成完整的SQL语句
            sb = new StringBuffer();
            sb.append("select ");
            if (StringUtils.isNotBlank(timeFieldName)) {
                if(isSelectFiledContainTimeField){
                    sb.append(selectFields).append(" from ");
                }else{
                    columnCount = columnCount +1;
                    timeFieldIndex = columnCount;
                    sb.append(selectFields).append(", ").append(timeFieldName).append(" as ").append(TIMESTAMP_FILED_NAME).append(" from ");
                }
            } else {
                sb.append(selectFields).append(" from ");
            }
            sb.append(tableName);
            if (StringUtils.isNotBlank(sqlWhere)) {
                sb.append(" where ").append(sqlWhere);
            }
            if (StringUtils.isNotBlank(sqlOrder)) {
                sb.append(" order by ").append(sqlOrder);
            }
            sql = sb.toString();
            // 生成分页SQL语句
            int limit = offset + pagesize -1;
            if(logger.isDebugEnabled()){
                logger.debug("offset:{},pagesize:{},limit:{}",new Object[]{offset,pagesize,limit});
            }
            if (offset > 0) {
                sql = super.getDataSource().getPageSql(sql, offset, pagesize);
            }
            // 执行SQL语句
            if (isSupport && isDelete) {
                pstm = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            } else {
                pstm = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            //分页参数
            if (offset > 0) {
                DBType dbType = super.getDataSource().getDbType();
                if(dbType.equals(DBType.db2)){
                    lstParameterObjectValue.add(Integer.valueOf(offset));
                    lstParameterObjectValue.add(Integer.valueOf(limit));
                }else if(dbType.equals(DBType.mssql)){
                    lstParameterObjectValue.add(Integer.valueOf(offset));
                    lstParameterObjectValue.add(Integer.valueOf(limit));
                }else if(dbType.equals(DBType.MySQL)){
                    lstParameterObjectValue.add(Integer.valueOf(offset-1));
                    lstParameterObjectValue.add(Integer.valueOf(limit));
                }else{
                    throw new DaoException("不支持该数据库分页查询.");
                }
            } else {
                pstm.setMaxRows(pagesize);
            }
            // 设置参数
            if (!Collections3.isEmpty(lstParameterObjectValue)){
                for (int i = 0; i < lstParameterObjectValue.size(); i++) {
                    Object objValue = lstParameterObjectValue.get(i);
                    pstm.setObject(i + 1, objValue);
                }
            }
            // 查询
            rs = pstm.executeQuery();
            logger.debug("抽取SQL:{}",sql);
            for (int i = 1; i <= columnCount; i++) {
                Map<String, String> mapField = super.getResultSetMetaDataColumn(rs.getMetaData(), i, lstPrimaryKey, attaPathVo);
                lstMeta.add(mapField);
            }

            // 生成Data列表
            int sizeCount = 0;
            String maxTimestamp = null;
            while (rs.next()) {
                Map<String, String> mapRow = Maps.newHashMap();
                Map<String, byte[]> mapRowLob = Maps.newHashMap();

                for (int i = 1; i <= columnCount; i++) {
                    String tName = rs.getMetaData().getTableName(i);
                    String fieldName = rs.getMetaData().getColumnName(i);
                    String fieldValue = null;
                    String attaValue = null;//附件标识
                    fieldValue = super.getCurrentResultSetValueString(rs, i, mapRowLob);

                    if (sizeLimitCount > 0 && fieldValue != null) {
                        sizeCount += fieldValue.length();
                    }
                    mapRow.put(tName+"."+fieldName, fieldValue);

                    //如果是附件字段
                    if ((attaPathVo != null) && (attaPathVo.getLstAttaFieldName() != null)
                            && attaPathVo.getLstAttaFieldName().contains(fieldName)) {

                        //是否是绝对路径
                        if (attaPathVo.getLstAbsolutePath() != null
                                && attaPathVo.getLstAbsolutePath().contains(fieldName)) {
                            //根据绝对路径把文件写入mapRowLob字节，并生成UUID
                            attaValue = this.getAttaLob(fieldValue, mapRowLob);
                        } else {
                            //若是相对路径，
                            if (attaPathVo.getPrefixPaths() != null
                                    && attaPathVo.getPrefixPaths().containsKey(fieldName)) {
                                //根据绝对路径把文件写入mapRowLob字节，并生成UUID
                                String path = attaPathVo.getPrefixPaths().get(fieldName);//相对基路径
                                if (path != null || !"".equals(path)) {
                                    String url = path + fieldValue;// 组合成URL
                                    attaValue = this.getAttaLob(url, mapRowLob);
                                }
                            }
                        }

                        String virtualAttaField = tName+"."+fieldName + Package.ATTA_SUFFIX;
                        mapRow.put(virtualAttaField, attaValue);
                    }

                }
                if (StringUtils.isNotBlank(timeFieldName)) {
                    if(timeFieldIndex >0){
                        maxTimestamp = super.getCurrentResultSetValueString(rs, timeFieldIndex, null);
                    }
                }


                lstRow.add(mapRow);
                mapLob.putAll(mapRowLob);
                //计算所有Blob字段大小
                if (sizeLimitCount > 0 && mapRowLob.size() > 0) {
                    for (Iterator<Map.Entry<String, byte[]>> iter = mapRowLob.entrySet().iterator(); iter.hasNext(); ) {
                        Map.Entry<String, byte[]> entry = iter.next();
                        byte[] data = entry.getValue();
                        if (data != null) {
                            sizeCount += data.length;
                        }
                    }
                }

                //如果需要删除表中业务数据，则在此处删除
                if (isSupport && isDelete) {
                    rs.deleteRow();
                } else {
                    //采用其他方式删除业务数据，待讨论
                }

                if (sizeLimitCount > 0 && sizeCount >= sizeLimitCount) {
                    resultVo.setSizeLimit(true);
                    break;
                }
            }
            resultVo.setMaxTimestamp(maxTimestamp);
            resultVo.setSizeCount(sizeCount);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        } finally {
            try {
                super.getDataSource().close(pstm, rs);
            } catch (SQLException e) {
                logger.error(e.getMessage(),e);
            }
        }
        resultVo.setName(tableName);
        resultVo.setMetadata(lstMeta);
        resultVo.setRows(lstRow);
        resultVo.setLobs(mapLob);
        return resultVo;
    }


    /**
     * 根据URL抽取附件到mapLob，返回UUID码
     *
     * @param url
     * @param mapLob
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public String getAttaLob(String url, Map<String, byte[]> mapLob) throws SystemException, DaoException {
        String value = null;
        try {
            //抽取文件
            File file = new File(url);
            //文件存在或者是文件则继续
            if ((file.exists() && file.isFile())) {
                InputStream is = new FileInputStream(file);
                byte[] binaryBuffer = StreamTool.readInputStream(is);
                if (mapLob != null) {
                    value = Identities.uuid2();
                    mapLob.put(value, binaryBuffer);
                } else {
                    byte[] enbuff = Base64.encodeBase64(binaryBuffer);
                    binaryBuffer = null;
                    value = new String(enbuff);
                    enbuff = null;
                }
            }
        } catch (IOException ex) {
            throw new DaoException(ex);
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage(), ex);
        }
        return value;
    }



}
