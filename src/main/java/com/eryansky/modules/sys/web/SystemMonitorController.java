/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Result;
import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.io.FileUtils;
import com.eryansky.common.utils.io.PropertiesLoader;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.ApplicationSessionContext;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.utils.*;
import net.sf.ehcache.Ehcache;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    @RequiresPermissions("sys:systemMonitor:view")
    @Logging(value = "系统监控",logType = LogType.access,logging = "!#isAjax")
    @RequestMapping("")
    public String list(HttpServletRequest request,HttpServletResponse response){
        if(WebUtils.isAjaxRequest(request)){
            ServerStatus serverStatus = null;
            try {
                serverStatus = SigarUtil.getServerStatus();
                return renderString(response,Result.successResult().setObj(serverStatus));
            } catch (Exception e) {
                logger.error(e.getMessage());
                return renderString(response,Result.errorResult());
            }
        }
        return "modules/sys/systemMonitor";
    }


    /**
     * 清空缓存
     * @param cacheName 缓存名称
     * @return
     */
    @Logging(value = "系统监控-清空缓存",logType = LogType.access)
    @RequiresPermissions("sys:systemMonitor:edit")
    @RequestMapping("clearCache")
    @ResponseBody
    public Result clearCache(String cacheName){
        Result result = null;
        try {
            //清空ehcache缓存
            if(StringUtils.isNotBlank(cacheName)){
                CacheUtils.removeCache(cacheName);
            }else{
                Collection<String> cacheNames = CacheUtils.cacheNames();
                for (String _cacheName : cacheNames) {
                    if(!ApplicationSessionContext.CACHE_SESSION.equals(_cacheName)){//黑名单
                        CacheUtils.removeCache(_cacheName);
                    }
                }
            }
            //更新客户端缓存时间戳
            AppConstants.SYS_INIT_TIME = System.currentTimeMillis();
            result = Result.successResult();
        } catch (Exception e) {
            result = Result.errorResult();
            logger.error(e.getMessage());
        }
        return result;
    }


    /**
     * 系统监控-系统日志
     * @param download 下载
     * @param request
     * @param response
     * @param uiModel
     * @return
     */
    @Logging(value = "系统监控-系统日志",logType = LogType.access,logging = "!#isAjax")
    @RequiresPermissions("sys:systemMonitor:view")
    @RequestMapping("log")
    public String log(@RequestParam(value = "download",defaultValue = "false") boolean download,
                      Integer pageSize,
                      HttpServletRequest request, HttpServletResponse response, Model uiModel){
        Result result = null;
        File file = null;
        if(download || WebUtils.isAjaxRequest(request)){
            PropertiesLoader propertiesLoader =AppConstants.getPropertiesLoader("log4j");
            String appDir = AppUtils.getAppAbsolutePath();
            String servletContextName = AppUtils.getServletContext().getServletContextName();
            String logConfigPath = StringUtils.substringAfter(propertiesLoader.getProperty("log4j.appender.RollingFile.File"),"/");
            String logPath = StringUtils.substringBefore(appDir,servletContextName).replace("webapps","").replace(File.separator+File.separator,File.separator) + logConfigPath;
            String _logPath = AppConstants.getLogPath(logPath);//读取配置文件配置的路径
            file = new File(_logPath);
        }
        if(download){
            WebUtils.setDownloadableHeader(request,response,file.getName());
            BufferedInputStream is = null;
            OutputStream os = null;
            try {
                os = response.getOutputStream();
                is = new BufferedInputStream(new FileInputStream(file));
                IOUtils.copy(is, os);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
            }
            return null;
        }
        if(WebUtils.isAjaxRequest(request)){
            try {
                // 读取日志
                List<String> logs = FileUtils.readLines(file, "utf-8");
                List<String> showLogs = logs;
                StringBuffer log = new StringBuffer();
                Collections.reverse(logs);
                Page page = new Page(request,response);
                page.setPageSize(pageSize == null ? 1000:pageSize);//最大读取行数
                if(page.getPageSize()!= Page.PAGESIZE_ALL){
                    showLogs = Collections3.getPagedList(logs,page.getPageNo(),page.getPageSize());
                    page.setResult(showLogs);
                    page.setTotalCount(showLogs.size());
                }
                for(int i = showLogs.size()-1;i >= 0;i--){
                    String line = logs.get(i);
                    log.append(line.replace("\t","&nbsp;")+"<br>");
                }
                return renderString(response,Result.successResult().setMsg(log.toString()).setObj(page));
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
                return renderString(response,Result.errorResult().setMsg(e.getMessage()));
            }
        }
        return "modules/sys/systemMonitor-log";
    }

}
