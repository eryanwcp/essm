/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.mapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.eryansky.core.orm.mybatis.entity.BaseEntity;
import com.eryansky.modules.notice.utils.MessageUtils;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.utils.UserUtils;

import java.util.Date;

/**
 * 消息接收表
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-03-14 
 */
public class MessageReceive extends BaseEntity<MessageReceive> {
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 是否发送成功 ${@link YesOrNo}
     */
    private String isSend;
    /**
     * 是否读取 ${@link YesOrNo}
     */
    private String isRead;
    /**
     * 读取时间
     */
    private Date readTime;

    public MessageReceive() {
        this.isSend = YesOrNo.YES.getValue();
    }

    public MessageReceive(String messageId) {
        this();
        this.messageId = messageId;
    }

    public MessageReceive(String id, String messageId) {
        super(id);
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsSend() {
        return isSend;
    }

    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    //    @JsonIgnore
    public Message getMessage() {
        return MessageUtils.get(messageId);
    }


    public String getOrganName(){
        User user = UserUtils.getUser(userId);
        if(user != null){
            return user.getDefaultOrganName();
        }
        return null;
    }
    public String getCompanyName(){
        User user = UserUtils.getUser(userId);
        if(user != null){
            return user.getCompanyName();
        }
        return null;
    }

    public String getUserName(){
        return UserUtils.getUserName(userId);
    }

    public String getIsReadView() {
        if(YesOrNo.YES.getValue().equals(isRead)){
            return "已阅";
        }
        return "未阅";
    }
}
