/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.web.interceptor;

import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.UserAgentUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Lists;
import com.eryansky.core.web.annotation.Mobile;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 手机端视图拦截器
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2015-07-10
 */
public class MobileInterceptor implements HandlerInterceptor {

	/**
	 * 包含的URL
	 */
	private List<String> includeUrls = Lists.newArrayList();

	/**
	 * 排除的URL
	 */
	private List<String> excludeUrls = Lists.newArrayList();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
							 Object handler) throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
						   ModelAndView modelAndView) throws Exception {
		if (modelAndView != null){
			if(UserAgentUtils.isMobileOrTablet(request) && !StringUtils.startsWithIgnoreCase(modelAndView.getViewName(), "redirect:")){
				Boolean flag = null;
				HandlerMethod handlerMethod = null;
				try {
					handlerMethod = (HandlerMethod) handler;
				} catch (ClassCastException e) {
//                logger.error(e.getMessage(),e);
				}

				if (handlerMethod != null) {
					Object bean = handlerMethod.getBean();
					//需要登录
					Mobile methodMobile = handlerMethod.getMethodAnnotation(Mobile.class);
					if (methodMobile != null) {
						flag = methodMobile.able();
					}

					if(methodMobile == null){//类注解处理
						Mobile classMobile =  this.getAnnotation(bean.getClass(),Mobile.class);
						if (classMobile != null) {
							flag = classMobile.able();
						}
					}


				}

				String requestUrl = request.getRequestURI();
				if(flag == null){
					if (Collections3.isNotEmpty(excludeUrls)) {
						for(String excludeUrl:excludeUrls){
							flag = !StringUtils.simpleWildcardMatch(excludeUrl, requestUrl);
							break;
						}
					}
				}

				if(flag == null){
					if (Collections3.isNotEmpty(includeUrls)) {
						for(String includeUrl:includeUrls){
							flag = StringUtils.simpleWildcardMatch(includeUrl, requestUrl);
							break;
						}
					}
				}

				// 如果是手机或平板访问的话，则跳转到手机视图页面。
				if(flag != null && flag){
					modelAndView.setViewName("mobile/" + modelAndView.getViewName());
				}
			}





		}
	}


	private <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
		T result = clazz.getAnnotation(annotationType);
		if (result == null) {
			Class<?> superclass = clazz.getSuperclass();
			if (superclass != null) {
				return getAnnotation(superclass, annotationType);
			} else {
				return null;
			}
		} else {
			return result;
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
								Object handler, Exception ex) throws Exception {
	}

	public List<String> getIncludeUrls() {
		return includeUrls;
	}

	public void setIncludeUrls(List<String> includeUrls) {
		this.includeUrls = includeUrls;
	}

	public List<String> getExcludeUrls() {
		return excludeUrls;
	}

	public void setExcludeUrls(List<String> excludeUrls) {
		this.excludeUrls = excludeUrls;
	}
}
