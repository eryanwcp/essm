/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.eryansky.common.mail.sender;

import com.eryansky.common.mail.auth.SimpleAuthenticator;
import com.eryansky.common.mail.config.ServerConfig;
import com.eryansky.common.mail.entity.Account;

import javax.mail.Message;
import javax.mail.SendFailedException;
import javax.mail.Session;
import java.util.Properties;

/**
 * 抽象类，发送服务器通道实现与邮件发送服务器直接通讯完成发送邮件操作
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
public abstract class Sender {
	protected Session session;
	protected boolean debug;

	/**
	 * 打开与邮件服务器的连接
	 *
	 * @return true 如果成功的话 false 其他情况
	 */
	public abstract boolean open();

	/**
	 * 关闭与邮件服务器的连接
	 */
	public abstract void close();

	/**
	 * 发送邮件
	 *
	 * @param message 邮件正文
	 *
	 * @return <code>true</code>, 发送成功，否则返回<code>false</code>
	 *
	 * @throws SendFailedException
	 */
	public abstract boolean send(Message message) throws SendFailedException;

	/**
	 * 工厂方法，创建一个接收服务器连接通道实体
	 *
	 * @param account
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	public static Sender make(Account account) throws Exception {
		Sender sender = null;
		ServerConfig server = account.getSenderServer();
		Properties properties = server.makeProperties();

		switch (server.getProtocol()) {
			case SMTP:
				sender = new SMTPSender();
				break;
			default:
				throw new Exception("不支持的协议");
		}

		// 创建认证信息
		// TODO 扩展Authenticator，以实现更加复杂的认证方式
		SimpleAuthenticator authenticator = null;

		if (server.getUsername() != null) {
			authenticator = new SimpleAuthenticator(server.getUsername(), server.getPassword());
		} else {
			authenticator = new SimpleAuthenticator(account.getUsername(), account.getPassword());
		}

		Session session = Session.getInstance(properties, authenticator);
		sender.setSession(session);

		return sender;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
		this.session.setDebug(debug);
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
