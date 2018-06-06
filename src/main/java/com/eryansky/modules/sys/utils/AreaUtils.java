/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.sys.mapper.Area;
import com.eryansky.modules.sys.service.AreaService;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-12
 */
public class AreaUtils {

    private static AreaService areaService = SpringContextHolder.getBean(AreaService.class);

    /**
     * @param areaId 区域ID
     * @return
     */
    public static Area get(String areaId){
        if(StringUtils.isNotBlank(areaId)){
            return areaService.get(areaId);
        }
        return null;
    }


    /**
     * @param code 区域编码
     * @return
     */
    public static Area getByCode(String code){
        if(StringUtils.isNotBlank(code)){
            return areaService.getByCode(code);
        }
        return null;
    }


    /**
     * 查找名称
     * @param areaId 区域编码
     * @return
     */
    public static String getAreaName(String areaId){
        Area area = get(areaId);
        if(area != null){
            return area.getName();
        }
        return null;
    }

}
