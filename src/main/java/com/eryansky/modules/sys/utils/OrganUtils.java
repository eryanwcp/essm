/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.service.OrganManager;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-25
 */
public class OrganUtils {

    private static OrganManager organManager = SpringContextHolder.getBean(OrganManager.class);

    /**
     * 根据机构ID查找机构名称
     * @param organId 机构ID
     * @return
     */
    public static Organ getOrgan(String organId){
        if(StringUtils.isBlank(organId)){
            return null;
        }
        return organManager.getById(organId);
    }


    /**
     * 根据机构ID查找机构名称
     * @param organId 机构ID
     * @return
     */
    public static String getOrganName(String organId){
        Organ organ = getOrgan(organId);
        if(organ != null){
            return organ.getName();
        }
        return null;
    }

    /**
     * 根据机构ID集合转换成机构名称
     * @param organIds 机构ID集合
     * @return
     */
    public static String getOrganNames(List<String> organIds){
        if(Collections3.isNotEmpty(organIds)){
            List<Organ> list = organManager.findOrgansByIds(organIds);
            return ConvertUtils.convertElementPropertyToString(list, "name", ", ");
        }
        return null;
    }

    public static String getAreaId(String organId){
        Organ organ = getOrgan(organId);
        if(organ != null){
            return organ.getAreaId();
        }
        return null;
    }
}
