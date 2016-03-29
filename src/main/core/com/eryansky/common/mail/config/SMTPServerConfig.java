/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.eryansky.common.mail.config;

import java.util.Properties;

/**
 * 实现存储基于SMTP协议的发送服务器的配置信息
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
public class SMTPServerConfig extends ServerConfig {
	public SMTPServerConfig() {
		this.protocol = Protocol.SMTP;
		this.encryptionType = EncryptionType.NONE;
	}

	@Override
	public Properties makeProperties() {
		Properties properties = super.makeProperties();

		switch (this.encryptionType) {
			case NONE:
				break;
			case TLS:
				properties.put("mail.smtp.starttls.enable", "true");
				break;
			case SSL:
				properties.put("mail.smtp.ssl.enable", "true");
				properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				properties.put("mail.smtp.socketFactory.fallback", "false");
				break;
		}

		if (this.address != null) {
			properties.put("mail.smtp.host", this.address);
		}

		if (this.port != null) {
			properties.put("mail.smtp.port", this.port);
		}

		if (this.isNeedAuth != null) {
			properties.put("mail.smtp.auth", this.isNeedAuth.toString());
		}

		if (this.timeout != null) {
			properties.put("mail.smtp.timeout", this.timeout);
		}

		if (this.connectionTimeout != null) {
			properties.put("mail.smtp.connectiontimeout", this.connectionTimeout);
		}

		return properties;
	}
}
