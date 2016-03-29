/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.mail.config;

import java.util.Properties;

/**
 * 实现存储基于POP3协议的发送服务器的配置信息
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
public class POP3ServerConfig extends ServerConfig {
	public POP3ServerConfig() {
		this.encryptionType = EncryptionType.NONE;
		this.protocol = Protocol.POP3;
	}

	@Override
	public Properties makeProperties() {
		Properties properties = super.makeProperties();

		switch (this.encryptionType) {
			case NONE:
				break;
			case TLS:
				properties.put("mail.pop3.starttls.enable", "true");
				break;
			case SSL:
				properties.put("mail.pop3.ssl.enable", "true");
				properties.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				properties.put("mail.pop3.socketFactory.fallback", "false");
//				if (this.port != null) {
//					properties.put("mail.pop3.socketFactory.port", this.port);
//				}
				break;
		}
//		properties.put("mail.mime.base64.ignoreerrors", "true");
		properties.put("mail.mime.address.strict", "false");


		if (this.username != null) {
			properties.put("mail.pop3.user", this.username);
		}

		if (this.address != null) {
			properties.put("mail.pop3.host", this.address);
		}

		if (this.port != null) {
			properties.put("mail.pop3.port", this.port);
		}

		if (this.timeout != null) {
			properties.put("mail.pop3.timeout", this.timeout);
		}

		if (this.connectionTimeout != null) {
			properties.put("mail.pop3.connectiontimeout", this.connectionTimeout);
		}

		return properties;
	}
}
