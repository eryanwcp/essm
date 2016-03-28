/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */

package com.eryansky.common.mail.sender;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.SendFailedException;

/**
 * 实现使用SMTP协议发送邮件
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
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
