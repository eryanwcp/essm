package com.eryansky.core.db.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.SysConstants;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.collections.MapUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.eryansky.core.db.dao.ExtractDAO;
import com.eryansky.core.db.dao.LoadDao;
import com.eryansky.core.db.exception.ConstraintViolationException;
import com.eryansky.core.db.meta.DiDataSource;
import com.eryansky.core.db.meta.DiDataSourceFactory;
import com.eryansky.core.db.utils.KeyPair;
import com.eryansky.core.db.vo.AttaPathVo;
import com.eryansky.core.db.vo.DbConfig;
import com.eryansky.core.db.vo.Package;
import com.eryansky.core.db.vo.ResultVo;
import com.eryansky.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通用存储、查询管理
 *
 * @author wencp@尔演&Eryan eryanwcp@gmail.com
 */
@Service
public class ETLService {

    private static final Logger logger = LoggerFactory.getLogger(ETLService.class);

    private ExtractDAO extractDAO;
    private LoadDao loadDao;
    private DbConfig localDatasource;//本地数据源
    private DiDataSource diDataSource;

    public ETLService() {
        super();
        try {
            localDatasource = new DbConfig(
                    SysConstants.getJdbcDriverClassName(),
                    SysConstants.getJdbcUrl(), SysConstants.getJdbcUserName(),
                    SysConstants.getJdbcPassword());
            diDataSource = DiDataSourceFactory.create(localDatasource);
            diDataSource.setInitialSize(1);
            diDataSource.setMaxActive(20);
            diDataSource.setValidationQuery(AppConstants.getJdbcValidationQuery());
            diDataSource.setTestWhileIdle(true);
            diDataSource.setTestOnBorrow(true);
            diDataSource.setTestOnReturn(true);
            diDataSource.setMaxWait(500);
            diDataSource.setMinIdle(5);
            diDataSource.setMaxIdle(10);
            //minEvictableIdleTimeMillis:1800000(30分钟) MySQL:28800(8小时)
            diDataSource.setMinEvictableIdleTimeMillis(20000);// 需要大于MySQL wait_timeout
            diDataSource.setTimeBetweenEvictionRunsMillis(28800);//
            this.extractDAO = new ExtractDAO(diDataSource);
            this.loadDao = new LoadDao(diDataSource);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }



    /**
     * 数据存储或更新（事务控制）
     *
     * @param tableName   表名
     * @param fieldNames   字段元数据(表名+"."+字段名) 多个之间以","分割
     * @param mapRow      行记录Map对象 key:（表名+"."+字段名） value:值（String类型）
     * @param mapLob      行记录中用到的Lob值Map对象
     * @param primaryKeys 主键字段 表名+"."+字段名 如果该使用物理表主键 则传null
     * @param realPath    附件路径
     * @return Map<String,String> key：表名+"."+字段名 value:主键字段对应的String类型值
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws com.eryansky.common.exception.ServiceException
     */
    public Map<String, String> save(String tableName,String fieldNames, Map<String, String> mapRow,
                                    Map<String, byte[]> mapLob, List<String> primaryKeys,
                                    Map<String, String> realPath) throws DaoException, SystemException, ServiceException {
        Map<String, String> mapPrimaryKeyValue = null;
        try {
            List<Map<String, String>> fieldMetas = loadDao.getMetaFields(tableName,fieldNames,null);
            mapPrimaryKeyValue = loadDao.insertRow(tableName, fieldMetas,
                    mapRow, mapLob, primaryKeys, true, realPath,null);
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        } catch (ConstraintViolationException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
        return mapPrimaryKeyValue;
    }

    /**
     * 数据更新（事务控制）
     *
     * @param tableName   表名
     * @param fieldNames   字段元数据(表名+"."+字段名) 多个之间以","分割
     * @param mapRow      行记录Map对象 key:（表名+"."+字段名） value:值（String类型）
     * @param mapLob      行记录中用到的Lob值Map对象
     * @param primaryKeys 主键字段 表名+"."+字段名 如果该使用物理表主键 则传null
     * @param realPath    附件路径
     * @return Map<String,String> key：表名+"."+字段名 value:主键字段对应的String类型值
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws com.eryansky.common.exception.ServiceException
     */
    public Map<String, String> update(String tableName,String fieldNames, Map<String, String> mapRow,
                                      Map<String, byte[]> mapLob, List<String> primaryKeys,
                                      Map<String, String> realPath) throws DaoException, SystemException, ServiceException {
        Map<String, String> mapPrimaryKeyValue = null;
        try {
            List<Map<String, String>> fieldMetas = loadDao.getMetaFields(tableName,fieldNames,null);
            mapPrimaryKeyValue = loadDao.updateRow(tableName, fieldMetas,
                    mapRow, mapLob, primaryKeys, true, realPath,null);
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        }
        return mapPrimaryKeyValue;
    }


    /**
     * 数据存储或更新
     *
     * @param tableName   表名
     * @param fieldNames  字段元数据(表名+"."+字段名) 多个之间以","分割
     * @param mapRow      行记录Map对象 key:（表名+"."+字段名） value:值（String类型）
     * @param mapLob      行记录中用到的Lob值Map对象
     * @param primaryKeys 主键字段 表名+"."+字段名 如果该使用物理表主键 则传null
     * @param realPath    附件路径
     * @return Map<String,String> key：表名+"."+字段名 value:主键字段对应的String类型值
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws com.eryansky.common.exception.ServiceException
     */
    public Map<String, String> saveOrUpdate(String tableName,String fieldNames, Map<String, String> mapRow,
                                            Map<String, byte[]> mapLob, List<String> primaryKeys,
                                            Map<String, String> realPath) throws DaoException, SystemException, ServiceException {
        Map<String, String> mapPrimaryKeyValue = null;
        AttaPathVo attaPathVo = null;
        try {
            if(!MapUtils.isEmpty(realPath)){
                Set<String> realPathSets = realPath.keySet();
                attaPathVo = new AttaPathVo();
                for(String realpath: realPathSets){
                    attaPathVo.getLstAttaFieldName().add(realpath);
                    attaPathVo.getLstAbsolutePath().add(realpath);
                }
            }
            List<Map<String, String>> fieldMetas = loadDao.getMetaFields(tableName, fieldNames,attaPathVo);
            mapPrimaryKeyValue = loadDao.updateRow(tableName, fieldMetas,
                    mapRow, mapLob, primaryKeys, false, realPath,null);
            if (MapUtils.isEmpty(mapPrimaryKeyValue)) {
                mapPrimaryKeyValue = loadDao.insertRow(tableName, fieldMetas,
                        mapRow, mapLob, primaryKeys, true, realPath,null);
            }
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        } catch (ConstraintViolationException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
        return mapPrimaryKeyValue;
    }

    /**
     * 删除数据 （事务控制）
     * @param tableName   表名
     * @param mapRow      行记录Map对象 key:（表名+"."+字段名） value:值（String类型）
     * @param primaryKeys 主键字段 表名+"."+字段名 如果该使用物理表主键 则传null
     * @return Map<String,String> key：表名+"."+字段名 value:主键字段对应的String类型值
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws com.eryansky.common.exception.ServiceException
     */
    public Map<String, String> deleteRow(String tableName,Map<String, String> mapRow,List<String> primaryKeys) throws DaoException, SystemException, ServiceException {
        Map<String, String> mapPrimaryKeyValue = null;
        try {
            mapPrimaryKeyValue = loadDao.deleteRow(tableName,mapRow,primaryKeys,null);
        } catch (SystemException ex) {
            throw ex;
        } catch (DaoException ex) {
            throw ex;
        }
        return mapPrimaryKeyValue;
    }

    /**
     * 通用查询 查询单行记录
     *
     * @param tableName   表名
     * @param idValue  主键（名称为"ID"）值：
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public Map<String, String> getRow(String tableName,String idValue) throws DaoException, SystemException {
        Map.Entry<String,String> map =  new KeyPair<String,String>(tableName+".ID",idValue);
        List<Map.Entry<String, String>> parameters = Lists.newArrayList();
        parameters.add(map);

        List<String> primaryKeys = Lists.newArrayList();
        primaryKeys.add(tableName+".ID");
        return this.getRow(tableName, parameters, primaryKeys);
    }

    /**
     * 通用查询 查询单行记录
     *
     * @param tableName   表名
     * @param parameters  参数 字段名：字段对应的值
     * @param primaryKeys 主键
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public Map<String, String> getRow(String tableName, List<Map.Entry<String, String>> parameters, List<String> primaryKeys) throws DaoException, SystemException {
        String sqlShere  = "";
        if (Collections3.isNotEmpty(primaryKeys)) {
            for (int i = 0; i < primaryKeys.size(); i++) {
                sqlShere += primaryKeys.get(i) + " = ?";
                if (i != primaryKeys.size() - 1) {
                    sqlShere += "and";
                }
            }
        }
        return this.getRow(tableName, null, sqlShere, parameters, primaryKeys, null);
    }

    /**
     * 通用查询 查询单行记录
     *
     * @param tableName   表名
     * @param fieldNames  字段名称 所有可以传 null或"*"
     * @param sqlWhere    where条件
     * @param parameters  参数 字段名：字段对应的值
     * @param primaryKeys 主键
     * @param attaPathVo  附件
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public Map<String, String> getRow(String tableName, String fieldNames, String sqlWhere, List<Map.Entry<String, String>> parameters, List<String> primaryKeys,
                                      AttaPathVo attaPathVo) throws DaoException, SystemException {
        final String SELECT_ALL = "*";
        if (StringUtils.isBlank(fieldNames)) {
            fieldNames = SELECT_ALL;
//            logger.warn("参数[fieldNames]为空，自动转换为{}", new Object[]{SELECT_ALL});
        }
        List<Map<String, String>> fieldValues = Lists.newArrayList();
        ResultVo resultVo = extractDAO.getRows(tableName, fieldNames, sqlWhere, parameters, null, primaryKeys, null, 0, 0, 0, false, attaPathVo);
        if (resultVo != null && Collections3.isNotEmpty(resultVo.getRows())) {
            for (Map<String, String> row : resultVo.getRows()) {
                Set<String> rowKeys = row.keySet();
                Map<String, String> newRow = Maps.newHashMap();
                for (String rowKey : rowKeys) {
                    String rowType = null;
                    for(Map<String, String> metaField:resultVo.getMetadata()){
                        if(metaField.get(Package.ATTRIB_NAME).equalsIgnoreCase(rowKey)){
                            rowType = metaField.get(Package.ATTRIB_TYPE);
                            break;
                        }
                    }
                    String rowValue = row.get(rowKey);
                    String newValue = row.get(rowKey);
                    if(rowType!=null && rowValue != null && (rowType.equals(Package.TYPE_DATE) || rowType.equals(Package.TYPE_TIMESTAMP))){
                        if(rowValue.length() == 19){//特殊处理 时间字段去除秒
                            newValue = rowValue.substring(0,16);
                        }
                    }
                    newRow.put(StringUtils.substringAfter(rowKey, "."), newValue);
                }
                fieldValues.add(newRow);
            }
        }
        return fieldValues.isEmpty() ? null:fieldValues.get(0);
    }


    /**
     * 通用查询 多条记录
     *
     * @param tableName   表名
     * @param fieldNames  字段名称 所有可以传 null或"*"
     * @param sqlWhere    where条件
     * @param parameters  参数 字段名：字段对应的值
     * @param sqlOrder    排序条件
     * @param primaryKeys 主键
     * @param attaPathVo  附件
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public List<Map<String, String>> getRows(String tableName, String fieldNames, String sqlWhere, List<Map.Entry<String, String>> parameters, String sqlOrder, List<String> primaryKeys,
                                             AttaPathVo attaPathVo) throws DaoException, SystemException {
        final String SELECT_ALL = "*";
        if (StringUtils.isBlank(fieldNames)) {
            fieldNames = SELECT_ALL;
//            logger.warn("参数[fieldNames]为空，自动转换为{}", new Object[]{SELECT_ALL});
        }
        List<Map<String, String>> fieldValues = Lists.newArrayList();
        ResultVo resultVo = extractDAO.getRows(tableName, fieldNames, sqlWhere, parameters, sqlOrder, primaryKeys, null, 0, 0, 0, false, attaPathVo);
        if (resultVo != null && Collections3.isNotEmpty(resultVo.getRows())) {
            for (Map<String, String> row : resultVo.getRows()) {
                Set<String> rowKeys = row.keySet();
                Map<String, String> newRow = Maps.newHashMap();
                for (String rowKey : rowKeys) {
                    newRow.put(StringUtils.substringAfter(rowKey, "."), row.get(rowKey));
                }
                fieldValues.add(newRow);
            }
        }

        return fieldValues;
    }

    /**
     * 通用查询（分页查询）
     *
     * @param tableName   表名
     * @param sqlWhere    where条件
     * @param parameters  参数 字段名：字段对应的值
     * @param sqlOrder    排序条件
     * @param primaryKeys 主键
     * @param pageNo      第几页
     * @param pageSize    页大小
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public List<Map<String, String>> getPageRows(String tableName, String sqlWhere, List<Map.Entry<String, String>> parameters, String sqlOrder, List<String> primaryKeys,
                                                 int pageNo, int pageSize) throws DaoException, SystemException {
        return getPageRows(tableName,null,sqlWhere,parameters,sqlOrder,primaryKeys,pageNo,pageSize,null);
    }

    /**
     * 通用查询（分页查询）
     *
     * @param tableName   表名
     * @param fieldNames  字段名称 所有可以传 null或"*"
     * @param sqlWhere    where条件
     * @param parameters  参数 字段名：字段对应的值
     * @param sqlOrder    排序条件
     * @param primaryKeys 主键
     * @param pageNo      第几页
     * @param pageSize    页大小
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public List<Map<String, String>> getPageRows(String tableName, String fieldNames, String sqlWhere, List<Map.Entry<String, String>> parameters, String sqlOrder, List<String> primaryKeys,
                                                 int pageNo, int pageSize) throws DaoException, SystemException {
        return getPageRows(tableName,fieldNames,sqlWhere,parameters,sqlOrder,primaryKeys,pageNo,pageSize,null);
    }
    /**
     * 通用查询（分页查询）
     *
     * @param tableName   表名
     * @param fieldNames  字段名称 所有可以传 null或"*"
     * @param sqlWhere    where条件
     * @param parameters  参数 字段名：字段对应的值
     * @param sqlOrder    排序条件
     * @param primaryKeys 主键
     * @param pageNo      第几页
     * @param pageSize    页大小
     * @param attaPathVo  附件
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     */
    public List<Map<String, String>> getPageRows(String tableName, String fieldNames, String sqlWhere, List<Map.Entry<String, String>> parameters, String sqlOrder, List<String> primaryKeys,
                                                 int pageNo, int pageSize, AttaPathVo attaPathVo) throws DaoException, SystemException {
        final String SELECT_ALL = "*";
        if (StringUtils.isBlank(fieldNames)) {
            fieldNames = SELECT_ALL;
            logger.warn("参数[fieldNames]为空，自动转换为{}", new Object[]{SELECT_ALL});
        }
        int offset = (pageNo - 1) * pageSize + 1;
        List<Map<String, String>> fieldValues = Lists.newArrayList();
        ResultVo resultVo = extractDAO.getRows(tableName, fieldNames, sqlWhere, parameters, sqlOrder, primaryKeys, null, offset, pageSize, 0, false, attaPathVo);
        if (resultVo != null && Collections3.isNotEmpty(resultVo.getRows())) {
            for (Map<String, String> row : resultVo.getRows()) {
                Set<String> rowKeys = row.keySet();
                Map<String, String> newRow = Maps.newHashMap();
                for (String rowKey : rowKeys) {
                    newRow.put(StringUtils.substringAfter(rowKey, "."), row.get(rowKey));
                }
                fieldValues.add(newRow);
            }
        }

        return fieldValues;
    }


}
