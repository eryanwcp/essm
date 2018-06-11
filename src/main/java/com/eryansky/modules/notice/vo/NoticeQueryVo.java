/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.vo;

import com.eryansky.modules.notice._enum.IsTop;
import com.eryansky.modules.notice._enum.NoticeReadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-01-30
 */
public class NoticeQueryVo implements Serializable {

    /**
     * 关键字
     */
    private String query;
    /**
     * 通知标题
     */
    private String title;
    /**
     * 通知内容
     */
    private String content;
    /**
     * 通知发布起始时间
     */
    private Date startTime;
    /**
     * 通知发布截止时间
     */
    private Date endTime;
    /**
     * 是否置顶 {@link com.jfit.modules.notice._enum.IsTop}
     */
    private Integer isTop;
    /**
     * 是否阅读 {@link com.jfit.modules.notice._enum.NoticeReadMode}
     */
    private Integer isRead;
    /**
     * 通知发布人
     */
    private List<String> publishUserIds = new ArrayList<String>(0);

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public List<String> getPublishUserIds() {
        return publishUserIds;
    }

    public void setPublishUserIds(List<String> publishUserIds) {
        this.publishUserIds = publishUserIds;
    }

    /**
     * 将截止时间设置到当天最后1秒钟 23h 59m 59s
     */
    public void syncEndTime(){
        if(this.endTime != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.endTime);
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            this.endTime = calendar.getTime();
        }
    }
}
