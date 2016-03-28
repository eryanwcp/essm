/**
 *  Copyright (c) 2012-2013 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.mapper.Log;
import com.eryansky.modules.sys.service.LogService;
import com.eryansky.utils.SelectType;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 日志
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-12-8 下午5:13
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/log")
public class LogController extends SimpleController {

    @Autowired
    private LogService logService;

    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/log";
    }


    @ModelAttribute
    public Log get(@RequestParam(required=false) String id) {
        if (StringUtils.isNotBlank(id)){
            return logService.get(id);
        }else{
            return new Log();
        }
    }

    /**
     *
     * @param log
     * @param name 姓名或登录名
     * @param request
     * @param response
     * @param uiModel
     * @return
     */
    @RequestMapping(value = {"datagrid"})
    @ResponseBody
    public Datagrid<Log> datagrid(Log log,String name,HttpServletRequest request,HttpServletResponse response,Model uiModel) {
        Page<Log> page = new Page<Log>(request);
        log.setUserId(name);
        page = logService.findPage(page,log);
        Datagrid<Log> dg = new Datagrid<Log>(page.getTotalCount(),page.getResult());
        return dg;
    }

    /**
     * 清除日志
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"remove"})
    @ResponseBody
    public Result remove(@RequestParam(value = "ids",required = false)List<String> ids) throws Exception {
        logService.deleteByIds(ids);
        Result result = Result.successResult();
        return result;
    }

    /**
     * 清空所有日志
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"removeAll"})
    @ResponseBody
    public Result removeAll() throws Exception {
        logService.removeAll();
        Result result = Result.successResult();
        return result;
    }

    /**
     * 日志类型下拉列表.
     */
    @RequestMapping(value = {"logTypeCombobox"})
    @ResponseBody
    public List<Combobox> logTypeCombobox(String selectType) throws Exception {
        List<Combobox> cList = Lists.newArrayList();
        Combobox selectCombobox = SelectType.combobox(selectType);
        if (selectCombobox != null) {
            cList.add(selectCombobox);
        }

        LogType[] lts = LogType.values();
        for (int i = 0; i < lts.length; i++) {
            Combobox combobox = new Combobox();
            combobox.setValue(lts[i].getValue().toString());
            combobox.setText(lts[i].getDescription());
            cList.add(combobox);
        }
        return cList;
    }

    /**
     * 数据修复 title
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"dataAutoFix"})
    @ResponseBody
    public Result dataAutoFix() throws Exception {
        logService.dataAutoFix();
        Result result = Result.successResult();
        return result;
    }


}
