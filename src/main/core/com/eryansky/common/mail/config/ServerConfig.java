/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.eryansky.common.mail.config;

import java.util.Properties;

/**
 * 存储用户对服务器的配置信息，包括接收服务器、发送服务器等
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
public class ServerConfig implements Configuration {
	public enum Protocol {
		SMTP, POP3, IMAP
	}

	public static enum EncryptionType {
		NONE, TLS, SSL
	}

	public static enum ContentType {
		TEXT, HTML
	}

	protected static final String CONTENT_TYPE_TEXT = "text/plan";
	protected static final String CONTENT_TYPE_HTML = "text/html";


	protected Protocol protocol;
	
	protected Boolean isNeedAuth;

	protected String charset = "utf-8";
	protected ContentType contentType;
	
	protected String address;
	protected Integer port;
	
	protected String username;
	protected String password;
	protected EncryptionType encryptionType;
	
	protected Integer timeout;
	protected Integer connectionTimeout;

	public Boolean isNeedAuth() {
		return isNeedAuth;
	}

	public void setNeedAuth(Boolean needAuth) {
		this.isNeedAuth = needAuth;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public Properties makeProperties() {
		return new Properties();
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public EncryptionType getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(EncryptionType encryptionType) {
		this.encryptionType = encryptionType;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	
	public String getContentTypeString() {
		switch (this.contentType) {
			case HTML:
				return CONTENT_TYPE_HTML;
			default:
				return CONTENT_TYPE_TEXT;
		}
	}
}
