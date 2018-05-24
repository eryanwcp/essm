/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.Page;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys.dao.SystemSerialNumberDao;
import com.eryansky.modules.sys.mapper.SystemSerialNumber;
import com.eryansky.modules.sys.sn.GeneratorConstants;
import com.eryansky.modules.sys.sn.SNGenerateApp;
import com.eryansky.utils.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-07-14 
 */
@Service
@Transactional(readOnly = true)
public class SystemSerialNumberService extends CrudService<SystemSerialNumberDao, SystemSerialNumber> implements ISerialNumService{

    @Autowired
    private SystemSerialNumberDao dao;


    @Override
    public Page<SystemSerialNumber> findPage(Page<SystemSerialNumber> page, SystemSerialNumber entity) {
        entity.setEntityPage(page);
        page.setResult(dao.findList(entity));
        return page;
    }


    /** 生成器锁 */
    private final ReentrantLock lock = new ReentrantLock();


    /** 预生成锁 */
    private final ReentrantLock prepareLock = new ReentrantLock();

    /** 最小值 */
    private int min = 0;

    /** 最大值 */
    private long max = 0;

    /** 预生成数量 */
    private int prepare = 0;

    /** 数据库存储的当前最大序列号 **/
    long maxSerialInt = 0;

    SystemSerialNumber systemSerialNumber =  new SystemSerialNumber();

    /** 预生成流水号 */
    HashMap<String,List<String>> prepareSerialNumberMap = new HashMap<String,List<String>>();

    /**
     * 查询单条序列号配置信息
     * @param systemSerialNumber
     * @return
     */
    @Override
    public SystemSerialNumber find(SystemSerialNumber systemSerialNumber) {
        return dao.find(systemSerialNumber);
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
        //临时List变量
        List<String> resultList = new ArrayList<String>(prepare);
        lock.lock();
        try{
            for(int i=0;i<prepare;i++){
                maxSerialInt ++;
                SNGenerateApp snGenerateApp = new SNGenerateApp();
                Map map = new HashMap(); //设定参数
                map.put(GeneratorConstants.PARAM_MODULE_CODE, moduleCode);
                String formatSerialNum = snGenerateApp.generateSN(systemSerialNumber.getConfigTemplate(),map);
                //更新数据
                systemSerialNumber.setMaxSerial(maxSerialInt + "");
                this.save(systemSerialNumber);
                resultList.add(formatSerialNum);
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);

        }finally{
            lock.unlock();
        }
        return resultList;
    }

    /**
     * 根据模块code生成序列号
     * @param moduleCode  模块code
     * @return  序列号
     */
    public String generateSerialNumberByModelCode(String moduleCode){

        //预序列号加锁
        prepareLock.lock();
        try{
            //判断内存中是否还有序列号
            if(null != prepareSerialNumberMap.get(moduleCode) && prepareSerialNumberMap.get(moduleCode).size() > 0){
                //若有，返回第一个，并删除
                return prepareSerialNumberMap.get(moduleCode).remove(0);
            }
        }finally {
            //预序列号解锁
            prepareLock.unlock();
        }
        systemSerialNumber = new SystemSerialNumber();
        systemSerialNumber.setModuleCode(moduleCode);
        systemSerialNumber = dao.find(systemSerialNumber);
        prepare = Integer.parseInt(systemSerialNumber.getPreMaxNum().trim());//预生成流水号数量
        String maxSerial = systemSerialNumber.getMaxSerial().trim(); //存储当前最大值
        maxSerialInt = Long.parseLong(maxSerial.trim());//数据库存储的最大序列号
        //生成预序列号，存到缓存中
        List<String> resultList = generatePrepareSerialNumbers(moduleCode);
        prepareLock.lock();
        try {
            prepareSerialNumberMap.put(moduleCode, resultList);
            return prepareSerialNumberMap.get(moduleCode).remove(0);
        } finally {
            prepareLock.unlock();
        }
    }

    /**
     * 设置最小值
     *
     * @param value 最小值，要求：大于等于零
     * @return 流水号生成器实例
     */
    public ISerialNumService setMin(int value) {
        lock.lock();
        try {
            this.min = value;
        }finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 最大值
     *
     * @param value 最大值，要求：小于等于Long.MAX_VALUE ( 9223372036854775807 )
     * @return 流水号生成器实例
     */
    public ISerialNumService setMax(long value) {
        lock.lock();
        try {
            this.max = value;
        }finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 设置预生成流水号数量
     * @param count 预生成数量
     * @return      流水号生成器实例
     */
    public ISerialNumService setPrepare(int count) {
        lock.lock();
        try {
            this.prepare = count;
        }finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 统计某一个字符出现的次数
     * @param str 查找的字符
     * @param c
     * @return
     */
    private int counter(String str,char c){
        int count=0;
        for(int i = 0;i < str.length();i++){
            if(str.charAt(i)==c){
                count++;
            }
        }
        return count;
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
}
