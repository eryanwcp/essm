/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.notice.dao;

import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.notice.mapper.MessageSender;


/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-14 
 */
@MyBatisDao
public interface MessageSenderDao extends CrudDao<MessageSender> {

    int deleteByMessageId(Parameter parameter);

}

