/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.mapper.OrganExtend;
import com.eryansky.modules.sys.service.OrganService;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-25
 */
public class OrganUtils {

    private static OrganService organService = SpringContextHolder.getBean(OrganService.class);

    /**
     * 根据机构ID查找机构
     * @param organId 机构ID
     * @return
     */
    public static Organ getOrgan(String organId){
        if(StringUtils.isBlank(organId)){
            return null;
        }
        return organService.get(organId);
    }


    /**
     * 根据机构ID查找
     * @param organId 机构ID
     * @return
     */
    public static OrganExtend getOrganExtend(String organId){
        if(StringUtils.isBlank(organId)){
            return null;
        }
        return organService.getOrganExtend(organId);
    }

    /**
     * 根据机构ID查找
     * @param organId 机构ID
     * @return
     */
    public static OrganExtend getOrganCompany(String organId){
        if(StringUtils.isBlank(organId)){
            return null;
        }
        return organService.getOrganCompany(organId);
    }

    /**
     * 根据用户ID查找
     * @param userId 用户ID
     * @return
     */
    public static OrganExtend getOrganExtendByUserId(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        return organService.getOrganExtendByUserId(userId);
    }

    /**
     * 根据用户ID查找
     * @param userId 用户ID
     * @return
     */
    public static OrganExtend getCompanyByUserId(String userId){
        if(StringUtils.isBlank(userId)){
            return null;
        }
        return organService.getCompanyByUserId(userId);
    }


    /**
     * 根据机构ID查找单位ID
     * @param organId 机构ID
     * @return
     */
    public static String getOrganCompanyId(String organId){
        if(StringUtils.isBlank(organId)){
            return null;
        }
        OrganExtend organExtend = getOrganCompany(organId);
        if(organExtend != null){
            return organExtend.getId();
        }
        return null;
    }

    /**
     * 根据机构ID查找单位名称
     * @param organId 机构ID
     * @return
     */
    public static String getOrganCompanyName(String organId){
        if(StringUtils.isBlank(organId)){
            return null;
        }
        OrganExtend organExtend = getOrganCompany(organId);
        if(organExtend != null){
            return organExtend.getName();
        }
        return null;
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

    public static boolean hasChild(String organId){
        List<Organ> list = organService.findByParent(organId);
        return  Collections3.isNotEmpty(list);
    }


    public static String getAreaId(String organId){
        Organ organ = getOrgan(organId);
        if(organ != null){
            return organ.getAreaId();
        }
        return null;
    }
}
