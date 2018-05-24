/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.notice.mapper.Notice;

import java.util.List;
import java.util.Map;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-10-15 
 */
@MyBatisDao
public interface NoticeDao extends CrudDao<Notice> {
    /**
     * 通知附件
     * @param noticeId
     * @return
     */
    List<String> findNoticeFiles(String noticeId);

    List<Notice> findQueryList(Map<String,Object> parameter);

    int deleteNoticeFile(Notice notice);

    int insertNoticeFile(Notice notice);
}
