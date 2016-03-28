/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.web.filter.cache;

import com.eryansky.common.web.filter.BaseFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;

/**
 * 缓存过滤器.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-30 下午9:33:44
 * 
 */
public class CacheFilter extends BaseFilter {
	ServletContext sc;
	long cacheTimeout = Long.MAX_VALUE;

	@Override
	public void init() throws ServletException {
		super.init();
		String ct = config.getInitParameter("cacheTimeout");
		if (ct != null) {
			cacheTimeout = 60 * 1000 * Long.parseLong(ct);
		}
		this.sc = config.getServletContext();
	}

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		// check if was a resource that shouldn't be cached.
		String r = sc.getRealPath("");
		String path = config.getInitParameter(request.getRequestURI());
		if (path != null && path.equals("nocache")) {
			chain.doFilter(request, response);
			return;
		}
		path = r + path;

		// customize to match parameters
		String id = request.getRequestURI() + request.getQueryString();
		// optionally append i18n sensitivity
		String localeSensitive = config.getInitParameter("locale-sensitive");
		if (localeSensitive != null) {
			StringWriter ldata = new StringWriter();
			Enumeration locales = request.getLocales();
			while (locales.hasMoreElements()) {
				Locale locale = (Locale) locales.nextElement();
				ldata.write(locale.getISO3Language());
			}
			id = id + ldata.toString();
		}
		File tempDir = (File) sc.getAttribute("javax.servlet.context.tempdir");

		// get possible cache
		String temp = tempDir.getAbsolutePath();
		File file = new File(temp + id);

		// get current resource
		if (path == null) {
			path = sc.getRealPath(request.getRequestURI());
		}
		File current = new File(path);

		try {
			long now = Calendar.getInstance().getTimeInMillis();
			// set timestamp check
			if (!file.exists()
					|| (file.exists() && current.lastModified() > file
					.lastModified())
					|| cacheTimeout < now - file.lastModified()) {
				String name = file.getAbsolutePath();
				name = name
						.substring(
								0,
								name.lastIndexOf("/") == -1 ? 0 : name
										.lastIndexOf("/"));
				new File(name).mkdirs();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				CacheResponseWrapper wrappedResponse = new CacheResponseWrapper(
						response, baos);
				chain.doFilter(request, wrappedResponse);

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baos.toByteArray());
				fos.flush();
				fos.close();
			}
		} catch (ServletException e) {
			if (!file.exists()) {
				throw new ServletException(e);
			}
		} catch (IOException e) {
			if (!file.exists()) {
				throw e;
			}
		}

		FileInputStream fis = new FileInputStream(file);
		String mt = sc.getMimeType(request.getRequestURI());
		response.setContentType(mt);
		ServletOutputStream sos = response.getOutputStream();
		for (int i = fis.read(); i != -1; i = fis.read()) {
			sos.write((byte) i);
		}
	}


	public void destroy() {
		this.sc = null;
	}
}
