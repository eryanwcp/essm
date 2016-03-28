/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.exception.ActionException;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 在线用户管理
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-05-18 
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/session")
public class SessionController extends SimpleController {

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
    public Datagrid<SessionInfo> onLineSessions(){
        return  SecurityUtils.getSessionUser();
    }


    /**
     * 强制用户下线
     * @param sessionIds sessionID集合
     * @return
     */
    @RequestMapping(value = {"offline"})
    @ResponseBody
    public Result offline(@RequestParam(value = "sessionIds")List<String> sessionIds){
        SecurityUtils.offLine(sessionIds);
        return Result.successResult();
    }

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
}