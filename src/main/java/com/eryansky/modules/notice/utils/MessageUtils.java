/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.utils;

import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.notice._enum.MessageReceiveObjectType;
import com.eryansky.modules.notice.mapper.Message;
import com.eryansky.modules.notice.mapper.MessageReceive;
import com.eryansky.modules.notice.service.MessageReceiveService;
import com.eryansky.modules.notice.service.MessageService;
import com.eryansky.modules.notice.service.MessageTask;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.modules.sys.utils.UserUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 消息工具类
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-03-14 
 */
public class MessageUtils {

    private static MessageService messageService = SpringContextHolder.getBean(MessageService.class);
    private static MessageReceiveService messageReceiveService = SpringContextHolder.getBean(MessageReceiveService.class);
    private static MessageTask messageTask = SpringContextHolder.getBean(MessageTask.class);
    private static UserService userService = SpringContextHolder.getBean(UserService.class);

    private MessageUtils(){

    }

    public static Message get(String id){
        if(StringUtils.isNotBlank(id)){
            return messageService.get(id);
        }
        return null;
    }

    /**
     * 发送系统消息给用户
     * @param receiveUserId 接收用户ID
     * @param content 消息内容
     */
    public static void sendToUserMessage(String receiveUserId,String content) {
        sendToUserMessage(receiveUserId, content, null);
    }

    /**
     * 发送系统消息给用户
     * @param receiveUserIds 接收用户IDS
     * @param content 消息内容
     */
    public static void sendToUserMessage(Collection<String> receiveUserIds,String content) {
        for(String receiveUserId:receiveUserIds){
            sendToUserMessage(receiveUserId, content, null);
        }
    }

    /**
     * 发送系统消息给用户
     * @param receiveUserId 接收用户ID
     * @param content 消息内容
     * @param linkUrl 消息链接
     */
    public static void sendToUserMessage(String receiveUserId,String content,
                                         String linkUrl) {
        List<String> receiveObjectIds = new ArrayList<String>(1);
        receiveObjectIds.add(receiveUserId);
        User user = userService.getSuperUser();
        sendUserMessage(user.getId(), content, linkUrl, receiveUserId);
    }

    /**
     * 发送消息
     * @param sender 发送者用户ID {@link User}
     * @param content 必选 消息内容
     * @param linkUrl 消息URL链接地址
     * @param receiveUserId 必选 接收对象ID集合
     */
    public static void sendUserMessage(String sender,String content,
                                   String linkUrl,String receiveUserId) {
        List<String> receiveObjectIds = new ArrayList<String>(1);
        receiveObjectIds.add(receiveUserId);
        sendMessage(null,sender,content,linkUrl,MessageReceiveObjectType.User.getValue(),receiveObjectIds);
    }


    /**
     * 发送消息
     * @param sender 发送者用户ID {@link User}
     * @param content 必选 消息内容
     * @param linkUrl 消息URL链接地址
     * @param receiveObjectType 必选 接收类型 {@link MessageReceiveObjectType}
     * @param receiveObjectIds 必选 接收对象ID集合
     */
    public static void sendMessage(String appId,String sender,String content,
                              String linkUrl,
                              String receiveObjectType,List<String> receiveObjectIds) {
        sendMessage(appId, sender, content,linkUrl,receiveObjectType,receiveObjectIds,null);
    }

    /**
     * 发送消息
     * @param sender 发送者用户ID {@link User}
     * @param content 必选 消息内容
     * @param linkUrl 消息URL链接地址
     * @param receiveObjectType 必选 接收类型 {@link MessageReceiveObjectType}
     * @param receiveObjectIds 必选 接收对象ID集合
     */
    public static void sendMessage(String appId,String sender,String content,
                                   String linkUrl,
                                   String receiveObjectType,List<String> receiveObjectIds,Date date) {
        MessageReceiveObjectType m = MessageReceiveObjectType.getByValue(receiveObjectType);
        sendMessage(appId, sender,null, content,linkUrl,m,receiveObjectIds,date);
    }



    /**
     * 发送消息
     * @param sender 发送者用户ID {@link User}
     * @param content 必选 消息内容
     * @param linkUrl 消息URL链接地址
     * @param messageReceiveObjectType 必选 接收类型 {@link MessageReceiveObjectType}
     * @param receiveObjectIds 必选 接收对象ID集合
     */
    public static void sendMessage(String sender,String content,
                                   String linkUrl,
                                   MessageReceiveObjectType messageReceiveObjectType,List<String> receiveObjectIds) {
        sendMessage(null,sender,null,content,linkUrl,messageReceiveObjectType,receiveObjectIds,null);
    }

    /**
     * 发送消息
     * @param appId 可选 应用ID
     * @param sender 发送者用户ID {@link User}
     * @param category 可选 消息分类
     * @param content 必选 消息内容
     * @param linkUrl 消息URL链接地址
     * @param messageReceiveObjectType 必选 接收类型 {@link MessageReceiveObjectType}
     * @param receiveObjectIds 必选 接收对象ID集合
     */
    public static void sendMessage(String appId,String sender,String category,String content,
                                   String linkUrl,
                                   MessageReceiveObjectType messageReceiveObjectType,List<String> receiveObjectIds,Date date) {
        Message model = new Message();
        User user = UserUtils.getUser(sender);
        if(user == null){
            throw new SystemException("["+sender+"]用户不存在");
        }

        model.setCategory(category);
        model.setOrganId(user.getCompanyId());
        model.setSender(user.getId());
        model.setContent(content);
        model.setUrl(linkUrl);
        model.setSendTime(date);
//        messageService.save(model);
        messageTask.saveAndSend(model, messageReceiveObjectType, receiveObjectIds);
    }

    /**
     * 用户消息
     * @param pageNo 第几页
     * @param pageSize  分页大小 不分页：-1
     * @return
     */
    public static Page<MessageReceive> findUserMessages(int pageNo, int pageSize, String isRead) {
        return findUserMessages(pageNo,pageSize, null,isRead);
    }

    /**
     * 用户消息
     * @param pageNo 第几页
     * @param pageSize  分页大小 不分页：-1
     * @param userId 用户ID
     * @return
     */
    public static Page<MessageReceive> findUserMessages(int pageNo, int pageSize, String userId, String isRead) {
        String _userId = userId;
        if(StringUtils.isBlank(_userId)){
//            _userId = SecurityUtils.getCurrentUserId();
            _userId = SecurityUtils.getCurrentUserLoginName();
        }
        Page<MessageReceive> page = new Page<MessageReceive>(pageNo,pageSize);
        return messageReceiveService.findUserPage(page, _userId,isRead);
    }
}
