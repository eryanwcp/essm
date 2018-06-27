/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.utils.AppConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 主页管理
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2014-09-16 10:30
 */
@Controller
@RequestMapping(value = "${adminPath}")
public class IndexController extends SimpleController {

    @RequestMapping(value = {""})
    public ModelAndView welcome() {
        ModelAndView modelAnView = new ModelAndView("login");
        if(SecurityUtils.getCurrentSessionInfo() != null){
            modelAnView = new ModelAndView("layout/index");
        }
        return modelAnView;
    }

    @RequestMapping(value = {"index"})
    public ModelAndView index() {
        ModelAndView modelAnView = new ModelAndView("layout/index");
        return modelAnView;
    }

    @RequestMapping("index/west")
    public ModelAndView west() {
        ModelAndView modelAnView = new ModelAndView("layout/west");
        User sessionUser = SecurityUtils.getCurrentUser();
        modelAnView.addObject("user", sessionUser);
        String userPhoto = null;
        if(StringUtils.isNotBlank(sessionUser.getPhoto())){
            userPhoto = sessionUser.getPhotoUrl();
        }else{
            userPhoto = SpringMVCHolder.getRequest().getContextPath()+"/static/img/icon_boy.png";
        }
        modelAnView.addObject("userPhoto", userPhoto);
        return modelAnView;
    }


}
