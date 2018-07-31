/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
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
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-03-14 
 */
@Service
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
    public Page<MessageReceive> findUserPage(Page<MessageReceive> page, String userId) {
        return findUserPage(page,userId,null,null);
    }

    /**
     * 用户消息
     * @param page
     * @param userId 用户ID
     * @return
     */
    public Page<MessageReceive> findUserPage(Page<MessageReceive> page, String userId, String isRead) {
        return findUserPage(page,userId,null,isRead);
    }


    /**
     * 用户消息
     * @param page
     * @param userId 用户ID
     * @param category 分类
     * @param isRead 是否已读 {@link YesOrNo}
     * @return
     */
    public Page<MessageReceive> findUserPage(Page<MessageReceive> page, String userId,String category, String isRead) {
        Parameter parameter = new Parameter();
        parameter.put(BaseInterceptor.PAGE,page);
        parameter.put(Message.FIELD_STATUS,Message.STATUS_NORMAL);
        parameter.put("mode", MessageMode.Published.getValue());
        parameter.put("userId",userId);
        parameter.put("category",category);
        parameter.put("isRead",isRead);
        page.setResult(dao.findUserList(parameter));
        return page;
    }

    /**
     * 设置通知已读状态
     * @param receive
     */
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
    public void setReadAll(String userId,String isRead){
        MessageReceive receive = new MessageReceive();
        receive.setUserId(userId);
        receive.setIsRead(StringUtils.isBlank(isRead) ? YesOrNo.YES.getValue():isRead);
        receive.setReadTime(Calendar.getInstance().getTime());
        dao.setUserMessageRead(receive);
    }

}
