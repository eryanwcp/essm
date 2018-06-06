/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package com.eryansky.core.web.interceptor;

import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Result;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.Exceptions;
import com.eryansky.common.utils.SysConstants;
import com.eryansky.common.utils.SysUtils;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.modules.sys.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.Set;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-05-05 12:59
 */
public class ExceptionInterceptor implements HandlerExceptionResolver {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static LogService logService = SpringContextHolder.getBean(LogService.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Result result = null;
        //开发模式下打印堆栈信息
        if(SysConstants.isdevMode()){
            ex.printStackTrace();
        }else{
            logger.error(ex.getMessage(),ex);
        }
        //非Ajax请求 将跳转到500错误页面
//        if(!WebUtils.isAjaxRequest(request)){
//            throw ex;
//        }
        //Ajax方式返回错误信息
        String emsg = ex.getMessage();
        StringBuilder sb = new StringBuilder();
        final String MSG_DETAIL = " 详细信息:";
        boolean isWarn = false;//是否是警告级别的异常
        Object obj = null;//其它信息
        sb.append("发生异常:");
        //Hibernate Validator Bo注解校验异常处理
        if(Exceptions.isCausedBy(ex, ConstraintViolationException.class)){
            isWarn = true;
            ConstraintViolationException ce = (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> set =  ce.getConstraintViolations();
            Iterator<?> iterator = set.iterator();
            int i = -1;
            while(iterator.hasNext()){
                ConstraintViolation<?> c = (ConstraintViolation<?>) iterator.next();
                sb.append(c.getMessage());
                i++;
                if(i==0){
                    obj = c.getPropertyPath().toString();
                }
                if (i < set.size() - 1) {
                    sb.append(",");
                }else{
                    sb.append(".");
                }
            }
            if(SysConstants.isdevMode()){
                sb.append(MSG_DETAIL).append(ex.getMessage());//将":"替换为","
            }
        }
        //参数类异常 Spring Assert、Apache Common Validate抛出该异常
        else if(Exceptions.isCausedBy(ex, IllegalArgumentException.class)){
            isWarn = true;
            sb.append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
        }
        //空指针异常
        else if(Exceptions.isCausedBy(ex, NullPointerException.class)){
            sb.append("程序没写好,发生空指针异常！");
            if(SysConstants.isdevMode()){
                sb.append(MSG_DETAIL).append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
            }
        }

        //业务异常
        else if(Exceptions.isCausedBy(ex, ServiceException.class)){
            ServiceException serviceException = (ServiceException) ex;
            result = new Result(serviceException.getCode(), serviceException.getMessage(), serviceException.getObj());
        }

        //系统异常
        else if(Exceptions.isCausedBy(ex, SystemException.class)){
            sb.append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
        }

        //其它异常
        else{
            if(SysConstants.isdevMode()){
                sb.append(MSG_DETAIL).append(SysUtils.jsonStrConvert(emsg));//将":"替换为","
            }else{
                sb.append("未知异常！");
            }
        }
        if(isWarn){
            result = new Result(Result.WARN,sb.toString(),obj);
            logger.warn(result.toString());
        }else{
            if(result == null){
                result = new Result(Result.ERROR,sb.toString(),obj);
            }
            logger.error(result.toString());
        }
//        Map<String, Object> model = Maps.newHashMap();
//        model.put("ex", ex);
//        return  new ModelAndView("error-business", model);

        //异步方式返回异常信息
        WebUtils.renderText(response, result);
        return null;
    }
}
