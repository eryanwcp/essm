/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */

package com.eryansky.common.mail.config;

import java.util.Properties;

/**
 * 实现存储基于IMAP协议的发送服务器的配置信息
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-09-14
 */
public class IMAPServerConfig extends ServerConfig {
	public IMAPServerConfig() {
		this.encryptionType = EncryptionType.NONE;
		this.protocol = Protocol.IMAP;
	}

	@Override
	public Properties makeProperties() {
		Properties properties = super.makeProperties();

		switch (this.encryptionType) {
			case NONE:
				break;
			case TLS:
				properties.put("mail.imap.starttls.enable", "true");
				break;
			case SSL:
				properties.put("mail.imap.ssl.enable", "true");
				properties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				properties.put("mail.imap.socketFactory.fallback", "false");
//				properties.put("mail.imap.auth.login.disable", "true");
//				properties.put("mail.imap.auth.plain.disable", "true");
				break;
		}

		if (this.username != null) {
			properties.put("mail.imap.user", this.username);
		}

		if (this.address != null) {
			properties.put("mail.imap.host", this.address);
		}

		if (this.port != null) {
			properties.put("mail.imap.port", this.port);
		}

		if (this.timeout != null) {
			properties.put("mail.imap.timeout", this.timeout);
		}

		if (this.connectionTimeout != null) {
			properties.put("mail.imap.connectiontimeout", this.connectionTimeout);
		}

		return properties;
	}
}
