/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 为Response设置Expires等Header的Filter.
 * <p/>
 * eg.在web.xml中设置
 * <filter>
 * <filter-name>expiresHeaderFilter</filter-name>
 * <filter-class>com.eryansky.common.web.ResponseHeaderFilter</filter-class>
 * <init-param>
 * <param-name>Cache-Control</param-name>
 * <param-value>public, max-age=31536000</param-value>
 * </init-param>
 * </filter>
 * <filter-mapping>
 * <filter-name>expiresHeaderFilter</filter-name>
 * <url-pattern>/img/*</url-pattern>
 * </filter-mapping>
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class ResponseHeaderFilter extends BaseFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // set the provided HTTP response parameters
        for (Enumeration e = config.getInitParameterNames(); e.hasMoreElements(); ) {
            String headerName = (String) e.nextElement();
            response.addHeader(headerName, config.getInitParameter(headerName));
        }
        // pass the request/response on
        chain.doFilter(request, response);
    }


    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }
}
