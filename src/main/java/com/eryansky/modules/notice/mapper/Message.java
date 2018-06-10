/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.mapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.notice._enum.MessageMode;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;

import java.util.Date;

/**
 * 消息表
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-03-14 
 */
public class Message extends DataEntity<Message> {
    /**
     * 应用标识
     */
    private String appId;
    /**
     * 消息分类
     */
    private String category;
    /**
     * 标题
     */
    private String title;
    /**
     * 链接
     */
    private String url;
    /**
     * 图片
     */
    private String image;
    /**
     * 图片地址
     * @Transient
     */
    private String imageUrl;
    /**
     * 内容
     */
    private String content;
    /**
     * 消息状态 {@link MessageMode}
     */
    private String mode = MessageMode.Draft.getValue();
    /**
     * 发送人
     */
    private String sender;
    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 所属机构
     */
    private String organId;
    /**
     * 消息提醒 {@link com.jfit.modules.notice._enum.TipMessage}
     */
    private String tipMessage;

    private MessageSender messageSender;



    /**
     * 查询关键字
     */
    private String query;
    private String receiveObjectType;

    public Message() {
        super();
    }

    public Message(String id) {
        super(id);
    }

    public Message(String id, String category) {
        super(id);
        this.category = category;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getModeView() {
        MessageMode s = MessageMode.getByValue(mode);
        String str = "";
        if (s != null) {
            str = s.getDescription();
        }
        return str;
    }

    public String getSenderName() {
        return UserUtils.getUserName(sender);
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getOrganName(){
        return OrganUtils.getOrganName(organId);
    }

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public String getReceiveObjectType() {
        return receiveObjectType;
    }

    public void setReceiveObjectType(String receiveObjectType) {
        this.receiveObjectType = receiveObjectType;
    }

    public String getTipMessage() {
        return tipMessage;
    }

    public void setTipMessage(String tipMessage) {
        this.tipMessage = tipMessage;
    }
}
