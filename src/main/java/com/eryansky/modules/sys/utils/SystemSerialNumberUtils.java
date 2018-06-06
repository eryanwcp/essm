/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.sys.mapper.SystemSerialNumber;
import com.eryansky.modules.sys.service.SystemSerialNumberService;

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
            SystemSerialNumber systemSerialNumber = new SystemSerialNumber();
            systemSerialNumber.setModuleCode(moduleCode);
            return systemSerialNumberService.find(systemSerialNumber);
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
     * 生成序列号
     * @param moduleCode 模块编码
     * @return
     */
    public static String generateSerialNumberByModelCode(String moduleCode){
        return systemSerialNumberService.generateSerialNumberByModelCode(moduleCode);
    }


}
