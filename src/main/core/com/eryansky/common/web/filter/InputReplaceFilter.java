/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 特殊字符输入过滤拦截器.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-2-22 下午1:51:52
 */
public class InputReplaceFilter extends BaseFilter {

	private Properties pp = new Properties();

	// 非法词、敏感词、特殊字符、配置在初始化参数中
	private void initConfig() {
		// 配置文件位置
		String file = config.getInitParameter("file");
		// 文件实际位置
		String realPath = config.getServletContext().getRealPath(file);
		try {
			// 加载非法词
			pp.load(new FileInputStream(realPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 过滤编码
		if (request.getMethod().equalsIgnoreCase("post")) {
			request.setCharacterEncoding("utf-8");
		} else {
			Iterator its = request.getParameterMap().values().iterator();
			while (its.hasNext()) {
				String[] params = (String[]) its.next();
				int len = params.length;
				for (int i = 0; i < len; i++) {
					params[i] = new String(params[i].getBytes("utf-8"), "utf-8");
				}
			}
		}
		// 过滤客户端提交表单中特殊字符
		Iterator its = request.getParameterMap().values().iterator();
		while (its.hasNext()) {
			String[] params = (String[]) its.next();
			for (int i = 0; i < params.length; i++) {
				//特殊字符Html转译
//				params[i] = params[i].replaceAll(params[i], EncodeUtils.htmlEscape(params[i]));
				for (Object oj : pp.keySet()) {
					String key = (String) oj;
					params[i] = params[i].replace(key, pp.getProperty(key));
				}
			}
		}

		chain.doFilter(request, response);
	}

}