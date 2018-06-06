/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.mail.receiver;

import com.eryansky.common.mail.auth.SimpleAuthenticator;
import com.eryansky.common.mail.config.ServerConfig;
import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.exception.NotSupportedException;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 抽象类，接收服务器通道实现与邮件接收服务器直接通讯完成收取邮件、删除邮件、标记邮件、搜索邮件等操作
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
public abstract class Receiver {
	public static enum SearchType {
		ALL, UNREAD, READ
	}

	protected Session session;
	protected Store store;
	protected Folder folder;
	protected boolean debug;
	protected String folderName = "INBOX";

	/**
	 * 以只读方式打开与邮件服务器的连接
	 *
	 * @return true 如果成功的话 false 其他情况
	 */
	public boolean open() {
		return open(Folder.READ_ONLY);
	}

	/**
	 * 打开与邮件服务器的连接
	 *
	 * @param mode 打开模式，包括只读、读写
	 * @return true 如果成功的话 false 其他情况
	 */
	public abstract boolean open(int mode);

	/**
	 * 关闭与邮件服务器的连接
	 */
	public abstract void close();

	/**
	 * 从邮件服务器获取邮件，并保持最新状态同步
	 *
	 * @return 获取到的新邮件集合
	 */
	public abstract Message[] fetch();

	/**
	 * 切换文件夹
	 *
	 * @param mode 打开模式，包括只读、读写
	 * @return true 如果成功的话 false 其他情况
	 */
	public abstract boolean switchFolder(int mode);


	/**
	 * 按类型搜索邮件
	 * 
	 * @param type 邮件类型
	 * @return 符合条件的 邮件集合
	 */
	public abstract Message[] search(SearchType type);

	/**
	 * 检查目标消息是否已经被存储到本地
	 *
	 * @param mail 待查找的消息
	 *
	 * @return true 如果消息已经被持久化在消息存储区 false 其他情况
	 */
	public boolean isMailExists(Message mail) {
		return false;
	}

	/**
	 * 检查目标消息是否已经被存储到本地
	 *
	 * @param uid 待查找的消息的唯一标识
	 *
	 * @return true 如果消息已经被持久化在消息存储区 false 其他情况
	 */
	public boolean isMailExists(String uid) {
		return false;
	}


	/**
	 * 工厂方法，创建一个接收服务器连接通道实体
	 *
	 * @param account 用户账户
	 *
	 * @return 接受服务器连接通道实体
	 *
	 * @throws NotSupportedException 不支持所申明的发送协议
	 */
	public static Receiver make(Account account) throws NotSupportedException {
		Receiver receiver = null;
		ServerConfig server = account.getReceiverServer();
		Properties properties = server.makeProperties();

		switch (server.getProtocol()) {
			case POP3:
				receiver = new POP3Receiver();
				break;
			case IMAP:
				receiver = new IMAPReceiver();
				break;
			default:
				throw new NotSupportedException();
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
		receiver.setSession(session);

		return receiver;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
		this.session.setDebug(this.debug);
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public Store getStore() {
		return store;
	}

	public Folder getFolder() {
		return folder;
	}

	public static String decodeText(String text) throws UnsupportedEncodingException {
		if (text == null) return null;
		if (text.startsWith("=?GB") || text.startsWith("=?gb")) text = MimeUtility.decodeText(text);
		else text = new String(text.getBytes("ISO8859_1"));
		return text;
	}

	/**
	 * 获取邮件ID
	 * @param message
	 * @return
	 * @throws MessagingException
	 */
	public abstract String getUID(MimeMessage message) throws MessagingException;

	/**
	 * 获得邮件主题
	 * @param msg 邮件内容
	 * @return 解码后的邮件主题
	 */
	public static String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
		return MimeUtility.decodeText(msg.getSubject());
	}



}