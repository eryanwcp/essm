/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.sys.mapper.VersionLog;

import java.util.List;


/**
 * 版本更新日志
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2015-9-26
 */
@MyBatisDao
public interface VersionLogDao extends CrudDao<VersionLog> {
    /**
     * 删除所有 清空
     * @return
     */
    int removeAll();

    List<VersionLog> findQueryList(Parameter parameter);

    /**
     * 根据版本号查找
     * @param versionLog
     * @return
     */
    VersionLog getByVersionCode(VersionLog versionLog);

    /**
     * 获取当前最新版本
     * @param entity
     * @return
     */
    VersionLog getLatestVersionLog(VersionLog entity);
}
