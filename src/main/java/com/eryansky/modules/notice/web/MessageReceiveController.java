/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.web;

import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.web.annotation.Mobile;
import com.eryansky.core.web.annotation.MobileValue;
import com.eryansky.modules.notice.mapper.MessageReceive;
import com.eryansky.modules.notice.service.MessageReceiveService;
import com.eryansky.modules.notice.service.MessageTask;
import com.eryansky.modules.notice.utils.MessageUtils;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys._enum.YesOrNo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 消息接收 个人消息中心
 *
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2016-04-14
 */
@Controller
@RequestMapping(value = "${adminPath}/notice/messageReceive")
public class MessageReceiveController extends SimpleController {

    @Autowired
    private MessageReceiveService messageReceiveService;
    @Autowired
    private MessageTask messageTask;

    @ModelAttribute
    public MessageReceive get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return messageReceiveService.get(id);
        } else {
            return new MessageReceive();
        }
    }

    /**
     * 个人消息列表
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @Mobile(value = MobileValue.ALL)
    @Logging(logType = LogType.access, value = "消息中心")
    @RequestMapping(value = {"", "list"})
    public String list(@ModelAttribute MessageReceive model, HttpServletRequest request, HttpServletResponse response, Model uiModel) {
        Page<MessageReceive> page = new Page<MessageReceive>(request, response);
        if (StringUtils.isBlank(model.getIsRead())) {
            model.setIsRead(YesOrNo.NO.getValue());
        }
        if (WebUtils.isAjaxRequest(request)) {
            page = MessageUtils.findUserMessages(page.getPageNo(), page.getPageSize(), model.getIsRead());
            return renderString(response, page);
        } else {
            page = MessageUtils.findUserMessages(page.getPageNo(), page.getPageSize(), model.getIsRead());
        }

        uiModel.addAttribute("page", page);
        uiModel.addAttribute("model", model);
        return "modules/notice/messageReceiveList";
    }

    /**
     * 个人消息详情
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "info")
    public ModelAndView info(@ModelAttribute MessageReceive model, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/messageReceiveInfo");
        messageReceiveService.setRead(model);
//        messageTask.setRead(model);
        modelAndView.addObject("model", model);
        return modelAndView;
    }

    /**
     * 设置已读(异步)
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "setRead")
    @ResponseBody
    public Result setRead(@ModelAttribute MessageReceive model) {
        messageTask.setRead(model);
        return Result.successResult();
    }

    /**
     * 设置全部已读
     *
     * @return
     */
    @RequestMapping(value = "setReadAll")
    @ResponseBody
    public Result setReadAll() {
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
//        messageTask.setReadAll(sessionInfo.getUserId());
        messageReceiveService.setReadAll(sessionInfo.getUserId(), null);
        return Result.successResult();
    }

}