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
import com.eryansky.common.utils.DateUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.excelTools.ExcelUtils;
import com.eryansky.core.excelTools.JsGridReportBase;
import com.eryansky.core.excelTools.TableData;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.core.security.annotation.RequiresRoles;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.mapper.Log;
import com.eryansky.modules.sys.service.LogService;
import com.eryansky.utils.AppConstants;
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
import java.util.ArrayList;
import java.util.Iterator;
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

    @Logging(value = "日志管理",logType = LogType.access)
    @RequestMapping(value = {""})
    public String list() {
        return "modules/sys/log";
    }


    @ModelAttribute("model")
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
    public String datagrid(Log log,String name,HttpServletRequest request,HttpServletResponse response,Model uiModel) {
        Page<Log> page = new Page<Log>(request);
        log.setUserId(name);
        page = logService.findPage(page,log);
        Datagrid<Log> dg = new Datagrid<Log>(page.getTotalCount(),page.getResult());
        String json = JsonMapper.getInstance().toJsonWithExcludeProperties(dg,Log.class,new String[]{"exception"});
        return json;
    }


    /**
     *
     * @param log
     * @param request
     * @param response
     * @param uiModel
     * @return
     */
    @RequiresRoles(value = AppConstants.ROLE_SYSTEM_MANAGER)
    @RequestMapping(value = {"export"})
    public void export(Log log,HttpServletRequest request,HttpServletResponse response,Model uiModel) {
        response.setContentType("application/msexcel;charset=UTF-8");
        Page<Log> page = new Page<Log>(request);
//        page.setPageSize(Page.PAGESIZE_ALL);
        page = logService.findPage(page,log);

        List<Object[]> list = new ArrayList<Object[]>();
        Iterator<Log> iterator = page.getResult().iterator();
        while (iterator.hasNext()){
            Log _log = iterator.next();
            list.add(new Object[]{_log.getTypeView(),_log.getTitle(),_log.getUserCompanyName(),_log.getUserOfficeName(),_log.getUserName(),_log.getIp(),_log.getModule(), DateUtils.formatDateTime(_log.getOperTime()),_log.getActionTime()});
        }

        List<TableData> tds = new ArrayList<TableData>();

        String title = "审计日志-"+DateUtils.getCurrentDate();
        //Sheet2
        String[] hearders = new String[] {"日志类型","标题","单位","部门", "姓名", "IP地址", "模块","操作时间","操作耗时(ms)"};//表头数组
        TableData td = ExcelUtils.createTableData(list, ExcelUtils.createTableHeader(hearders),null);
        td.setSheetTitle(title);
        tds.add(td);

        try {
            JsGridReportBase report = new JsGridReportBase(request, response);
            report.exportToExcel(title, SecurityUtils.getCurrentSessionInfo().getName(), tds);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }


    /**
     * 日志明细信息
     * @param log
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"detail"})
    @ResponseBody
    public Result detail(@ModelAttribute("model") Log log) throws Exception {
        Result result = Result.successResult().setObj(log);
        return result;
    }


    /**
     * 清除日志
     *
     * @return
     * @throws Exception
     */
    @Logging(value = "日志管理-删除日志",logType = LogType.access)
    @RequiresRoles(value = AppConstants.ROLE_SYSTEM_MANAGER)
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
    @Logging(value = "日志管理-清除日志",logType = LogType.access)
    @RequiresRoles(value = AppConstants.ROLE_SYSTEM_MANAGER)
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
            combobox.setValue(lts[i].getValue());
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
