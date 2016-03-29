/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.notice._enum.ReceiveObjectType;
import com.eryansky.modules.notice.mapper.Notice;
import com.eryansky.modules.notice.mapper.NoticeSendInfo;
import com.eryansky.modules.notice.service.NoticeReceiveInfoService;
import com.eryansky.modules.notice.service.NoticeSendInfoService;
import com.eryansky.modules.notice.service.NoticeService;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-08-01 17:44
 */
public class NoticeUtils {

    public static final String MSG_REPEAT = "转发：";
    public static final String DIC_NOTICE = "NOTICE_TYPE";//通知
    private NoticeUtils(){

    }

    private static UserManager userManager = SpringContextHolder.getBean(UserManager.class);
    private static NoticeService noticeService = SpringContextHolder.getBean(NoticeService.class);
    private static NoticeSendInfoService noticeSendInfoService = SpringContextHolder.getBean(NoticeSendInfoService.class);
    private static NoticeReceiveInfoService noticeReceiveInfoService = SpringContextHolder.getBean(NoticeReceiveInfoService.class);


    /**
     * 根据ID查找
     * @param noticeId
     * @return
     */
    public static Notice getNotice(String noticeId) {
        return noticeService.get(noticeId);
    }

    /**
     * 判断当前登录用户是否读取通知
     * @param noticeId 通知ID
     * @return
     */
    public static boolean isRead(String noticeId) {
//        return noticeReceiveInfoService.isRead(SecurityUtils.getCurrentSessionInfo().getUserId(), noticeId);
        return true;
    }



    public static List<String> getNoticeFileIds(String noticeId) {
        List<String> result = new ArrayList<String>(0);
        if(StringUtils.isBlank(noticeId)){
            return result;
        }
        return noticeService.getFileIds(noticeId);
    }

    public static List<String> getNoticeReceiveUserIds(String noticeId) {
        List<String> result = new ArrayList<String>(0);
        if(StringUtils.isBlank(noticeId)){
            return result;
        }
        List<NoticeSendInfo> list = noticeSendInfoService.findNoticeSendInfos(noticeId, ReceiveObjectType.User.getValue());
        if(Collections3.isNotEmpty(list)){
            result = ConvertUtils.convertElementPropertyToList(list,"receiveObjectId");
        }
        return result;
    }

    public static List<String> getNoticeReceiveOrganIds(String noticeId) {
        List<String> result = new ArrayList<String>(0);
        if(StringUtils.isBlank(noticeId)){
            return result;
        }
        List<NoticeSendInfo> list = noticeSendInfoService.findNoticeSendInfos(noticeId, ReceiveObjectType.Organ.getValue());
        if(Collections3.isNotEmpty(list)){
            result = ConvertUtils.convertElementPropertyToList(list,"receiveObjectId");
        }
        return result;
    }



    /**
     * 通知管理员 超级管理 + 系统管理员 + 通知管理员
     * @param userId 用户ID 如果为null,则为当前登录用户ID
     * @return
     */
    public static boolean isNoticeAdmin(String userId){
        String _userId = userId;
        if(_userId == null){
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            _userId = sessionInfo.getUserId();
        }

        boolean isAdmin = false;
        if (userManager.isSuperUser(_userId) || SecurityUtils.isPermittedRole(AppConstants.ROLE_SYSTEM_MANAGER)
                || SecurityUtils.isPermittedRole(AppConstants.ROLE_NOTICE_MANAGER)) {//系统管理员 + 通知管理员
            isAdmin = true;
        }
        return isAdmin;
    }
}
