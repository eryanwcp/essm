package com.eryansky.common.web.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义http body信息保存
 * <br>将该Filter配置在web.xml 在SpringMVC DispatcherServlet之上
 */
public class CustomHttpServletRequestFilter extends BaseFilter{


    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new CustomHttpServletRequestWrapper(request), response);
    }

}