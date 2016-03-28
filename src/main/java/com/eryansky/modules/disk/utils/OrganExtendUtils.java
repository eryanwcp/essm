/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.disk.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.google.common.collect.Lists;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.service.OrganManager;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-02-03
 */
public class OrganExtendUtils {

    private static OrganManager organManager = SpringContextHolder.getBean(OrganManager.class);

    private OrganExtendUtils(){

    }
    /**
     * 部门领导 部门主管、分管领导
     * @param organId 机构ID
     * @return
     */
    public static List<String> getLeaderUser(String organId){
        List<String> list = Lists.newArrayList();
        Organ organ = organManager.loadById(organId);
        list.add(organ.getManagerUserId());
        list.add(organ.getSuperManagerUserId());
        return list;
    }
}
