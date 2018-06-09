/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.sys.mapper.SystemSerialNumber;
import com.eryansky.modules.sys.service.SystemSerialNumberService;
import com.eryansky.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-07-18 
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/systemSerialNumber")
public class SystemSerialNumberController extends SimpleController {

    @Autowired
    private SystemSerialNumberService systemSerialNumberService;

    @ModelAttribute
    public SystemSerialNumber get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemSerialNumberService.get(id);
        } else {
            return new SystemSerialNumber();
        }
    }

    @RequiresPermissions("sys:systemSerialNumber:view")
    @RequestMapping(value = {"list", ""})
    public String list(SystemSerialNumber model, HttpServletRequest request,HttpServletResponse response,Model uiModel) {
        Page<SystemSerialNumber> page = new Page<SystemSerialNumber>(request,response);
        page = systemSerialNumberService.findPage(page,model);
        uiModel.addAttribute("page",page);
        return "modules/sys/systemSerialNumberList";
    }


    @RequiresPermissions("sys:systemSerialNumber:view")
    @RequestMapping(value = "form")
    public String form(SystemSerialNumber model, Model uiModel) {
        uiModel.addAttribute("model", model);
        return "modules/sys/systemSerialNumberForm";
    }

    @RequiresPermissions("sys:systemSerialNumber:edit")
    @RequestMapping(value = "save")
    public String save(SystemSerialNumber model, Model uiModel, RedirectAttributes redirectAttributes) {
        if (!beanValidator(uiModel, model)){
            return form(model, uiModel);
        }
        systemSerialNumberService.save(model);
        addMessage(redirectAttributes, "保存'" + model.getModuleName() + "'成功");
        return "redirect:" + AppConstants.getAdminPath() + "/sys/systemSerialNumber/";
    }

    @RequiresPermissions("sys:systemSerialNumber:edit")
    @RequestMapping(value = "delete")
    public String delete(SystemSerialNumber model, RedirectAttributes redirectAttributes) {
        systemSerialNumberService.delete(model);
        addMessage(redirectAttributes, "删除成功");
        return "redirect:" + AppConstants.getAdminPath() + "/sys/systemSerialNumber/";
    }

}