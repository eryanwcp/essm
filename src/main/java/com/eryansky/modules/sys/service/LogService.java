/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys.dao.LogDao;
import com.eryansky.modules.sys.mapper.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-26
 */
@Service
@Transactional(readOnly = true)
public class LogService extends CrudService<LogDao, Log> {

    @Autowired
    private LogDao logDao;


    @Override
    public Page<Log> findPage(Page<Log> page, Log entity) {
        entity.setEntityPage(page);
        page.setResult(dao.findList(entity));
        return page;
    }

    /**
     * 删除 物理删除
     * @param ids
     */
    @Transactional(readOnly = false)
    public void deleteByIds(List<String> ids){
        if(Collections3.isNotEmpty(ids)){
            for(String id:ids){
                dao.delete(new Log(id));
            }
        }
    }

    @Transactional(readOnly = false)
    public int remove(String id){
        int reslutCount = logDao.remove(id);
        logger.debug("清除日志：{}",reslutCount);
        return reslutCount;
    }

    /**
     * 清空所有日志
     * @return
     */
    @Transactional(readOnly = false)
    public int removeAll(){
        int reslutCount = logDao.removeAll();
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
        int result = logDao.clearInvalidLog(parameter);
        return result;
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
        return logDao.getUserLoginCount(parameter);
    }

    /**
     * 数据修复 title
     */
    public void dataAutoFix(){
        List<Log> list = logDao.findNullData();
        if(Collections3.isNotEmpty(list)){
            for(Log log:list){
                Log _log = logDao.getNotNullData(log.getModule());
                if(_log != null && StringUtils.isBlank(log.getTitle())){
                    log.setTitle(_log.getTitle());
                    logDao.update(log);
                }
            }
        }

    }

}
