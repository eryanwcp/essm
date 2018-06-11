/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.notice.mapper.Notice;
import com.eryansky.modules.notice.mapper.NoticeReceiveInfo;
import com.eryansky.modules.notice.service.NoticeReceiveInfoService;
import com.eryansky.modules.notice.service.NoticeSendInfoService;
import com.eryansky.modules.notice.service.NoticeService;
import com.eryansky.modules.sys.service.UserService;
import com.eryansky.utils.AppConstants;

import java.util.Collections;
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

    private static UserService userService = SpringContextHolder.getBean(UserService.class);
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
        NoticeReceiveInfo noticeReceiveInfo = noticeReceiveInfoService.getUserNotice(SecurityUtils.getCurrentUserId(), noticeId);
        return noticeReceiveInfo != null && noticeReceiveInfo.isRead();
    }


    public static List<String> findFileIdsByNoticeId(String noticeId) {
        if(StringUtils.isBlank(noticeId)){
            return Collections.emptyList();
        }
        return noticeService.findFileIdsByNoticeId(noticeId);
    }

    public static List<String> findNoticeReceiveUserIds(String noticeId) {
        if(StringUtils.isBlank(noticeId)){
            return Collections.emptyList();
        }
        return noticeSendInfoService.findUserIdsByNoticeId(noticeId);
    }

    public static List<String> findNoticeReceiveOrganIds(String noticeId) {
        if(StringUtils.isBlank(noticeId)){
            return Collections.emptyList();
        }
        return noticeSendInfoService.findOrganIdsByNoticeId(noticeId);
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
        if (userService.isSuperUser(_userId) || SecurityUtils.isPermittedRole(AppConstants.ROLE_SYSTEM_MANAGER)
                || SecurityUtils.isPermittedRole(AppConstants.ROLE_NOTICE_MANAGER)) {//系统管理员 + 通知管理员
            isAdmin = true;
        }
        return isAdmin;
    }
}
