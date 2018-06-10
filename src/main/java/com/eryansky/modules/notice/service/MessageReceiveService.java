/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.notice._enum.MessageMode;
import com.eryansky.modules.notice.dao.MessageReceiveDao;
import com.eryansky.modules.notice.mapper.Message;
import com.eryansky.modules.notice.mapper.MessageReceive;
import com.eryansky.modules.sys._enum.YesOrNo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-14 
 */
@Service
@Transactional(readOnly = true)
public class MessageReceiveService extends CrudService<MessageReceiveDao, MessageReceive> {

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
    public Page<MessageReceive> findPage(Page<MessageReceive> page, MessageReceive entity) {
        entity.setEntityPage(page);
        page.setResult(dao.findList(entity));
        return page;
    }

    /**
     * 用户消息
     * @param page
     * @param userId 用户ID
     * @return
     */
    public Page<MessageReceive> findUserPage(Page<MessageReceive> page, String userId, String isRead) {
        Parameter parameter = new Parameter();
        parameter.put(BaseInterceptor.PAGE,page);
        parameter.put(Message.FIELD_STATUS,Message.STATUS_NORMAL);
        parameter.put("mode", MessageMode.Published.getValue());
        parameter.put("userId",userId);
        parameter.put("isRead",isRead);
        page.setResult(dao.findUserList(parameter));
        return page;
    }

    /**
     * 设置通知已读状态
     * @param receive
     */
    @Transactional(readOnly = false)
    public void setRead(MessageReceive receive){
        receive.setIsRead(YesOrNo.YES.getValue());
        receive.setReadTime(Calendar.getInstance().getTime());
        this.save(receive);
    }

    /**
     * 设置通知已读状态
     * @param userId
     * @param isRead
     */
    @Transactional(readOnly = false)
    public void setReadAll(String userId,String isRead){
        MessageReceive receive = new MessageReceive();
        receive.setUserId(userId);
        receive.setIsRead(StringUtils.isBlank(isRead) ? YesOrNo.YES.getValue():isRead);
        receive.setReadTime(Calendar.getInstance().getTime());
        dao.setUserMessageRead(receive);
    }

    
    public List<MessageReceive> findListByCategoryAndUserId(String category,String userId){
        Parameter parameter = Parameter.newParameter();
        parameter.put("category",category);
        parameter.put("userId",userId);
    	return dao.findListByCategoryAndUserId(parameter);
    }

}
