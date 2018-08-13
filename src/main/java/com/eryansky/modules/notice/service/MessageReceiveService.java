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
import com.eryansky.modules.weixin.utils.WeixinUtils;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-03-14 
 */
@Service
public class MessageReceiveService extends CrudService<MessageReceiveDao, MessageReceive> {


    /**
     * 根据消息ID删除
     *
     * @param messageId
     * @return
     */
    public int deleteByMessageId(String messageId) {
        Parameter parameter = Parameter.newParameter();
        parameter.put("messageId", messageId);
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
     *
     * @param page
     * @param userId 用户ID
     * @param isRead
     * @return
     */
    public Page<MessageReceive> findUserPage(Page<MessageReceive> page, String userId, String isRead) {
        return findUserPage(page, userId, isRead, null);
    }

    /**
     * 用户消息
     *
     * @param page
     * @param userId 用户ID
     * @param isRead
     * @param isSend
     * @return
     */
    public Page<MessageReceive> findUserPage(Page<MessageReceive> page, String userId, String isRead, String isSend) {
        Parameter parameter = new Parameter();
        parameter.put(BaseInterceptor.PAGE, page);
        parameter.put(Message.FIELD_STATUS, Message.STATUS_NORMAL);
        parameter.put("mode", MessageMode.Published.getValue());
        parameter.put("userId", userId);
        parameter.put("isRead", isRead);
        parameter.put("isSend", isSend);
        page.setResult(dao.findUserList(parameter));
        return page;
    }

    /**
     * 用户未发送成功的消息
     *
     * @param userId
     * @return
     */
    public List<MessageReceive> findUnSendByUserId(String userId) {
        Parameter parameter = new Parameter();
        parameter.put(Message.FIELD_STATUS, Message.STATUS_NORMAL);
        parameter.put("mode", MessageMode.Published.getValue());
        parameter.put("userId", userId);
        return dao.findUserList(parameter);
    }

    /**
     * 设置通知已读状态
     *
     * @param receive
     */
    public void setRead(MessageReceive receive) {
        receive.setIsRead(YesOrNo.YES.getValue());
        receive.setReadTime(Calendar.getInstance().getTime());
        receive.setIsSend(YesOrNo.YES.getValue());
        this.save(receive);
    }

    /**
     * 设置通知已读状态
     *
     * @param userId
     * @param isRead
     */
    public void setReadAll(String userId, String isRead) {
        MessageReceive receive = new MessageReceive();
        receive.setUserId(userId);
        receive.setIsRead(StringUtils.isBlank(isRead) ? YesOrNo.YES.getValue() : isRead);
        receive.setReadTime(Calendar.getInstance().getTime());
        receive.setIsSend(YesOrNo.YES.getValue());
        dao.setUserMessageRead(receive);
    }


    /**
     * 重新推送微信端
     *
     * @param userId
     */
    public void reSendByUserId(String userId) {
        List<MessageReceive> list = findUnSendByUserId(userId);
        for (MessageReceive messageReceive : list) {
            reSendByMessageReceive(messageReceive);
        }
    }

    /**
     * 重新推送微信端
     *
     * @param messageReceive
     */
    private void reSendByMessageReceive(MessageReceive messageReceive) {
        Message message = messageReceive.getMessage();
        boolean flag = WeixinUtils.sendTextMsg(messageReceive.getUserId(), message.getContent(), message.getUrl());
        messageReceive.setIsSend(flag ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
        save(messageReceive);
    }


    /**
     * 重新推送微信端
     *
     * @param messageReceiveId
     */
    public void reSendByMessageReceiveId(String messageReceiveId) {
        MessageReceive messageReceive = get(messageReceiveId);
        Message message = messageReceive.getMessage();
        boolean flag = WeixinUtils.sendTextMsg(messageReceive.getUserId(), message.getContent(), message.getUrl());
        messageReceive.setIsSend(flag ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue());
        save(messageReceive);
    }
}
