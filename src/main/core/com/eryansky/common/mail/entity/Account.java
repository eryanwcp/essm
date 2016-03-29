/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.mail.entity;

import com.eryansky.common.mail.config.ServerConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 维护存储用户账户信息，这些信息包括用户名、密码、电子邮件地址、接收服务器配置、发送服务器配置等
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
public class Account {
	private final static String PATTERN_MAIL = "^([\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+)$";

	private String username;
	private String password;
	
	private String mailAddress;
	private String webMailServerUrl;
	
	private String passwordState;
	private String sessionId;

	private ServerConfig senderServer;
	private ServerConfig receiverServer;

	public ServerConfig getSenderServer() {
		return senderServer;
	}

	public void setSenderServer(ServerConfig senderServer) {
		this.senderServer = senderServer;
	}

	public ServerConfig getReceiverServer() {
		return receiverServer;
	}

	public void setReceiverServer(ServerConfig receiverServer) {
		this.receiverServer = receiverServer;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		Pattern pattern = Pattern.compile(PATTERN_MAIL); // 校验字符串是否符合电子邮件规格
		Matcher matcher = pattern.matcher(mailAddress);
		if (matcher.find()) {
			this.mailAddress = mailAddress.toLowerCase();
		} else {
			throw new IllegalArgumentException("非法电子邮件地址");
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordState() {
		return passwordState;
	}

	public void setPasswordState(String passwordState) {
		this.passwordState = passwordState;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getWebMailServerUrl() {
		return webMailServerUrl;
	}

	public void setWebMailServerUrl(String webMailServerUrl) {
		this.webMailServerUrl = webMailServerUrl;
	}
}
