/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.cms.dao;


import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.persistence.CrudDao;
import com.eryansky.modules.cms.mapper.Guestbook;

/**
 * 留言DAO接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2013-8-23
 */
@MyBatisDao
public interface GuestbookDao extends CrudDao<Guestbook> {

}
