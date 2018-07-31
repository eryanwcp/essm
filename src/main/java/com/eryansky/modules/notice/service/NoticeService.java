/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm._enum.StatusState;
import com.eryansky.common.orm.model.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.Identities;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.notice._enum.*;
import com.eryansky.modules.notice.mapper.NoticeSendInfo;
import com.eryansky.modules.sys.service.OrganService;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.notice.dao.NoticeDao;
import com.eryansky.modules.notice.mapper.Notice;
import com.eryansky.modules.notice.mapper.NoticeReceiveInfo;
import com.eryansky.modules.notice.vo.NoticeQueryVo;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 通知管理
 */
@Service
public class NoticeService extends CrudService<NoticeDao,Notice> {

	@Autowired
	private NoticeSendInfoService noticeSendInfoService;
    @Autowired
	private NoticeReceiveInfoService noticeReceiveInfoService;
    @Autowired
	private UserService userService;



    /**
     * 保存通知和文件
     * @param entity
     * @param isPub
     * @param userIds
     * @param organIds
     * @param fileIds
     */
    public void saveNoticeAndFiles(Notice entity,Boolean isPub,Collection<String> userIds,Collection<String> organIds,List<String> fileIds) {
        List<String> oldFileIds = Collections.EMPTY_LIST;
        if(!entity.getIsNewRecord()){
            oldFileIds = findFileIdsByNoticeId(entity.getId());
        }
        super.save(entity);
        saveNoticeFiles(entity.getId(),fileIds);

        List<String> removeFileIds = Collections3.subtract(oldFileIds,fileIds);
        if(Collections3.isNotEmpty(removeFileIds)){
            deleteNoticeFiles(entity.getId(),removeFileIds);
            DiskUtils.deleteFolderFiles(removeFileIds);
        }

        //历史数据
        noticeReceiveInfoService.deleteByNoticeId(entity.getId());
        noticeSendInfoService.deleteByNoticeId(entity.getId());

        saveNoticeSendInfos(userIds, entity.getId(), ReceiveObjectType.User.getValue());
        saveNoticeSendInfos(organIds, entity.getId(),ReceiveObjectType.Organ.getValue());

        if(isPub != null && isPub) {
            publish(entity);
        }
    }

    private void saveNoticeSendInfos(Collection<String> ids, String noticeId,String receieveObjectType){
        if(Collections3.isNotEmpty(ids)) {
            for(String id : ids){
                NoticeSendInfo noticeSendInfo = new NoticeSendInfo();
                noticeSendInfo.setReceiveObjectType(receieveObjectType);
                noticeSendInfo.setNoticeId(noticeId);
                noticeSendInfo.setReceiveObjectId(id);
                noticeSendInfoService.save(noticeSendInfo);
            }
        }

    }

    /**
     * 删除通知
     * @param ids
     */
    public void deleteByIds(List<String> ids){
        if(Collections3.isNotEmpty(ids)){
            for(String id:ids){
                this.delete(new Notice(id));
            }
        }
    }

    /**
     * 属性过滤器查找得到分页数据.
     *
     * @param page 分页对象
     * @param userId 发布人 查询所有则传null
     * @param noticeQueryVo 标查询条件
     * @return
     */
	public Page<Notice> findPage(Page<Notice> page,Notice notice, String userId, NoticeQueryVo noticeQueryVo){
        Parameter parameter = new Parameter();
        parameter.put(DataEntity.FIELD_STATUS, DataEntity.STATUS_NORMAL);

        if(noticeQueryVo != null && Collections3.isNotEmpty(noticeQueryVo.getPublishUserIds())){
            parameter.put("userId",noticeQueryVo.getPublishUserIds().get(0));
        }else{
            parameter.put("userId",userId);
        }

        if(noticeQueryVo != null){
            parameter.put("isTop", noticeQueryVo.getIsTop());
            parameter.put("query",noticeQueryVo.getQuery());

            if (noticeQueryVo.getStartTime() != null) {
                parameter.put("startTime", DateUtils.format(noticeQueryVo.getStartTime(), DateUtils.DATE_TIME_FORMAT));
            }
            if (noticeQueryVo.getEndTime() != null) {
                parameter.put("endTime", DateUtils.format(noticeQueryVo.getEndTime(), DateUtils.DATE_TIME_FORMAT));
            }
        }

        notice.setEntityPage(page);
        parameter.put(BaseInterceptor.PAGE,page);
        parameter.put("dbName",notice.getDbName());
        Map<String,String> sqlMap = Maps.newHashMap();
        sqlMap.put("dsf",super.dataScopeFilter(SecurityUtils.getCurrentUser(), "o", "u"));
        parameter.put("sqlMap",sqlMap);
        page.setResult(dao.findQueryList(parameter));

        return page;

	}



    /**
     * 发布公告
     *
     * @param noticeId
     *            公告ID
     */
    public void publish(String noticeId) {
        Notice notice = this.get(noticeId);
        if (notice == null) {
            throw new ServiceException("公告[" + noticeId + "]不存在.");
        }
        publish(notice);
    }

    @Async
    public void asyncPublish(String noticeId) {
        Notice notice = this.get(noticeId);
        if (notice == null) {
            throw new ServiceException("公告[" + noticeId + "]不存在.");
        }
        publish(notice);
    }

	/**
	 * 发布公告
	 * 
	 * @param notice 通知
	 */
	public void publish(Notice notice) {
        notice.setMode(NoticeMode.Effective.getValue());
        if(notice.getPublishTime() == null) {
            Date nowTime = Calendar.getInstance().getTime();
            notice.setPublishTime(nowTime);
        }
		this.save(notice);
        List<NoticeReceiveInfo>  receiveInfos = Lists.newArrayList();
        List<String> receiveUserIds = Lists.newArrayList();

        if(NoticeReceiveScope.CUSTOM.getValue().equals(notice.getReceiveScope())){
            List<String> _receiveUserIds = notice.getNoticeReceiveUserIds();
            List<String> receiveOrganIds = notice.getNoticeReceiveOrganIds();
            List<String> userIds = userService.findUserIdsByOrganIds(receiveOrganIds);
            if(Collections3.isNotEmpty(_receiveUserIds)){
                receiveUserIds.addAll(_receiveUserIds);
            }
            if(Collections3.isNotEmpty(userIds)){
                receiveUserIds.addAll(userIds);
            }
        }else if(NoticeReceiveScope.ALL.getValue().equals(notice.getReceiveScope())){
            receiveUserIds = userService.findAllNormalUserIds();
        }else if(NoticeReceiveScope.COMPANY_AND_CHILD.getValue().equals(notice.getReceiveScope())){
            receiveUserIds = userService.findOwnerAndChildsUserIds(UserUtils.getCompanyId(notice.getUserId()));
        }else if(NoticeReceiveScope.COMPANY.getValue().equals(notice.getReceiveScope())){
            receiveUserIds = userService.findUserIdsByCompanyId(UserUtils.getCompanyId(notice.getUserId()));
        }else if(NoticeReceiveScope.OFFICE_AND_CHILD.getValue().equals(notice.getReceiveScope())){
            receiveUserIds = userService.findOwnerAndChildsUserIds(notice.getUserId());
        }else if(NoticeReceiveScope.OFFICE.getValue().equals(notice.getReceiveScope())){
            receiveUserIds = userService.findUserIdsByOrganId(UserUtils.getDefaultOrganId(notice.getUserId()));
        }
        if(Collections3.isNotEmpty(receiveUserIds)){
            for(String userId:receiveUserIds){
                NoticeReceiveInfo receiveInfo = new NoticeReceiveInfo(userId,notice.getId());
                checkReceiveInfoAdd(receiveInfos, receiveInfo);
            }
        }

        if(Collections3.isNotEmpty(receiveInfos)){
            for(NoticeReceiveInfo noticeReceiveInfo:receiveInfos){
                noticeReceiveInfoService.save(noticeReceiveInfo);
            }

        }


	}

    /**
     * 发布公告
     * @param type
     * @param title
     * @param content
     * @param sendTime
     * @param userId
     * @param organId
     * @param organIds
     */
    public void sendToOrganNotice(String appId,String type,String title,String content,Date sendTime,String userId,String organId,List<String> organIds) {
        //保存到notice表
        Notice notice = new Notice();
        notice.setId(Identities.uuid2());
        notice.setAppId(appId);
        notice.setType(type);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setPublishTime(sendTime);
        notice.setReceiveScope(NoticeReceiveScope.CUSTOM.getValue());
        notice.setUserId(userId);
        notice.setOrganId(organId);
        notice.setCreateTime(Calendar.getInstance().getTime());
        dao.insert(notice);

        if (CollectionUtils.isNotEmpty(organIds)) {
            //去重
            for (String _organId : organIds) {
                //保存到notice send表
                NoticeSendInfo noticeSendInfo = new NoticeSendInfo();
                noticeSendInfo.setNoticeId(notice.getId());
                noticeSendInfo.setReceiveObjectType(ReceiveObjectType.Organ.getValue());
                noticeSendInfo.setReceiveObjectId(_organId);
                noticeSendInfoService.save(noticeSendInfo);
            }
            //发布
            publish(notice);
        }

    }
    


    /**
     * 去除重复
     * @param receiveInfos
     * @param receiveInfo
     */
    private void checkReceiveInfoAdd(List<NoticeReceiveInfo> receiveInfos,NoticeReceiveInfo receiveInfo){
        boolean flag = false;
        for(NoticeReceiveInfo r:receiveInfos){
            if(r.getUserId().equals(receiveInfo.getUserId())){
                flag = true;
                break;
            }

        }
        if(!flag){
            receiveInfos.add(receiveInfo);
        }

    }


    /**
     * 标记为已读
     * @param userId 所属用户ID
     * @param noticeIds 通知ID集合
     */
    public void markReaded(String userId,List<String> noticeIds){
        if (Collections3.isNotEmpty(noticeIds)) {
            for (String id : noticeIds) {
                NoticeReceiveInfo noticeReceiveInfo = noticeReceiveInfoService.getUserNotice(userId, id);
                noticeReceiveInfo.setIsRead(NoticeReadMode.readed.getValue());
                noticeReceiveInfoService.save(noticeReceiveInfo);
            }
        } else {
            logger.warn("参数[entitys]为空.");
        }

    }

    /**
     * 插入通知附件关联信息
     * @param id 通知ID
     * @param ids 文件IDS
     */
    public void insertNoticeFiles(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        if(Collections3.isNotEmpty(ids)){
            dao.insertNoticeFiles(parameter);
        }
    }

    /**
     * 刪除通知附件关联信息
     * @param id 通知ID
     * @param ids 文件IDS
     */
    public void deleteNoticeFiles(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deleteNoticeFiles(parameter);
    }


    /**
     * 保存通知附件关联信息
     * 保存之前先删除原有
     * @param id 通知ID
     * @param ids 文件IDS
     */
    public void saveNoticeFiles(String id, Collection<String> ids){
        Parameter parameter = Parameter.newParameter();
        parameter.put("id",id);
        parameter.put("ids",ids);
        dao.deleteNoticeFiles(parameter);
        if(Collections3.isNotEmpty(ids)){
            dao.insertNoticeFiles(parameter);
        }
    }


    /**
     * 查找通知附件ID
     * @param noticeId
     * @return
     */
    public List<String> findFileIdsByNoticeId(String noticeId){
        return dao.findFileIdsByNoticeId(noticeId);
    }



    /**
     * 轮询通知 定时发布、到时失效、取消置顶
     */
    public void pollNotice() {
        // 查询到今天为止所有未删除的通知
        Date nowTime = Calendar.getInstance().getTime();
        Notice notice = new Notice();
        notice.setStatus(StatusState.NORMAL.getValue());
        notice.setMode(NoticeMode.Invalidated.getValue());
        List<Notice> noticeList = dao.findList(notice);
        if (Collections3.isNotEmpty(noticeList)) {
            for (Notice n : noticeList) {
                if (NoticeMode.UnPublish.getValue().equals(n.getMode())
                        && n.getEffectTime() != null
                        && nowTime.compareTo(n.getEffectTime()) != -1) {//定时发布
                    this.publish(n);
                }else if (NoticeMode.Effective.getValue().equals(n.getMode())
                        && n.getInvalidTime() != null
                        && nowTime.compareTo(n.getInvalidTime()) != -1) {//到时失效
                    n.setMode(NoticeMode.Invalidated.getValue());
                   this.save(n);
                }
                //取消置顶
                if (IsTop.Yes.getValue().equals(n.getIsTop())
                        && n.getEndTopDay() != null && n.getEndTopDay() >0) {
                    Date publishTime = (n.getPublishTime() == null) ? nowTime: n.getPublishTime();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(publishTime);
                    cal.add(Calendar.DATE, n.getEndTopDay());
                    if (nowTime.compareTo(cal.getTime()) != -1) {
                        n.setIsTop(IsTop.No.getValue());
                        this.save(n);
                    }
                }
            }
        }
    }
}
