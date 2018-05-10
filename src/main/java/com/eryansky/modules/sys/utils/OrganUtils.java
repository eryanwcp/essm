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
import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.mapper.OrganExtend;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.OrganService;
import com.eryansky.modules.sys.service.SystemService;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-25
 */
public class OrganUtils {

    private static OrganService organService = SpringContextHolder.getBean(OrganService.class);
    private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);

    /**
     * 根据机构ID查找机构名称
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
        return systemService.getOrganExtend(organId);
    }


    /**
     * 根据机构ID查找
     * @param organId 机构ID
     * @return
     */
    public static OrganExtend getOrganExtendCompany(String organId){
        OrganExtend organExtend = getOrganExtend(organId);
        return getOrganExtend(organExtend.getCompanyId());
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
        return systemService.getOrganExtendByUserId(userId);
    }

    public static OrganExtend getOrganExtendCompanyByUserId(String userId){
        OrganExtend organExtend = getOrganExtendByUserId(userId);
        return getOrganExtend(organExtend.getCompanyId());
    }


    /**
     * 根据机构ID查找机构名称
     * @param organId 机构ID
     * @return
     */
    public static String getOrganCompanyId(String organId){
        OrganExtend organExtend = getOrganExtend(organId);
        if(organExtend != null){
            return organExtend.getCompanyId();
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

    /**
     * 根据机构ID集合转换成机构名称
     * @param organIds 机构ID集合
     * @return
     */
    public static String getOrganNames(List<String> organIds){
        if(Collections3.isNotEmpty(organIds)){
            List<Organ> list = organService.findOrgansByIds(organIds);
            return ConvertUtils.convertElementPropertyToString(list, "name", ", ");
        }
        return null;
    }

    public static boolean hasChild(String organId){
        List<Organ> list = organService.findByParent(organId);
        return  Collections3.isEmpty(list) ? false:true;
    }

    public static List<String> findChildsOrganIds(String organId){
        return organService.findChildsOrganIds(organId);
    }


    public static String getAreaId(String organId){
        Organ organ = getOrgan(organId);
        if(organ != null){
            return organ.getAreaId();
        }
        return null;
    }
}
