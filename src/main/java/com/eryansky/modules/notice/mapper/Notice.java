/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.mapper;

import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys._enum.YesOrNo;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.notice._enum.IsTop;
import com.eryansky.modules.notice._enum.NoticeMode;
import com.eryansky.modules.notice._enum.NoticeReceiveScope;
import com.eryansky.modules.notice.utils.NoticeUtils;
import com.eryansky.modules.sys.utils.DictionaryUtils;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 内部通知 实体类
 */
@SuppressWarnings("serial")
@JsonFilter(" ")
public class Notice extends DataEntity<Notice> {

    public static final String DATE_TIME_SHORT_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * 通知公告标题
     */
    private String title;
    /**
     * 通知公告类型 {@DictionaryUtils.DIC_NOTICE}
     */
    private String type;
    /**
     * 通知公告内容
     */
    private String content;
    /**
     * 附件
     */
    private List<String> fileIds = new ArrayList<String>(0);


    /**
     * 发布人
     */
    private String userId;
    /**
     * 发布部门
     */
    private String organId;


    /**
     * 0表示不置顶，1表示置顶
     */
    private Integer isTop;
    /**
     * 结束置顶天数
     */
    private Integer endTopDay;

    /**
     * 状态 默认：未发布 {@link NoticeMode}
     */
    private Integer mode = NoticeMode.UnPublish.getValue();
    /**
     * 发布日期
     */
    private Date publishTime;

    /**
     * 生效时间
     */
    private Date effectTime = Calendar.getInstance().getTime();
    /**
     * 失效时间 为空，则一直有效
     */
    private Date invalidTime;
    /**
     * 是否记录 查看情况
     */
    private String isRecordRead = YesOrNo.YES.getValue();

    /**
     * 接收范围 {@link NoticeReceiveScope}
     */
    private String receiveScope = NoticeReceiveScope.COMPANY_AND_CHILD.getValue();


    public Notice() {
        super();
    }

    public Notice(String id) {
        super(id);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }


    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Integer getIsTop() {
        return this.isTop;
    }

    public void setEndTopDay(Integer endTopDay) {
        this.endTopDay = endTopDay;
    }

    public Integer getEndTopDay() {
        return this.endTopDay;
    }


    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getMode() {
        return this.mode;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    public Date getPublishTime() {
        return publishTime;
    }


    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }


    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }

    @JsonFormat(pattern = DATE_TIME_SHORT_FORMAT, timezone = TIMEZONE)
    public Date getEffectTime() {
        return this.effectTime;
    }

    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    @JsonFormat(pattern = DATE_TIME_SHORT_FORMAT, timezone = TIMEZONE)
    public Date getInvalidTime() {
        return this.invalidTime;
    }

    public String getIsRecordRead() {
        return isRecordRead;
    }

    public void setIsRecordRead(String isRecordRead) {
        this.isRecordRead = isRecordRead;
    }

    public String getReceiveScope() {
        return receiveScope;
    }

    public void setReceiveScope(String receiveScope) {
        this.receiveScope = receiveScope;
    }

    @JsonIgnore
    public List<String> getFileIds() {
        if(Collections3.isEmpty(fileIds)){
            this.fileIds = NoticeUtils.getNoticeFileIds(this.id);
        }
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }


    /**
     * 通知类型 由数据字典转换而来
     *
     * @return
     */
    public String getTypeView() {
        return DictionaryUtils.getDictionaryNameByDV(NoticeUtils.DIC_NOTICE, this.getType(), this.getType());
    }

    public String getIsTopView() {
        IsTop s = IsTop.getIsTop(isTop);
        String str = "";
        if (s != null) {
            str = s.getDescription();
        }
        return str;
    }

    public String getModeView() {
        NoticeMode s = NoticeMode.getNoticeMode(mode);
        String str = "";
        if (s != null) {
            str = s.getDescription();
        }
        return str;
    }

    /**
     * 发布人姓名
     * @return
     */
    public String getPublishUserName() {
        return UserUtils.getUserName(this.userId);
    }

    /**
     * 发布部门 名称
     * @return
     */
    public String getPublishOrganName() {
        return OrganUtils.getOrganName(this.organId);
    }

    /**
     * 通知ID
     * @return
     */
    public String getNoticeId() {
        return this.id;
    }

    public List<String> getNoticeReceiveUserIds(){
        return NoticeUtils.getNoticeReceiveUserIds(this.id);
    }

    public List<String> getNoticeReceiveOrganIds(){
        return NoticeUtils.getNoticeReceiveOrganIds(this.id);
    }
    /**
     * 转发
     * @return
     */
    public Notice repeat() {
        Notice notice = new Notice();
        notice.setTitle(NoticeUtils.MSG_REPEAT + this.getTitle());
        notice.setContent(this.getContent());
        notice.setType(this.getType());
        notice.setIsTop(this.getIsTop());
        notice.setEndTopDay(this.getEndTopDay());
        notice.setEffectTime(this.getEffectTime());
        notice.setEndTopDay(this.getEndTopDay());
        return notice;
    }

}

