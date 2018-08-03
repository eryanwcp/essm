/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.mapper.SystemSerialNumber;
import com.eryansky.modules.sys.service.SystemSerialNumberService;
import com.eryansky.utils.CacheConstants;
import com.eryansky.utils.CacheUtils;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-12
 */
public class SystemSerialNumberUtils {

    private static SystemSerialNumberService systemSerialNumberService = SpringContextHolder.getBean(SystemSerialNumberService.class);

    /**
     * @param id
     * @return
     */
    public static SystemSerialNumber get(String id){
        if(StringUtils.isNotBlank(id)){
            return systemSerialNumberService.get(id);
        }
        return null;
    }

    /**
     * @param moduleCode
     * @return
     */
    public static SystemSerialNumber getByModuleCode(String moduleCode){
        if(StringUtils.isNotBlank(moduleCode)){
            return systemSerialNumberService.getByCode(moduleCode);
        }
        return null;
    }

    /**
     * 获得当前最大值
     * @param moduleCode
     * @return
     */
    public static String getMaxSerialByModuleCode(String moduleCode){
        SystemSerialNumber systemSerialNumber = getByModuleCode(moduleCode);
        if(systemSerialNumber != null){
            return systemSerialNumber.getMaxSerial();
        }
        return null;
    }

    /**
     * 根据模块code生成序列号
     * @param moduleCode  模块code
     * @return  序列号
     */
    public static String generateSerialNumberByModelCode(String moduleCode){
        synchronized (moduleCode.intern()){
            List<String> preData = (List<String>)CacheUtils.get(CacheConstants.SYS_SERIAL_NUMBER_CACHE,moduleCode);
            if(Collections3.isNotEmpty(preData)){
                String result = preData.remove(0);
                CacheUtils.put(CacheConstants.SYS_SERIAL_NUMBER_CACHE,moduleCode,preData);
                return result;
            }
            preData = systemSerialNumberService.generatePrepareSerialNumbers(moduleCode);
            String result = preData.remove(0);
            CacheUtils.put(CacheConstants.SYS_SERIAL_NUMBER_CACHE,moduleCode,preData);
            return result;
        }
    }

}
