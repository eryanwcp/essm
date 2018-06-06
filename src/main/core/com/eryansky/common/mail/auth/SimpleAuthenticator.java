/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.eryansky.common.mail.auth;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 简单的用户名/密码认证方式
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
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
