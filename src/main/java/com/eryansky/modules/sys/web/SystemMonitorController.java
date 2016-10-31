/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Result;
import com.eryansky.common.web.springmvc.SimpleController;
import com.jfit.utils.ServerStatus;
import com.jfit.utils.SigarUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 系统监控
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-10-28 
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/systemMonitor")
public class SystemMonitorController extends SimpleController {


    /**
     * 系统信息
     * @return
     */
    @RequestMapping("")
    public ModelAndView list(){
        ModelAndView modelAndView = new ModelAndView("modules/sys/systemMonitorList");
        return modelAndView;
    }


    /**
     * 系统信息
     * @return
     */
    @RequestMapping("systemInfo")
    @ResponseBody
    public Result systemInfo(){
        Result result = null;
        ServerStatus serverStatus = null;
        try {
            serverStatus = SigarUtil.getServerStatus();
            result = Result.successResult().setObj(serverStatus);
        } catch (Exception e) {
            result = Result.errorResult();
            logger.error(e.getMessage());
        }
        return result;
    }

}
