/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.eryansky.common.mail.entity;

import javax.mail.Message;

/**
 * 邮件实体类，存储一封邮件的完整信息，包括主题、内容、附件、收件人、发件人
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
@Deprecated
public class Mail {
	/**
	 * 邮件消息内容实体
	 */
	private Message message;

	/**
	 * 邮件的唯一标识
	 */
	private String uid;
	
	public Mail() {
		
	}
	
	public Mail(String uid, Message message) {
		this.uid = uid;
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
