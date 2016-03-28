/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.filter.cache;

import com.eryansky.common.web.filter.BaseFilter;
import com.eryansky.common.web.utils.WebUtils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 为Response设置客户端缓存控制Header的Filter.
 * 
 * eg.在web.xml中设置
 * 	<filter>
 * 		<filter-name>cacheControlHeaderFilter</filter-name>
 * 		<filter-class>com.eryansky.common.web.filter.CacheControlHeaderFilter</filter-class>
 * 		<init-param>
 * 			<param-name>expiresSeconds</param-name>
 * 			<param-value>31536000</param-value>
 * 		</init-param>
 * 	</filter>
 * 	<filter-mapping>
 * 		<filter-name>cacheControlHeaderFilter</filter-name>
 * 		<url-pattern>/img/*</url-pattern>
 * 	</filter-mapping>
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-31 下午9:09:35 
 *
 */
public class CacheControlHeaderFilter extends BaseFilter {

	private static final String PARAM_EXPIRES_SECONDS = "expiresSeconds";
	private long expiresSeconds;

	@Override
	public void init() throws ServletException {
		super.init();
		String expiresSecondsParam = config.getInitParameter(PARAM_EXPIRES_SECONDS);
		if (expiresSecondsParam != null) {
			expiresSeconds = Long.valueOf(expiresSecondsParam);
		} else {
			expiresSeconds = WebUtils.ONE_YEAR_SECONDS;
		}
	}

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		super.doFilter(request, response, chain);
		WebUtils.setExpiresHeader(response, expiresSeconds);
		chain.doFilter(request, response);
	}


	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}
}
