/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
/**
 * 数据流工具类
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2011-12-30下午2:30:28
 */
public class StreamTool {

	/**
	 * 从输入流读取数据
	 * @param inStream 输入流
	 * @return 字节数组
	 * @throws Exception    
	 * @date：    2011-12-27 上午9:35:28
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception{
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = inStream.read(buffer)) !=-1 ){
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}
}
