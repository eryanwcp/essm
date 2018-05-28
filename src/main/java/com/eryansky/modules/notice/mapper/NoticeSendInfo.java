/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.mapper;

import com.eryansky.core.orm.mybatis.entity.BaseEntity;
import com.eryansky.modules.notice._enum.ReceiveObjectType;
import com.eryansky.modules.notice.utils.NoticeUtils;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-10-15 
 */
public class NoticeSendInfo extends BaseEntity<NoticeSendInfo> {

    /**
     * 通知ID
     */
    private String noticeId;
    private Notice notice;
    /**
     * 接收人类型 {@link ReceiveObjectType}
     */
    private String receiveObjectType;
    /**
     * 接收对象ID
     */
    private String receiveObjectId;

    public NoticeSendInfo() {
        super();
    }

    public NoticeSendInfo(String id) {
        super(id);
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    private Notice getNotice() {
        if(this.notice == null){
            return NoticeUtils.getNotice(this.noticeId);
        }
        return this.notice;
    }

    public String getReceiveObjectType() {
        return receiveObjectType;
    }

    public void setReceiveObjectType(String receiveObjectType) {
        this.receiveObjectType = receiveObjectType;
    }

    public String getReceiveObjectId() {
        return receiveObjectId;
    }

    public void setReceiveObjectId(String receiveObjectId) {
        this.receiveObjectId = receiveObjectId;
    }
}
