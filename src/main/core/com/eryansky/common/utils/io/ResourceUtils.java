/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.io;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 国际化资源工具类.
 * @author 温春平&wencp wencp@strongit.com.cn
 * @date   2012-12-15 上午8:43:52
 */
public class ResourceUtils {

	/**
	 * 返回 {res}.properties 中 key 对应的值
	 * 
	 * @param baseName
	 * @param key
	 * @return
	 */
	public static String getString(String baseName, String key) {
		return _getStringForLocale(Locale.getDefault(), baseName, key);
	}

	/**
	 * 返回 {res}.properties 中 key 对应的值
	 * 
	 * @param locale
	 * @param baseName
	 * @param key
	 * @return
	 */
	private static String _getStringForLocale(Locale locale, String baseName,
			String key) {
		try {
			ResourceBundle rb = ResourceBundle.getBundle(baseName, locale,
					ResourceUtils.class.getClassLoader());
			return (rb != null) ? rb.getString(key) : null;
		} catch (MissingResourceException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 返回 {res}.properties 中 key 对应的值，并对值进行参数格式化
	 * 
	 * @param baseName
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getString(String baseName, String key, Object... args) {
		String text = getString(baseName, key);
		return (text != null) ? MessageFormat.format(text, args) : null;
	}

	/**
	 * 返回 {res}.properties 中 key 对应的值，并对值进行参数格式化
	 * 
	 * @param locale
	 * @param baseName
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getStringForLocale(Locale locale, String baseName,
			String key, Object... args) {
		String text = _getStringForLocale(locale, baseName, key);
		return (text != null) ? MessageFormat.format(text, args) : null;
	}

	/**
	 * 加载{res}.properties资源
	 * @param resource 资源路径
	 * @return 资源内容
	 */
	public static String loadFromResource(String resource) {
		InputStream in = null;
		BufferedReader reader = null;
		try {
			in = new FileInputStream(resource);
			reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			return IOUtils.toString(reader);
		} catch (Exception excp) {
			throw new RuntimeException(excp);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(in);
			reader = null;
		}
	}

}
