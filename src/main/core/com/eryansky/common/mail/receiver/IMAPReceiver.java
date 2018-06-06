/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.eryansky.common.mail.receiver;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

/**
 * 实现使用IMAP协议接收邮件
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
public class IMAPReceiver extends Receiver {
	private IMAPStore store;
	private IMAPFolder folder;

	private static final String IMAP = "imap";

	/* (non-Javadoc)
	 * @see org.gfg.mail.receiver.Receiver#open()
	 */
	@Override
	public boolean open(int mode) {
		boolean flag;
		
		try {
			// 创建IMAP的邮箱实体
			this.store = (IMAPStore)this.session.getStore(IMAP);

			// 连接邮件服务器
			this.store.connect();

			// 打开文件夹对象
			flag = this.switchFolder(mode);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
			this.close();
		}

		return flag;
	}

	/* (non-Javadoc)
	 * @see org.gfg.mail.receiver.Receiver#close()
	 */
	@Override
	public void close() {
		if (this.folder != null) {
			try {
				this.folder.close(true);
			} catch (Exception ignored) {
			}
		}
		if (this.store != null) {
			try {
				this.store.close();
			} catch (Exception ignored) {
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.gfg.mail.receiver.Receiver#fetch()
	 */
	@Override
	public Message[] fetch() {
		Message[] result = null;

		try {
			// 初始化消息列表
			result = folder.getMessages();
			FetchProfile profile = new FetchProfile();
			profile.add(UIDFolder.FetchProfileItem.UID);

			// 从服务器上获取新的消息
			folder.fetch(result, profile);
		} catch (Exception e) {
			result = null;
		}

		return result;
	}

	/* (non-Javadoc)
		 * @see org.gfg.mail.receiver.Receiver#switchFolder()
		 */
	@Override
	public boolean switchFolder(int mode) {
		boolean flag = true;

		try {
			this.folder = (IMAPFolder)this.store.getFolder(this.folderName);
			this.folder.open(mode);
		} catch (MessagingException e) {
			flag = false;
		}

		return flag;
	}

	@Override
	public Message[] search(SearchType type) {
		Message[] result = null;

		Flags seen = new Flags(Flag.SEEN);
		FlagTerm flagTerm = new FlagTerm(seen, false);
		
		try {
			result = folder.search(flagTerm);
		} catch (MessagingException ignored) {
		}
		
		return result;
	}

	@Override
	public IMAPStore getStore() {
		return store;
	}

	@Override
	public IMAPFolder getFolder() {
		return folder;
	}

	@Override
	public String getUID(MimeMessage message) throws MessagingException {
		return Long.toString(folder.getUID(message));
	}
}