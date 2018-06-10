/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.notice.dao;

import com.eryansky.common.orm._enum.StatusState;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.notice._enum.MessageMode;
import com.eryansky.modules.notice.mapper.MessageReceive;

import java.util.List;
import java.util.Map;


/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-14 
 */
@MyBatisDao
public interface MessageReceiveDao extends CrudDao<MessageReceive> {
    /**
     * 用户消息
     * @param parameter status {@link StatusState}
     *                  mode {@link MessageMode}
     *                  userId 用户ID
     * @return
     */
    List<MessageReceive> findUserList(Parameter parameter);

    int setUserMessageRead(MessageReceive messageReceive);
    
    List<MessageReceive> findListByCategoryAndUserId(Parameter parameter);

    int deleteByMessageId(Parameter parameter);
}

