/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

/**
 * Clob工具类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2012-12-11 上午10:32:35
 */
public class ClobUtil {

	/**
	 * 获得字符串
	 * 
	 * @param c
	 *            java.sql.Clob
	 * @return 字符串
	 */
	public static String getString(Clob c) {
		StringBuffer s = new StringBuffer();
		if (c != null) {
			try {
				BufferedReader bufferRead = new BufferedReader(c.getCharacterStream());
				try {
					String str;
					while ((str = bufferRead.readLine()) != null) {
						s.append(str);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return s.toString();
	}

	/**
	 * 获得Clob
	 * 
	 * @param s
	 *            字符串
	 * @return java.sql.Clob
	 */
	public static Clob getClob(String s) {
		Clob c = null;
		try {
			if (s != null) {
				c = new SerialClob(s.toCharArray());
			}
		} catch (SerialException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}

}
