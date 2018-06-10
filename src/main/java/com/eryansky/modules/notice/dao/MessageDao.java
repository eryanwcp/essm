/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.notice.mapper.Message;


/**
 * 消息DAO接口
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-03-14 
 */
@MyBatisDao
public interface MessageDao extends CrudDao<Message> {
}

