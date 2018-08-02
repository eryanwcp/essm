/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.sys._enum.LogType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 在线用户管理
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-05-18 
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/session")
public class SessionController extends SimpleController {

    @RequiresPermissions("sys:session:view")
    @Logging(value = "在线用户",logType = LogType.access)
    @RequestMapping(value = {""})
    public ModelAndView list(){
        return new ModelAndView("modules/sys/session");
    }

    /**
     * 在线用户
     * @return
     */
    @RequestMapping(value = {"onLineSessions"})
    @ResponseBody
    public Datagrid<SessionInfo> onlineDatagrid(HttpServletRequest request) throws Exception {
        Page<SessionInfo> page = new Page<SessionInfo>(request);
        page = SecurityUtils.findSessionUserPage(page);
        Datagrid<SessionInfo> dg = new Datagrid<SessionInfo>(page.getTotalCount(),page.getResult());
        return dg;
    }


    /**
     * 强制用户下线
     * @param sessionIds sessionID集合
     * @return
     */
    @RequiresPermissions("sys:session:edit")
    @RequestMapping(value = {"offline"})
    @ResponseBody
    public Result offline(@RequestParam(value = "sessionIds")List<String> sessionIds){
        SecurityUtils.offLine(sessionIds);
        return Result.successResult();
    }

    @RequiresPermissions("sys:session:edit")
    @RequestMapping(value = {"offlineAll"})
    @ResponseBody
    public Result offlineAll(){
        if(SecurityUtils.isCurrentUserAdmin()){
            SecurityUtils.offLineAll();
        }else{
            throw new ActionException("未授权.");
        }

        return Result.successResult();
    }


    /**
     * 详细信息
     * @param id
     * @return
     */
    @RequestMapping(value = {"detail"})
    @ResponseBody
    public Result detail(String id){
        SessionInfo sessionInfo = SecurityUtils.getSessionInfo(id);
        return Result.successResult().setObj(sessionInfo);
    }
}