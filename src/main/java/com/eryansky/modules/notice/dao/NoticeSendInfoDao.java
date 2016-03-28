/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.persistence.CrudDao;
import com.eryansky.modules.notice.mapper.NoticeSendInfo;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-10-15 
 */
@MyBatisDao
public interface NoticeSendInfoDao extends CrudDao<NoticeSendInfo> {

    List<NoticeSendInfo> findQueryList(NoticeSendInfo noticeSendInfo);
}
