/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Command
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2018-11-11
 */
public class CommandUtils {

	public static String execute(String command) throws IOException {
		return execute(command, "GBK");
	}
	
	public static String execute(String command, String charsetName) throws IOException {
		Process process = Runtime.getRuntime().exec(command);
		// 记录dos命令的返回信息
		StringBuffer stringBuffer = new StringBuffer();
		// 获取返回信息的流
		InputStream in = process.getInputStream();
		Reader reader = new InputStreamReader(in, charsetName);
		BufferedReader bReader = new BufferedReader(reader);
		String res = bReader.readLine();
		while (res != null) {
			stringBuffer.append(res);
			stringBuffer.append("\n");
			res = bReader.readLine();
		}
		bReader.close();
		reader.close();
		return stringBuffer.toString();
	}
	
}
