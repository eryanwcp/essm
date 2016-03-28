/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.notice.mapper;

import com.eryansky.core.orm.mybatis.entity.BaseEntity;
import com.eryansky.modules.notice.utils.NoticeUtils;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-10-15 
 */
public class NoticeSendInfo extends BaseEntity<NoticeSendInfo> {

    /**
     * 通知ID
     */
    private String noticeId;
    private Notice notice;
    /**
     * 接收人类型 {@link com.eryansky.modules.mail._enum.ReceiveObjectType}
     */
    private Integer receiveObjectType;
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

    public Integer getReceiveObjectType() {
        return receiveObjectType;
    }

    public void setReceiveObjectType(Integer receiveObjectType) {
        this.receiveObjectType = receiveObjectType;
    }

    public String getReceiveObjectId() {
        return receiveObjectId;
    }

    public void setReceiveObjectId(String receiveObjectId) {
        this.receiveObjectId = receiveObjectId;
    }
}
