/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.web.annotation.MobileValue;
import com.eryansky.modules.sys.service.UserPasswordService;
import com.eryansky.modules.sys.service.UserService;
import com.google.common.collect.Maps;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.modules.notice.mapper.NoticeReceiveInfo;
import com.eryansky.modules.notice.service.NoticeReceiveInfoService;
import com.eryansky.modules.notice.service.NoticeService;
import com.eryansky.modules.sys.mapper.UserPassword;
import com.eryansky.utils.AppConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Portal主页门户管理
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2014-07-31 12:30
 */
@Controller
@RequestMapping(value = "${adminPath}/portal")
public class PortalController extends SimpleController {

    @Autowired
    private UserService userService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeReceiveInfoService noticeReceiveInfoService;
    @Autowired
    private UserPasswordService userPasswordService;


    @RequestMapping("")
    public ModelAndView portal() {
        ModelAndView modelAnView = new ModelAndView("layout/portal");
        return modelAnView;
    }

    /**
     * 个人消息中心
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("mymessages")
    @ResponseBody
    public Result mymessages(HttpServletRequest request,HttpServletResponse response) throws Exception {
        WebUtils.setNoCacheHeader(response);
        Result result = null;
        Map<String, Long> map = Maps.newHashMap();
        // 当前登录用户
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        long noticeReceiveInfos = 0;
        Page<NoticeReceiveInfo> page = new Page<NoticeReceiveInfo>(request);
        page = noticeReceiveInfoService.findUserUnreadNotices(page,sessionInfo.getUserId());
        if(Collections3.isNotEmpty(page.getResult())){
            noticeReceiveInfos = page.getTotalCount();
        }
        map.put("noticeReceiveInfos", noticeReceiveInfos);

        if(AppConstants.getIsSecurityOn()){
            Long tipPasswordType = checkPassword(sessionInfo.getUserId());
            map.put("tipPasswordType", tipPasswordType);
        }

        result = Result.successResult().setObj(map);
//        logger.info(map.toString());
        return result;
    }


    /**
     * 个人消息中心
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("checkPassword")
    @ResponseBody
    public Result checkPassword(HttpServletResponse response) throws Exception {
        WebUtils.setNoCacheHeader(response);
        Result result = null;
        Map<String, Object> map = Maps.newHashMap();
        // 当前登录用户
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();

        if(AppConstants.getIsSecurityOn()){
            Long tipPasswordType = checkPassword(sessionInfo.getUserId());
            String tipPasswordMsg = null;
            if(tipPasswordType != null){
                int userPasswordUpdateCycle = AppConstants.getUserPasswordUpdateCycle();
                if(tipPasswordType==0L){
                    tipPasswordMsg = "您从未修改过用户密码，请修改用户密码！";
                }else if(tipPasswordType==1L){
                    tipPasswordMsg = "您已超过"+userPasswordUpdateCycle+"天没有修改用户密码，请修改用户密码！";
                }
            }

            map.put("tipPasswordType", tipPasswordType);
            map.put("tipPasswordMsg", tipPasswordMsg);
        }

        result = Result.successResult().setObj(map);
        return result;
    }

    private Long checkPassword(String userId){
        UserPassword userPassword = userPasswordService.getLatestUserPasswordByUserId(userId);
//            String tipPasswordMsg = "";
        Calendar calendar = Calendar.getInstance();
        int userPasswordUpdateCycle = AppConstants.getUserPasswordUpdateCycle();
        calendar.add(Calendar.DATE, - userPasswordUpdateCycle);
        Date time = calendar.getTime();
        Long tipPasswordType = null;


        if(userPassword==null){
//                tipPasswordMsg = "您从未修改过登录秘密，请修改登录密码！";
            tipPasswordType = 0L;
        }else if(time.compareTo(userPassword.getModifyTime()) > 0){
//                tipPasswordMsg = "您已超过"+userPasswordUpdateCycle+"天没有修改登录密码，请修改登录密码！";
            tipPasswordType = 1L;
        }
        return tipPasswordType;
    }
    /**
     * 我的通知
     *
     * @return
     */
    @Mobile(value = MobileValue.PC)
    @RequiresUser(required = false)
    @RequestMapping("notice")
    public ModelAndView notice(HttpServletRequest request,HttpServletResponse response) {
        ModelAndView modelAnView = new ModelAndView("layout/portal-notice");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            Page<NoticeReceiveInfo> page = new Page<NoticeReceiveInfo>(SpringMVCHolder.getRequest());
            page = noticeReceiveInfoService.findReadNoticePage(page,new NoticeReceiveInfo(), sessionInfo.getUserId(),null);
            modelAnView.addObject("page", page);

        }

        return modelAnView;
    }


}
