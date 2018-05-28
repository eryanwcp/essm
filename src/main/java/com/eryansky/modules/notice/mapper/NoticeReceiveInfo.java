/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.mapper;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.eryansky.core.orm.mybatis.entity.BaseEntity;
import com.eryansky.modules.notice._enum.NoticeReadMode;
import com.eryansky.modules.notice.utils.NoticeUtils;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;

import java.util.Date;

/**
 * 通知接收信息
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-10-15 
 */
@JsonFilter(" ")
public class NoticeReceiveInfo extends BaseEntity<NoticeReceiveInfo> {

    /**
     * 用户
     */
    private String userId;
    /**
     * 是否已读 默认值：否 {@link NoticeReadMode}
     */
    private String isRead;
    /**
     * 读取时间
     */
    private Date readTime;

    private String noticeId;

    private Notice notice;

    public NoticeReceiveInfo() {
        super();
        this.isRead = NoticeReadMode.unreaded.getValue();
    }

    public NoticeReceiveInfo(String id) {
        super(id);
    }

    public NoticeReceiveInfo(String userId, String noticeId) {
        this.userId = userId;
        this.noticeId = noticeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }


    /**
     * 接收人姓名
     * @return
     */
    public String getUserName(){
        return UserUtils.getUserName(this.userId);
    }

    /**
     * 是否读取
     * @return
     */
    public String getIsReadView(){
        NoticeReadMode s = NoticeReadMode.getByValue(isRead);
        String str = "";
        if(s != null){
            str =  s.getDescription();
        }
        return str;
    }


    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return getNotice().getTitle();
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

    public String getType() {
        return getNotice().getType();
    }

    public String getTypeView() {
        return getNotice().getTypeView();
    }

    public String getContent() {
        return getNotice().getContent();
    }

    public String getPublishUserName() {
        return getNotice().getPublishUserName();
    }

    public String getPublishOrganName() {
        return getNotice().getPublishOrganName();
    }

    public Date getPublishTime() {
        return getNotice().getPublishTime();
    }

    public String getIsTop() {
        return getNotice().getIsTop();
    }

    public String getIsTopView() {
        return getNotice().getIsTopView();
    }

    /**
     * 接收人部门
     * @return
     */
    public String  getOrganName(){
        return UserUtils.getDefaultOrganName(userId);
    }

    /**
     * 判断当前登录用户是否读取
     * @return
     */
    public boolean isRead(){
        return NoticeUtils.isRead(this.noticeId);
    }

    /**
     * 判断当前登录用户是否读取
     * @return
     */
    public String isReadView(){
        return this.isRead() ? NoticeReadMode.readed.getDescription():NoticeReadMode.unreaded.getDescription();
    }
}
