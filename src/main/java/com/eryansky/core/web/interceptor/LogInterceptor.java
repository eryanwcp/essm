/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.web.interceptor;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.*;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.net.IpUtils;
import com.eryansky.modules.sys.mapper.User;
import com.eryansky.modules.sys.service.*;
import com.google.common.collect.Lists;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.mapper.Log;
import com.eryansky.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志拦截器
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2015-07-10
 */
public class LogInterceptor implements HandlerInterceptor {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	private static final ThreadLocal<Long> startTimeThreadLocal =
			new NamedThreadLocal<Long>("ThreadLocal StartTime");
	@Autowired
	private RequestMappingHandlerAdapter adapter;
	private final Map<Class<?>, Set<Method>> initBinderCache = new ConcurrentHashMap<Class<?>, Set<Method>>(64);
	private List<HandlerMethodArgumentResolver> argumentResolvers;
	private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache =
			new ConcurrentHashMap<MethodParameter, HandlerMethodArgumentResolver>(256);

	/**
	 * 需要拦截的资源
	 */
	private List<String> includeUrls = Lists.newArrayList();
	/**
	 * 排除拦截的资源
	 */
	private List<String> excludeUrls = Lists.newArrayList();

	/**
	 * 静态内部类，延迟加载，懒汉式，线程安全的单例模式
	 */
	public static final class Static {
		private static LogService logService = SpringContextHolder.getBean(LogService.class);
	}

	@Autowired
	public LogInterceptor(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
		argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
							 Object handler) throws Exception {
		long beginTime = System.currentTimeMillis();//1、开始时间
		startTimeThreadLocal.set(beginTime);        //线程绑定变量（该数据只有当前请求的线程可见）
		if (logger.isDebugEnabled()) {
			logger.debug("开始计时: {}  URI: {}", new SimpleDateFormat("hh:mm:ss.SSS")
					.format(beginTime), request.getRequestURI());
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
						   ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("ViewName: " + modelAndView.getViewName());
			}

		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
								Object handler, Exception ex) throws Exception {
		long beginTime = startTimeThreadLocal.get();//得到线程绑定的局部变量（开始时间）
		startTimeThreadLocal.remove();
		long endTime = System.currentTimeMillis();    //2、结束时间
		// 保存日志
		SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		String requestUrl = request.getRequestURI();
		requestUrl = requestUrl.replaceAll("//", "/");

		String newLogValue = null;
		String logType = LogType.access.getValue();
		HandlerMethod handlerMethod = null;
		boolean flag = false;
		Boolean _flag = null;
		String remark = null;
		if (ex != null) {
			flag = true;
			newLogValue = LogType.exception.getDescription();
			logType = LogType.exception.getValue();
		}

		//注解处理
		if (!flag) {
			try {
				handlerMethod = (HandlerMethod) handler;
			} catch (ClassCastException e) {
//                logger.error(e.getMessage(),e);
			}
			Logging logging = null;
			if (handlerMethod != null) {
				//需要登录
				logging = handlerMethod.getMethodAnnotation(Logging.class);
				MethodParameter[] parameters = handlerMethod.getMethodParameters();
				Object[] parameterValues = new Object[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					MethodParameter parameter = parameters[i];
					HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
					ModelAndViewContainer mavContainer = new ModelAndViewContainer();
					mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
					WebDataBinderFactory webDataBinderFactory = getDataBinderFactory(handlerMethod);
					try {
						Object value = resolver.resolveArgument(parameter, mavContainer, webRequest, webDataBinderFactory);
						parameterValues[i] = value;
					} catch (Exception e) {
						logger.error(e.getMessage() + "\n" + requestUrl);
					}
				}

				if (logging != null && Boolean.valueOf(SpringUtils.parseSpel(logging.logging(), handlerMethod.getMethod(), parameterValues))) {
					flag = true;
					_flag = true;
					logType = logging.logType().getValue();
					String logValue = logging.value();
					newLogValue = logValue;
					if (StringUtils.isNotBlank(logValue)) {
						newLogValue = SpringUtils.parseSpel(logValue, handlerMethod.getMethod(), parameterValues);
					}
					if (StringUtils.isNotBlank(logging.remark())) {
						remark = SpringUtils.parseSpel(logging.remark(), handlerMethod.getMethod(), parameterValues);
					}
				} else if (logging != null && !Boolean.valueOf(SpringUtils.parseSpel(logging.logging(), handlerMethod.getMethod(), parameterValues))) {
					_flag = false;
				}
			}

		}

		//自定义配置URL 处理
		if ((_flag == null || _flag) && !flag && Collections3.isNotEmpty(includeUrls)) {
			for (String includeUrl : includeUrls) {
				flag = StringUtils.simpleWildcardMatch(request.getContextPath() + includeUrl, requestUrl);
				if (flag) {
					break;
				}
			}
		}

		//自定义配置URL 处理
		if ((_flag == null || _flag) && flag && Collections3.isNotEmpty(excludeUrls)) {
			for (String excludeUrl : excludeUrls) {
				boolean matchFlag = StringUtils.simpleWildcardMatch(request.getContextPath() + excludeUrl, requestUrl);
				if (matchFlag) {
					flag = false;
					break;
				}
			}
		}

		if ((_flag == null || _flag) && flag) {
			Log log = new Log();
			log.setTitle(newLogValue);
			log.setUserId(sessionInfo == null ? User.SUPERUSER_ID : sessionInfo.getUserId());
			log.setType(logType);
			log.setIp(sessionInfo == null ? IpUtils.getIpAddr0(request):sessionInfo.getIp());
			log.setUserAgent(UserAgentUtils.getHTTPUserAgent(request));
			log.setDeviceType(UserAgentUtils.getDeviceType(request).toString());
			log.setBrowserType(UserAgentUtils.getBrowser(request).getName());
			log.setOperTime(Calendar.getInstance().getTime());
			log.setActionTime((endTime - beginTime) + "");
			log.setModule(requestUrl);
			log.setAction(request.getMethod());
			log.setRemark(remark);
			log.setParams(request.getParameterMap());
			// 如果有异常，设置异常信息
			log.setException(Exceptions.getStackTraceAsString(ex));
			Static.logService.saveAspectLog(log, handlerMethod);

		}

		// 打印JVM信息。
		if (logger.isDebugEnabled()) {
			logger.debug("计时结束：{}  耗时：{}  URI: {}  最大内存: {}m  已分配内存: {}m  已分配内存中的剩余空间: {}m  最大可用内存: {}m",
					new Object[]{new SimpleDateFormat("hh:mm:ss.SSS").format(endTime), DateUtils.formatDateTime(endTime - beginTime),
							request.getRequestURI(), Runtime.getRuntime().maxMemory() / 1024 / 1024, Runtime.getRuntime().totalMemory() / 1024 / 1024, Runtime.getRuntime().freeMemory() / 1024 / 1024,
							(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1024 / 1024});
		}

	}

	private WebDataBinderFactory getDataBinderFactory(HandlerMethod handlerMethod) throws Exception {
		Class<?> handlerType = handlerMethod.getBeanType();
		Set<Method> methods = this.initBinderCache.get(handlerType);
		if (methods == null) {
			methods = MethodIntrospector.selectMethods(handlerType, RequestMappingHandlerAdapter.INIT_BINDER_METHODS);
			this.initBinderCache.put(handlerType, methods);
		}
		List<InvocableHandlerMethod> initBinderMethods = new ArrayList<InvocableHandlerMethod>();
		for (Method method : methods) {
			Object bean = handlerMethod.getBean();
			initBinderMethods.add(new InvocableHandlerMethod(bean, method));
		}
		return new ServletRequestDataBinderFactory(initBinderMethods, adapter.getWebBindingInitializer());
	}

	private HandlerMethodArgumentResolver getArgumentResolver(
			MethodParameter parameter) {
		HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
		if (result == null) {
			for (HandlerMethodArgumentResolver methodArgumentResolver : this.argumentResolvers) {
				if (logger.isTraceEnabled()) {
					logger.trace("Testing if argument resolver [" + methodArgumentResolver + "] supports [" +
							parameter.getGenericParameterType() + "]");
				}
				if (methodArgumentResolver.supportsParameter(parameter)) {
					result = methodArgumentResolver;
					this.argumentResolverCache.put(parameter, result);
					break;
				}
			}
		}
		return result;
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
