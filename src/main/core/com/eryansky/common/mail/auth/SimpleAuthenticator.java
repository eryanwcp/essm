/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */

package com.eryansky.common.mail.auth;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 简单的用户名/密码认证方式
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-09-14
 */
public class SimpleAuthenticator extends Authenticator {
	private String user;
	private String password;

	public SimpleAuthenticator(String user, String password) {
		this.user = user;
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.user, this.password);
	}
}
