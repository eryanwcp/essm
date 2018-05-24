/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.sys.mapper.SystemSerialNumber;


/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-07-14 
 */
@MyBatisDao
public interface SystemSerialNumberDao extends CrudDao<SystemSerialNumber> {

    SystemSerialNumber find(SystemSerialNumber systemSerialNumber);
}

