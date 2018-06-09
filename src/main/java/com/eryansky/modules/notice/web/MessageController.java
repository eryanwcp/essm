/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.notice.web;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.modules.notice.service.MessageTask;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.listener.SystemInitListener;
import com.eryansky.modules.notice._enum.MessageReceiveObjectType;
import com.eryansky.modules.notice._enum.TipMessage;
import com.eryansky.modules.notice.mapper.Message;
import com.eryansky.modules.notice.mapper.MessageReceive;
import com.eryansky.modules.notice.service.MessageReceiveService;
import com.eryansky.modules.notice.service.MessageService;
import com.eryansky.modules.notice.utils.MessageUtils;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.SelectType;
import com.eryansky.server.result.WSResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-24 
 */
@Controller
@RequestMapping(value = "${adminPath}/notice/message")
public class MessageController extends SimpleController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageReceiveService messageReceiveService;
    @Autowired
    private MessageTask messageTask;

    @ModelAttribute("model")
    public Message get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return messageService.get(id);
        } else {
            return new Message();
        }
    }


    @RequiresPermissions("notice:message:view")
    @RequestMapping(value = {"list", "","audit"})
    public ModelAndView list(@ModelAttribute("model")Message model, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/messageList");
        if(!SecurityUtils.isCurrentUserAdmin()){//非管理员
            User user = SecurityUtils.getCurrentUser();
            if(user != null && model != null){
                model.setOrganId(user.getCompanyId());
            }
        }
        Page<Message> page = messageService.findPage(new Page<Message>(request, response), model);
        modelAndView.addObject("page", page);
        modelAndView.addObject("model", model);
        return modelAndView;
    }

    @RequiresPermissions("notice:message:view")
    @RequestMapping(value = "form")
    public ModelAndView form(@ModelAttribute("model")Message model) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/messageForm");
        if(StringUtils.isBlank(model.getReceiveObjectType())){
            model.setReceiveObjectType(MessageReceiveObjectType.User.getValue());
        }
        modelAndView.addObject("model", model);
        modelAndView.addObject("messageReceiveObjectTypes", MessageReceiveObjectType.values());
        return modelAndView;
    }

    /**
     * 保存 发送消息
     * @param model
     * @param uiModel
     * @param redirectAttributes
     * @param receiveObjectType {@link MessageReceiveObjectType}
     * @param objectIds
     * @return
     */
    @RequiresPermissions("notice:message:edit")
    @RequestMapping(value = "save")
    public ModelAndView save(@ModelAttribute("model")Message model, Model uiModel, RedirectAttributes redirectAttributes,
                             String receiveObjectType,
                             @RequestParam(value = "objectIds")List<String> objectIds) {
        if (!beanValidator(uiModel, model)) {
            return form(model);
        }

        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(StringUtils.isBlank(model.getOrganId())){
            model.setOrganId(sessionInfo.getLoginOrganId());
        }

        if(StringUtils.isBlank(model.getSender())){
            model.setSender(sessionInfo.getUserId());
        }

        messageService.save(model);

        MessageReceiveObjectType messageReceiveObjectType = MessageReceiveObjectType.getByValue(receiveObjectType);
        if(StringUtils.isNotBlank(receiveObjectType) && messageReceiveObjectType != null){
            messageTask.saveAndSend(model, messageReceiveObjectType, objectIds);
        }

        addMessage(redirectAttributes, "消息正在发送...请稍候！");
        ModelAndView modelAndView = new ModelAndView("redirect:" + AppConstants.getAdminPath() + "/notice/message/?repage");
        return modelAndView;
    }

    @RequiresPermissions("notice:message:edit")
    @RequestMapping(value = "delete")
    public ModelAndView delete(@ModelAttribute("model")Message model, @RequestParam(required = false) Boolean isRe, RedirectAttributes redirectAttributes) {
        messageService.delete(model,isRe);
        addMessage(redirectAttributes, (isRe != null && isRe ? "恢复" : "") + "删除消息成功");
        ModelAndView modelAndView = new ModelAndView("redirect:" + AppConstants.getAdminPath() + "/notice/message/?repage");
        return modelAndView;
    }

    @RequiresPermissions("notice:message:view")
    @RequestMapping(value = "info")
    public ModelAndView info(@ModelAttribute("model")Message model, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/messageInfo");
        modelAndView.addObject("model", model);
        //接收信息
        Page<MessageReceive> page = new Page<MessageReceive>(request,response);
        MessageReceive messageReceive = new MessageReceive(model.getId());
        page = messageReceiveService.findPage(page,messageReceive);
        modelAndView.addObject("page", page);
        return modelAndView;
    }


    @RequestMapping(value = "view")
    public ModelAndView view(@ModelAttribute("model")Message model, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("modules/notice/messageView");
        modelAndView.addObject("model", model);
        return modelAndView;
    }


    /**
     * 发送消息 （可供外部调用）
     * @param content 必选 消息内容
     * @param linkUrl 消息URL链接地址
     * @param receiveObjectType 必选 接收类型 {@link MessageReceiveObjectType}
     * @param receiveObjectIds 必选 接收对象ID集合 多个之间以”,“分割
     * @return
     */
    @RequestMapping(value = "sendMessage")
    @ResponseBody
    public Result sendMessage(@RequestParam(value = "content") String content,
                              String linkUrl,
                              @RequestParam(value = "receiveObjectType") String receiveObjectType,
                              @RequestParam(value = "receiveObjectIds") List<String> receiveObjectIds) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        MessageUtils.sendMessage(null,sessionInfo.getUserId(),content,linkUrl,receiveObjectType,receiveObjectIds);
        result = Result.successResult().setMsg("消息正在发送...请稍候！");
        return result;
    }


    /**
     * 发送消息给管理员 （可供外部调用）
     * @param content 必选 消息内容
     * @param linkUrl 消息URL链接地址
     * @return
     */
    @RequestMapping(value = "sendToManager")
    @ResponseBody
    public Result sendToManager(@RequestParam(value = "content") String content,
                                String linkUrl) {
        Result result = null;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        User superUser = UserUtils.getSuperUser();
        List<String> receiveObjectIds = new ArrayList<String>(1);
        receiveObjectIds.add(superUser.getId());
        MessageUtils.sendMessage(null,sessionInfo.getUserId(),content,linkUrl,MessageReceiveObjectType.User.getValue(),receiveObjectIds);
        result = Result.successResult().setMsg("消息正在发送...请稍候！");
        return result;
    }

    /**
     * 消息提醒 下拉列表
     * @param selectType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "tipMessageCombobox" })
    @ResponseBody
    public List<Combobox> tipMessageCombobox(String selectType) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox titleCombobox = SelectType.combobox(selectType);
        if(titleCombobox != null){
            cList.add(titleCombobox);
        }
        TipMessage[] _emums = TipMessage.values();
        for (TipMessage column : _emums) {
            Combobox combobox = new Combobox(column.getValue(),column.getDescription());
            cList.add(combobox);
        }
        return cList;
    }

    /**
     * 外部消息接口
     * @param paramJson
     * @return
     * @throws Exception
     */
    @RequiresUser(required = false)
    @RequestMapping(value = { "api/sendMessage"},method = RequestMethod.POST)
    @ResponseBody
    public WSResult sendMessage(String paramJson) {
        if(SystemInitListener.apiWebService != null){
            return SystemInitListener.apiWebService.sendMessage(paramJson);
        }
        return WSResult.buildDefaultErrorResult(WSResult.class);
    }


}