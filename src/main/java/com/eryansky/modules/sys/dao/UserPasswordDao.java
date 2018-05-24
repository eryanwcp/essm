/**
*  Copyright (c) 2012-2018 http://www.eryansky.com
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*/
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;

import com.eryansky.modules.sys.mapper.UserPassword;

import java.util.List;

/**
 * 用户密码修改记录
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@MyBatisDao
public interface UserPasswordDao extends CrudDao<UserPassword> {


   List<UserPassword> findByUserId(Parameter parameter);


}
