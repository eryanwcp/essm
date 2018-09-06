/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.orm.mybatis.service.BaseService;
import com.eryansky.modules.sys.dao.SystemDao;
import com.eryansky.modules.sys.mapper.Organ;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统任务
 * @author 温春平@wencp jfwencp@jx.tobacco.gov.cn
 * @date 2017-09-19
 */
@Service
public class SystemService extends BaseService {

    @Autowired
    private SystemDao systemDao;
    @Autowired
    private OrganService organService;


    /**
     * organ表同步到扩展表
     * @return
     */
    @Deprecated
    public int insertToOrganExtend(){
        return insertToOrganExtend(null);
    }

    /**
     * organ表同步到扩展表
     * @param parameter 参数
     * @return
     */
    public int insertToOrganExtend(Parameter parameter){
        return systemDao.insertToOrganExtend(parameter);
    }

    /**
     * 删除organ扩展表数据
     * @return
     */
    @Deprecated
    public int deleteOrganExtend(){
        return deleteOrganExtend(null);
    }

    /**
     * 删除organ扩展表数据
     * @param parameter 参数
     * @return
     */
    public int deleteOrganExtend(Parameter parameter){
        return systemDao.deleteOrganExtend(parameter);
    }

    /**
     * 同步数据到organ表
     */
    public void syncOrganToExtend(){
        String dbType = AppConstants.getJdbcType();
        if("mysql".equalsIgnoreCase(dbType) || "mariadb".equalsIgnoreCase(dbType)){
            syncOrganToExtend(null);//使用存储过程
        }else{
            List<Organ> list = organService.findAllWithDelete();
            for(Organ organ:list){
                Parameter parameter = Parameter.newParameter();
                parameter.put("id",organ.getId());
                parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
                parameter.put("companyId", OrganUtils.getCompanyIdByRecursive(organ.getId()));
                parameter.put("companyCode", OrganUtils.getCompanyCodeByRecursive(organ.getId()));
                parameter.put("homeCompanyId", OrganUtils.getHomeCompanyIdByRecursive(organ.getId()));
                parameter.put("homeCompanyCode", OrganUtils.getHomeCompanyCodeByRecursive(organ.getId()));
                Integer level = StringUtils.isNotBlank(organ.getParentIds()) ? organ.getParentIds().split(",").length:null;
                parameter.put("level", level);
                syncOrganToExtend(parameter);
            }
        }
    }

    /**
     * 同步数据到organ表
     */
    public void syncOrganToExtend(Parameter parameter){
        deleteOrganExtend(parameter);
        insertToOrganExtend(parameter);
    }

}
