/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Result;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.ApplicationSessionContext;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.utils.AppConstants;
import com.eryansky.utils.CacheUtils;
import com.eryansky.utils.ServerStatus;
import com.eryansky.utils.SigarUtil;
import net.sf.ehcache.Ehcache;
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
    @Logging(value = "系统监控",logType = LogType.access)
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

    /**
     * 清空缓存
     * @param cacheName 缓存名称
     * @return
     */
    @Logging(value = "系统监控-清空缓存",logType = LogType.access)
    @RequiresPermissions(value = AppConstants.ROLE_SYSTEM_MANAGER)
    @RequestMapping("clearCache")
    @ResponseBody
    public Result clearCache(String cacheName){
        Result result = null;
        try {
            //清空ehcache缓存
            if(StringUtils.isNotBlank(cacheName)){
                CacheUtils.removeCache(cacheName);
            }else{
                String[] cacheNames = CacheUtils.getCacheManager().getCacheNames();
                for (String _cacheName : cacheNames) {
                    if(!ApplicationSessionContext.CACHE_SESSION.equals(_cacheName)){//黑名单
                        Ehcache cache = CacheUtils.getCacheManager().getEhcache(_cacheName);
                        cache.removeAll();
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

}
