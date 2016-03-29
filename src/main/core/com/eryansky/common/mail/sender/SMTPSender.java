/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.eryansky.common.mail.sender;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.SendFailedException;

/**
 * 实现使用SMTP协议发送邮件
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
public class SMTPSender extends Sender {
	private static final String SMTP = "smtp";
	private SMTPTransport transport = null;

	public SMTPSender() {

	}

	@Override
	public boolean open() {
		boolean flag = true;

		try {
			// 创建transport实例
			this.transport = (SMTPTransport) this.session.getTransport(SMTP);
			this.transport.connect();
		} catch (Exception e) {
			flag = false;
			this.close();
		}

		return flag;
	}

	@Override
	public void close() {
		if (this.transport != null) {
			try {
				this.transport.close();
			} catch (Exception e) {
				// 忽略异常
			} finally {
				transport = null;
			}
		}
	}


	@Override
	public boolean send(Message message) throws SendFailedException {
		boolean flag = true;

		try {
			this.transport.sendMessage(message, message.getAllRecipients());
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}
}
