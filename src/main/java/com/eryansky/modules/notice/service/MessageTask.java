/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.notice._enum.MessageReceiveObjectType;
import com.eryansky.modules.notice.mapper.Message;
import com.eryansky.modules.notice.mapper.MessageReceive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息发送异步任务
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15 
 */
@Component
public class MessageTask {

    private static Logger logger = LoggerFactory.getLogger(MessageTask.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageReceiveService messageReceiveService;

    /**
     * 发送消息
     * @param message
     * @param receiveObjectType
     * @param receiveObjectIds
     */
    @Async
    public void saveAndSend(Message message, String receiveObjectType, List<String> receiveObjectIds){
        saveAndSend(message,receiveObjectType, receiveObjectIds,true);
    }
    /**
     * 发送消息
     * @param message
     * @param receiveObjectType
     * @param receiveObjectIds
     * @param sendWeixin 是否通过微信发送消息
     */
    @Async
    public void saveAndSend(Message message, String receiveObjectType, List<String> receiveObjectIds,Boolean sendWeixin){
        MessageReceiveObjectType messageReceiveObjectType = MessageReceiveObjectType.getByValue(receiveObjectType);
        if(StringUtils.isNotBlank(receiveObjectType) && messageReceiveObjectType != null){
            if(MessageReceiveObjectType.User.equals(messageReceiveObjectType)){
                saveAndSend(message, messageReceiveObjectType, receiveObjectIds,sendWeixin);
            }else if(MessageReceiveObjectType.Organ.equals(messageReceiveObjectType)){
                saveAndSend(message, messageReceiveObjectType, receiveObjectIds,sendWeixin);
            }
        }
    }

    /**
     *
     * @param message
     * @param messageReceiveObjectType
     * @param receiveObjectIds
     */
    @Async
    public void saveAndSend(Message message, MessageReceiveObjectType messageReceiveObjectType, List<String> receiveObjectIds){
        saveAndSend(message,messageReceiveObjectType, receiveObjectIds,true);
    }

    /**
     *
     * @param message
     * @param messageReceiveObjectType
     * @param receiveObjectIds
     * @param sendWeixin 是否通过微信发送消息
     */
    @Async
    public void saveAndSend(Message message, MessageReceiveObjectType messageReceiveObjectType, List<String> receiveObjectIds,Boolean sendWeixin){
        messageService.saveAndSend(message,messageReceiveObjectType,receiveObjectIds,sendWeixin);
    }

    /**
     * 设置通知已读状态
     * @param receive
     */
    @Async
    public void setRead(MessageReceive receive){
        messageReceiveService.setRead(receive);
    }

    /**
     * 设置通知已读状态
     * @param userId
     */
    @Async
    public void setReadAll(String userId){
        messageReceiveService.setReadAll(userId, null);
    }

}
