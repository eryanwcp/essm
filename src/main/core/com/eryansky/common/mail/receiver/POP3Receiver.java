/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */

package com.eryansky.common.mail.receiver;

import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

/**
 * 实现使用POP3协议接收邮件
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-09-14
 */
public class POP3Receiver extends Receiver {
	private POP3Store store;

	private POP3Folder folder;

	private static final String POP3 = "pop3";

	/* (non-Javadoc)
		 * @see org.gfg.mail.receiver.Receiver#open()
		 */
	@Override
	public boolean open(int mode) {
		boolean flag;
		try {
			// 创建POP3的邮箱实体
			this.store = (POP3Store) this.session.getStore(POP3);
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
			} catch (Exception ignored) {}
		}
		if (this.store != null) {
			try {
				this.store.close();
			} catch (Exception ignored) {}
		}
	}

	/* (non-Javadoc)
	 * @see org.gfg.mail.receiver.Receiver#fetch()
	 */
	@Override
	public Message[] fetch() {
		Message[] result;

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
			this.folder = (POP3Folder) this.store.getFolder(this.folderName);
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
	public POP3Store getStore() {
		return store;
	}


	@Override
	public POP3Folder getFolder() {
		return folder;
	}

	@Override
	public String getUID(MimeMessage message) throws MessagingException {
		return folder.getUID(message);
	}
}
