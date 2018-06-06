/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.utils;

import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.security.SecurityUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * Spring工具类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-04-13 
 */
public class SpringUtils {
    /**
     * 解析Spring SpEL表达式
     * @param str 原表达式
     * @param method 方法
     * @param args 参数
     * @return
     */
    public static String parseSpel(String str, Method method, Object[] args) {
        try {
            //获取被拦截方法参数名列表(使用Spring支持类库)
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String[] paraNameArr = u.getParameterNames(method);

            //使用SPEL进行key的解析
            ExpressionParser parser = new SpelExpressionParser();
            //SPEL上下文
            StandardEvaluationContext context = new StandardEvaluationContext();
            //把方法参数放入SPEL上下文中
            for (int i = 0; i < paraNameArr.length; i++) {
                context.setVariable(paraNameArr[i], args[i]);
            }
            //Ajax请求
            context.setVariable("isAjax", WebUtils.isAjaxRequest(SpringMVCHolder.getRequest()));
            //Session信息
            context.setVariable("sessionInfo", SecurityUtils.getCurrentSessionInfo());
            return parser.parseExpression(str).getValue(context, String.class);
        } catch (Exception e) {
//            logger.error(e.getMessage(),e);
        }
        return str;
    }
}
