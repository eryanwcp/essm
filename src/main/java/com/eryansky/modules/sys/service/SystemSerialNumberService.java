/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys.dao.SystemSerialNumberDao;
import com.eryansky.modules.sys.mapper.SystemSerialNumber;
import com.eryansky.modules.sys.sn.GeneratorConstants;
import com.eryansky.modules.sys.sn.SNGenerateApp;
import com.eryansky.utils.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-07-14 
 */
@Service
public class SystemSerialNumberService extends CrudService<SystemSerialNumberDao, SystemSerialNumber> {

    @Autowired
    private SystemSerialNumberDao dao;


    @Override
    public Page<SystemSerialNumber> findPage(Page<SystemSerialNumber> page, SystemSerialNumber entity) {
        entity.setEntityPage(page);
        page.setResult(dao.findList(entity));
        return page;
    }

    /**
     * 乐观锁更新方式
     * @param entity
     * @return 返回更新数 0：更新失败 1：更新成功
     */
    public void updateByVersion(SystemSerialNumber entity){
        int result = dao.updateByVersion(entity);
        if(result == 0){
            throw new ServiceException("乐观锁更新失败,"+entity.toString());
        }
    }



    /**
     * 根据模块编码查找
     * @param moduleCode
     * @return
     */
    public SystemSerialNumber getByCode(String moduleCode) {
        SystemSerialNumber entity = new SystemSerialNumber();
        entity.setModuleCode(moduleCode);
        return dao.getByCode(entity);
    }

    /**
     * 查询所有序列号配置信息
     */
    public List<SystemSerialNumber> findAll(){
        SystemSerialNumber entity = new SystemSerialNumber();
        return  dao.findAllList(entity);
    }

    /**
     * 根据模块code生成预数量的序列号存放到Map中
     * @param moduleCode 模块code
     * @return
     */
    @CachePut(value = CacheConstants.SYS_SERIAL_NUMBER_CACHE,key="#moduleCode")
    public List<String> generatePrepareSerialNumbers(String moduleCode){
        SystemSerialNumber entity = getByCode(moduleCode);
        int version = entity.getVersion();
        /** 预生成数量 */
        int prepare = StringUtils.isNotBlank(entity.getPreMaxNum()) ? Integer.valueOf(entity.getPreMaxNum()):0;
        /** 数据库存储的当前最大序列号 **/
        long maxSerialInt = StringUtils.isNotBlank(entity.getMaxSerial()) ? Integer.valueOf(entity.getMaxSerial()):0;
        //临时List变量
        List<String> resultList = new ArrayList<String>(prepare);
        for(int i=0;i<prepare;i++){
            SNGenerateApp snGenerateApp = new SNGenerateApp();
            Map map = new HashMap(); //设定参数
            map.put(GeneratorConstants.PARAM_MODULE_CODE, moduleCode);
            map.put(GeneratorConstants.PARAM_MAX_SERIAL, maxSerialInt+"");
            String formatSerialNum = snGenerateApp.generateSN(entity.getConfigTemplate(),map);
            maxSerialInt ++;
            //更新数据
            entity.setMaxSerial(maxSerialInt + "");
            updateByVersion(entity);
            version++;
            entity.setVersion(version);
            resultList.add(formatSerialNum);
        }
        return resultList;
    }


    /**
     * 年度重置序列号
     */
    public void clearSerialNumber(){
        List<SystemSerialNumber> numberList = this.findAll();
        for (SystemSerialNumber systemSerialNumber : numberList) {
            if (SystemSerialNumber.YES.equals(systemSerialNumber.getYearClear())) {
                systemSerialNumber.setMaxSerial("0");
                this.save(systemSerialNumber);
            }
        }
    }


    /**
     * 清空缓存
     */
    @CacheEvict(value = CacheConstants.SYS_SERIAL_NUMBER_CACHE,key="#moduleCode")
    public void clearCacheByModuleCode(String moduleCode){

    }
}
