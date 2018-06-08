/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys.dao.LogDao;
import com.eryansky.modules.sys.mapper.Log;
import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.mapper.OrganExtend;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.method.HandlerMethod;

import java.util.*;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-26
 */
@Service
@Transactional(readOnly = true)
public class LogService extends CrudService<LogDao, Log> {

    @Autowired
    private LogDao dao;


    @Override
    public Page<Log> findPage(Page<Log> page, Log entity) {
        entity.setEntityPage(page);
        page.setResult(dao.findList(entity));
        return page;
    }

    /**
     * 删除
     * @param ids
     */
    @Transactional(readOnly = false)
    public void deleteByIds(Collection<String> ids){
        if(Collections3.isNotEmpty(ids)){
            for(String id :ids){
                deleteById(id);
            }
        }else{
            logger.warn("参数[ids]为空.");
        }
    }
    /**
     * 删除
     * @param id
     */
    public void deleteById(String id){
        dao.delete(new Log(id));
    }


    @Transactional(readOnly = false)
    public int remove(String id){
        int reslutCount = dao.remove(id);
        logger.debug("清除日志：{}",reslutCount);
        return reslutCount;
    }

    /**
     * 清空所有日志
     * @return
     */
    @Transactional(readOnly = false)
    public int removeAll(){
        int reslutCount = dao.removeAll();
        logger.debug("清空日志：{}",reslutCount);
        return reslutCount;
    }

    /**
     * 清空有效期之外的日志
     * @param  day 保留时间 （天）
     * @throws DaoException
     * @throws SystemException
     */
    @Transactional(readOnly = false)
    public int clearInvalidLog(int day){
        if(day <0){
            throw new SystemException("参数[day]不合法，需要大于0.输入为："+day);
        }
        Date now = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(now); // 得到gc格式的时间
        gc.add(5, -day); // 2表示月的加减，年代表1依次类推(３周....5天。。)
        // 把运算完的时间从新赋进对象
        gc.set(gc.get(gc.YEAR), gc.get(gc.MONTH), gc.get(gc.DATE));

        Parameter parameter = new Parameter();
        parameter.put("operTime", DateUtils.format(gc.getTime(), DateUtils.DATE_FORMAT));
        int result = dao.clearInvalidLog(parameter);
        return result;
    }


    /**
     * 清空有效期之外的日志
     * @param  day 保留时间 （天）
     * @throws DaoException
     * @throws SystemException
     */
    @Transactional(readOnly = false)
    public void insertToHistoryAndClear(int day){
        if(day <0){
            throw new SystemException("参数[day]不合法，需要大于0.输入为："+day);
        }
        Date now = new Date();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(now); // 得到gc格式的时间
        gc.add(5, -day); // 2表示月的加减，年代表1依次类推(３周....5天。。)
        // 把运算完的时间从新赋进对象
        gc.set(gc.get(gc.YEAR), gc.get(gc.MONTH), gc.get(gc.DATE));

        Parameter parameter = new Parameter();
        parameter.put("createTime", DateUtils.format(gc.getTime(), DateUtils.DATE_FORMAT));
        logger.info("备份日志表开始：{}",DateUtils.format(gc.getTime(), DateUtils.DATE_FORMAT));
        int countInsert = dao.insertToHistory(parameter);//插入历史表
        logger.info("备份日志表结束:{}",new Object[]{countInsert});
        logger.info("删除日志表开始：{}",DateUtils.format(gc.getTime(), DateUtils.DATE_FORMAT));
        int countDelete = dao.clearInvalidLog(parameter);//删除数据
        logger.info("删除日志表结束:{}",new Object[]{countDelete});
    }

    /**
     * 查询用户登录次数
     * @param userId
     * @return
     */
    public Long getUserLoginCount(String userId){
        return getUserLoginCount(userId,null,null);
    }
    /**
     * 查询用户登录次数
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    public Long getUserLoginCount(String userId,Date startTime,Date endTime){
        Parameter parameter = new Parameter();
        parameter.put("userId", userId);
        if(startTime != null){
            parameter.put("startTime",DateUtils.format(startTime, DateUtils.DATE_TIME_FORMAT));
        }
        if(startTime != null){
            parameter.put("endTime",DateUtils.format(endTime, DateUtils.DATE_TIME_FORMAT));
        }
        return dao.getUserLoginCount(parameter);
    }

    /**
     * 数据修复 title
     */
    public void dataAutoFix(){
        List<Log> list = dao.findNullData();
        if(Collections3.isNotEmpty(list)){
            for(Log log:list){
                Log _log = dao.getNotNullData(log.getModule());
                if(_log != null && StringUtils.isBlank(log.getTitle())){
                    log.setTitle(_log.getTitle());
                    dao.update(log);
                }
            }
        }

    }

    /**
     * 员工登录统计
     */
    public Page<Map<String,Object>> getLoginStatistics(Page pg, String name, String startTime, String endTime){
        Parameter parameter = Parameter.newParameter();
        if(StringUtils.isNotBlank(startTime)){
            parameter.put("startTime", startTime + " 00:00:00");
        }
        if(StringUtils.isNotBlank(endTime)){
            parameter.put("endTime", endTime + " 23:59:59");
        }
        parameter.put("name", name);
        parameter.put(BaseInterceptor.PAGE,pg);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        parameter.put(Log.FIELD_STATUS, Log.STATUS_NORMAL);
        List<Map<String,Object>> mapList= dao.getLoginStatistics(parameter);
        if(Collections3.isNotEmpty(mapList)){
            for(Map<String,Object> map:mapList){
                OrganExtend organ = OrganUtils.getOrganCompany((String) map.get("organId"));
                map.put("company", organ != null ? organ.getName():(String) map.get("name"));
            }
        }
        pg.setResult(mapList);
        return pg;
    }

    /**
     * 模块访问统计
     */
    public Page<Map<String,Object>> getModuleStatistics(Page pg, String objectIds,String organId,Boolean onlyCompany, String startTime, String endTime,String postCode){
        Parameter parameter = new Parameter();
        if(StringUtils.isNotBlank(startTime)){
            parameter.put("startTime", startTime + " 00:00:00");
        }
        if(StringUtils.isNotBlank(endTime)){
            parameter.put("endTime", endTime + " 23:59:59");
        }
        if (StringUtils.isNotBlank(objectIds)){
            String [] objectId = objectIds.split(",");
            parameter.put("objectIds", objectId);
        }
        parameter.put("organId", organId);
        parameter.put("onlyCompany", onlyCompany);
        parameter.put("postCode", postCode);
        parameter.put(BaseInterceptor.PAGE,pg);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        List<Map<String,Object>> mapList= dao.getModuleStatistics(parameter);
        pg.setResult(mapList);
        return pg;
    }

    /**
     * 每天访问数据
     * @return
     */
    public List<Map<String,Object>> getDayLoginStatistics(String startTime, String endTime){
        Parameter parameter = new Parameter();
        if(StringUtils.isNotBlank(startTime)){
            startTime+=" 00:00:00";
            parameter.put("startTime", startTime);
        }
        if(StringUtils.isNotBlank(endTime)){
            endTime+=" 23:59:59";
            parameter.put("endTime", endTime);
        }
//        parameter.put(BaseInterceptor.PAGE,pg);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        List<Map<String,Object>> list= dao.getDayLoginStatistics(parameter);
        return list;
    }


    /**
     * 清理过期日志
     */
    @Async
    public void saveAspectLog(Log log, HandlerMethod handler){
        save(log);
    }

}