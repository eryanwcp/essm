/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.orm._enum.StatusState;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.notice._enum.ReceiveObjectType;
import com.eryansky.modules.notice.dao.NoticeSendInfoDao;
import com.eryansky.modules.notice.mapper.Notice;
import com.eryansky.modules.notice.mapper.NoticeSendInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-10-16 
 */
@Service
public class NoticeSendInfoService extends CrudService<NoticeSendInfoDao,NoticeSendInfo> {

    /**
     * 通知发送配置信息
     * @param noticeId 通知ID
     * @return
     */
    public List<NoticeSendInfo> findNoticeSendInfos(String noticeId){
        return findNoticeSendInfos(noticeId, null);
    }

    /**
     * 通知发送配置信息
     * @param noticeId 通知ID
     * @param receiveObjectType {@link ReceiveObjectType}
     * @return
     */
    public List<NoticeSendInfo> findNoticeSendInfos(String noticeId,String receiveObjectType){
        NoticeSendInfo noticeSendInfo = new NoticeSendInfo();
        Notice notice = new Notice(noticeId);
        notice.setStatus(StatusState.NORMAL.getValue());
        noticeSendInfo.setNotice(notice);
        noticeSendInfo.setReceiveObjectType(receiveObjectType);
        return dao.findQueryList(noticeSendInfo);
    }

    /**
     * 查找接收用户IDS
     * @param noticeId 通知ID
     * @return
     */
    public List<String> findUserIdsByNoticeId(String noticeId){
        return findObjectIdsByNoticeId(noticeId,ReceiveObjectType.User.getValue());
    }

    /**
     * 查找接收机构IDS
     * @param noticeId 通知ID
     * @return
     */
    public List<String> findOrganIdsByNoticeId(String noticeId){
        return findObjectIdsByNoticeId(noticeId,ReceiveObjectType.Organ.getValue());
    }

    /**
     * 查找接收对象IDS
     * @param noticeId 通知ID
     * @param receiveObjectType {@link ReceiveObjectType}
     * @return
     */
    public List<String> findObjectIdsByNoticeId(String noticeId,String receiveObjectType){
        NoticeSendInfo entity = new NoticeSendInfo();
        entity.setNoticeId(noticeId);
        entity.setReceiveObjectType(receiveObjectType);
        return dao.findObjectIdsByNoticeId(entity);
    }


    /**
     * 根据通知ID删除
     * @param noticeId
     * @return
     */
    public int deleteByNoticeId(String noticeId){
        Parameter parameter = Parameter.newParameter();
        parameter.put("noticeId",noticeId);
        return dao.deleteByNoticeId(parameter);
    }
}
