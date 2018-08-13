/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.modules.weixin.utils.QYWeixinUtils;
import com.eryansky.modules.weixin.utils.WeixinUtils;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.notice._enum.MessageMode;
import com.eryansky.modules.notice._enum.MessageReceiveObjectType;
import com.eryansky.modules.notice.dao.MessageDao;
import com.eryansky.modules.notice.mapper.Message;
import com.eryansky.modules.notice.mapper.MessageReceive;
import com.eryansky.modules.notice.mapper.MessageSender;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.eryansky.modules.sys.service.UserService;
import org.aspectj.weaver.MemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-03-14 
 */
@Service
public class MessageService extends CrudService<MessageDao, Message> {

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MessageReceiveService messageReceiveService;
    @Autowired
    private UserService userService;


    @Override
    public Page<Message> findPage(Page<Message> page, Message entity) {
        entity.getSqlMap().put("dsf",super.dataScopeFilter(SecurityUtils.getCurrentUser(), "o", "u"));//数据权限控制
        entity.setEntityPage(page);
        page.setResult(dao.findList(entity));
        return page;
    }


    /**
     * 删除
     * @param entity
     * @param isRe 是否恢复删除
     */
    public void delete(Message entity, Boolean isRe) {
        if(isRe != null && isRe){
            entity.setStatus(Message.STATUS_NORMAL);
            super.save(entity);
        }else{
            super.delete(entity);
        }
    }

    /**
     * 保存并发送
     * @param message
     * @param messageReceiveObjectType
     * @param receiveObjectIds
     * @param sendWeixin
     */
    public void saveAndSend(Message message, MessageReceiveObjectType messageReceiveObjectType, List<String> receiveObjectIds,Boolean sendWeixin){
        if(Collections3.isNotEmpty(receiveObjectIds)){
            message.setMode(MessageMode.Publishing.getValue());
            message.setSendTime(Calendar.getInstance().getTime());
            this.save(message);
            for(String objectId: receiveObjectIds){
                MessageSender messageSender = new MessageSender(message.getId());
                messageSender.setObjectType(messageReceiveObjectType.getValue());
                messageSender.setObjectId(objectId);
                messageSenderService.save(messageSender);

                List<String> targetIds = Lists.newArrayList();
                if(MessageReceiveObjectType.User.equals(messageReceiveObjectType)){
                    targetIds.add(UserUtils.getLoginName(objectId));
                }else if(MessageReceiveObjectType.Organ.equals(messageReceiveObjectType)){
                    targetIds = userService.findUsersLoginNamesByOrganId(objectId);
                }else if(MessageReceiveObjectType.Member.equals(messageReceiveObjectType)){
                    String openid = objectId;//TODO 获取openid
                    targetIds.add(openid);
                }
                for(String targetId:targetIds){
                    MessageReceive messageReceive = new MessageReceive(message.getId());
                    messageReceive.setUserId(targetId);
                    messageReceive.setIsRead(YesOrNo.NO.getValue());

                    //通过微信发送消息
                    if(sendWeixin != null && sendWeixin){
//                        QYWeixinUtils.sendTextMsg(null,targetId, message.getContent(), message.getUrl());
                        boolean flag = WeixinUtils.sendTextMsg(targetId,message.getContent(),message.getUrl());
                        if(!flag){
                            messageReceive.setIsSend(YesOrNo.NO.getValue());
                        }
                    }
                    messageReceiveService.save(messageReceive);
                }


            }
            message.setMode(MessageMode.Published.getValue());
            this.save(message);
        }
    }
}
