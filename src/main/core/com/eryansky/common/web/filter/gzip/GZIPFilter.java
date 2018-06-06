/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.filter.gzip;

import com.eryansky.common.web.filter.BaseFilter;
import com.eryansky.common.web.utils.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GZIP拦截器.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-30 下午9:32:59
 * 
 */
public class GZIPFilter extends BaseFilter{


	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		WebUtils.checkAccetptGzip(request);
		if (WebUtils.checkAccetptGzip(request)) {
			GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(
					response);
			chain.doFilter(request, wrappedResponse);
			wrappedResponse.finishResponse();
			return;
		}
		chain.doFilter(request, response);
	}


	public void destroy() {
		// noop
	}
}
