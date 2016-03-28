/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys.dao.VersionLogDao;
import com.eryansky.modules.sys.mapper.VersionLog;
import com.eryansky.utils.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 版本更新日志
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-29 
 */
@Service
@Transactional(readOnly = true)
public class VersionLogService extends CrudService<VersionLogDao,VersionLog>{

    /**
     * 删除
     * @param id
     */
    @Transactional(readOnly = false)
    public void delete(String id) {
        dao.delete(new VersionLog(id));
    }

    public Page<VersionLog> findPage(Page<VersionLog> page, Parameter parameter) {
        parameter.put(BaseInterceptor.PAGE,page);
        parameter.put(BaseInterceptor.DB_NAME, AppConstants.getJdbcType());
        page.setResult(dao.findQueryList(parameter));
        return page;
    }

    /**
     * 清空所有更新日志数据
     */
    @Transactional(readOnly = false)
    public void removeAll(){
        int reslutCount = dao.removeAll();
        logger.debug("清空版本更新日志：{}",reslutCount);
    }
    /**
     * 根据版本号查找
     * @param versionLogType {@link com.eryansky.modules.sys._enum.VersionLogType}
     * @param versionCode
     * @return
     */
    public VersionLog getByVersionCode(Integer versionLogType,String versionCode) {
        VersionLog versionLog = new VersionLog();
        versionLog.setVersionLogType(versionLogType);
        versionLog.setVersionCode(versionCode);
        return dao.getByVersionCode(versionLog);
    }

    /**
     * 获取当前最新版本
     * @param versionLogType
     * @return
     */
    public VersionLog getLatestVersionLog(Integer versionLogType) {
        return dao.getLatestVersionLog(versionLogType);
    }
}
