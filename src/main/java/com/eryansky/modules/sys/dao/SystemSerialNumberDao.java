/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
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

