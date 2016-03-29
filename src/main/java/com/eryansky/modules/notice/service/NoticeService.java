/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.orm.mybatis.interceptor.BaseInterceptor;
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.security.SecurityUtils;
import com.google.common.collect.Lists;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.notice._enum.IsTop;
import com.eryansky.modules.notice._enum.NoticeMode;
import com.eryansky.modules.notice._enum.NoticeReceiveScope;
import com.eryansky.modules.notice.dao.NoticeDao;
import com.eryansky.modules.notice.mapper.Notice;
import com.eryansky.modules.notice.mapper.NoticeReceiveInfo;
import com.eryansky.modules.notice.vo.NoticeQueryVo;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.YesOrNo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 通知管理
 */
@Service
@Transactional(readOnly =  true)
public class NoticeService extends CrudService<NoticeDao,Notice> {

	@Autowired
	private NoticeSendInfoService noticeSendInfoService;
    @Autowired
	private NoticeReceiveInfoService noticeReceiveInfoService;
    @Autowired
	private OrganManager organManager;
    @Autowired
	private UserManager userManager;

    public void save(Notice entity,boolean updateFile) {
        super.save(entity);
        if(updateFile){
            dao.deleteNoticeFile(entity);
            if(Collections3.isNotEmpty(entity.getFileIds())){
                dao.insertNoticeFile(entity);
            }
        }
    }

    /**
     * 删除通知
     * @param noticeId
     */
    public void removeNotice(String noticeId){
        this.delete(new Notice(noticeId));
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
        parameter.put(Notice.FIELD_STATUS, StatusState.NORMAL.getValue());

        if(noticeQueryVo != null && Collections3.isNotEmpty(noticeQueryVo.getPublishUserIds())){
            parameter.put("userId",noticeQueryVo.getPublishUserIds().get(0));
        }else{
            parameter.put("userId",userId);
        }

        if(noticeQueryVo != null){
            if (noticeQueryVo.getIsTop() != null) {
                parameter.put("isTop", noticeQueryVo.getIsTop());
            }

            if (StringUtils.isNotBlank(noticeQueryVo.getTitle())) {
                parameter.put("title",noticeQueryVo.getTitle());
            }
            if (StringUtils.isNotBlank(noticeQueryVo.getContent())) {
                parameter.put("content",noticeQueryVo.getContent());
            }

            //TODO 分页查询存在问题
//            if (Collections3.isNotEmpty(noticeQueryVo.getPublishUserIds())) {
//                parameter.put("publishUserIds", noticeQueryVo.getPublishUserIds());
//            }

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
            List<String> userIds = userManager.findUserIdsByOrganIds(receiveOrganIds);
            if(Collections3.isNotEmpty(_receiveUserIds)){
                receiveUserIds.addAll(_receiveUserIds);
            }
            if(Collections3.isNotEmpty(userIds)){
                receiveUserIds.addAll(userIds);
            }
        }else if(NoticeReceiveScope.ALL.getValue().equals(notice.getReceiveScope())){
            receiveUserIds = userManager.findAllNormalUserIds();

        }else if(NoticeReceiveScope.COMPANY_AND_CHILD.getValue().equals(notice.getReceiveScope())){
            User user = userManager.loadById(notice.getUserId());
            receiveUserIds = userManager.findOwnerAndChildsUserIds(user.getCompanyId());

        }else if(NoticeReceiveScope.COMPANY.getValue().equals(notice.getReceiveScope())){
            List<String> organIds = organManager.findOrganChildsDepartmentOrganIds(notice.getOrganId());
            receiveUserIds = userManager.findUserIdsByOrganIds(organIds);
        }else if(NoticeReceiveScope.OFFICE_AND_CHILD.getValue().equals(notice.getReceiveScope())){
            User user = userManager.loadById(notice.getUserId());
            List<String> organIds = Lists.newArrayList();
            organIds.add(user.getOfficeId());
            List<String> officeIds = organManager.findOrganChildsOfficeOrganIds(user.getDefaultOrganId());
            if(Collections3.isNotEmpty(officeIds)){
                organIds.addAll(officeIds);
            }
            receiveUserIds = userManager.findUserIdsByOrganIds(organIds);

        }else if(NoticeReceiveScope.OFFICE.getValue().equals(notice.getReceiveScope())){
            User user = userManager.loadById(notice.getUserId());
            List<String> organIds = new ArrayList<String>(1);
            organIds.add(user.getOfficeId());
            receiveUserIds = userManager.findUserIdsByOrganIds(organIds);

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


    public void saveFromModel(Notice notice,boolean isPublish){
        this.save(notice);
        if(isPublish){
            this.publish(notice.getId());
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
                noticeReceiveInfo.setIsRead(YesOrNo.YES.getValue());
                noticeReceiveInfoService.save(noticeReceiveInfo);
            }
        } else {
            logger.warn("参数[entitys]为空.");
        }

    }

    
    /**
     * 虚拟删除通知
     * @param ids
     */
	public void remove(List<String> ids) {
		if (Collections3.isNotEmpty(ids)) {
			for (String id : ids) {
				Notice notice = this.get(id);
				notice.setStatus(StatusState.DELETE.getValue());
				this.save(notice);
			}
		}
	}


    /**
     * 查找通知附件ID
     * @param noticeId
     * @return
     */
    public List<String> getFileIds(String noticeId){
        return dao.findNoticeFiles(noticeId);
    }



    /**
     * 轮询通知 定时发布、到时失效、取消置顶
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
    public void pollNotice() throws SystemException, ServiceException,
            DaoException {
        // 查询到今天为止所有未删除的通知
        String hql = " from Notice n where n.status= :p1 and n.noticeMode <> :p2";
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
