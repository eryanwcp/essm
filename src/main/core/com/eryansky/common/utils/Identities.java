/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils;

import com.eryansky.common.utils.id.SnowFlake;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * 封装各种生成唯一性ID算法的工具类.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class Identities {

	private static SecureRandom random = new SecureRandom();
	private static SnowFlake snowFlake = SnowFlake.getInstance();

	private Identities() {
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成,中间有-分割
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成,中间无-分割
	 */
	public static String uuid2() {
		return uuid().replaceAll("-", "");
	}

	/**
	 * 基于雪花算法
	 * @return
	 */
	public static Long uuid3() {
		return snowFlake.nextId();
	}

	/**
	 * 基于雪花算法
	 * @return
	 */
	public static String uuid4() {
		return String.valueOf(uuid3());
	}

	/**
	 * 使用SecureRandom随机生成Long. 
	 */
	public static long randomLong() {
		return random.nextLong();
	}

	/**
	 * 基于Base62编码的随机生成Long.
	 */
	/*public static String randomBase62() {
		return Encodes.encodeBase62(random.nextLong());
	}*/
	
	public static void main(String[] args) {
		for(int i=0;i<1000;i++){
			System.out.println(randomLong());
		}
	}
}
