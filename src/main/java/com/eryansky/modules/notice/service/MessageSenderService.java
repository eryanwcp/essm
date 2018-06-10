/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.notice.dao.MessageSenderDao;
import com.eryansky.modules.notice.mapper.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-14 
 */
@Service
@Transactional(readOnly = true)
public class MessageSenderService extends CrudService<MessageSenderDao, MessageSender> {


    /**
     * 根据消息ID删除
     * @param messageId
     * @return
     */
    public int deleteByMessageId(String messageId){
        Parameter parameter = Parameter.newParameter();
        parameter.put("messageId",messageId);
        return dao.deleteByMessageId(parameter);
    }

    @Override
    public Page<MessageSender> findPage(Page<MessageSender> page, MessageSender entity) {
        entity.setEntityPage(page);
        page.setResult(dao.findList(entity));
        return page;
    }
}
