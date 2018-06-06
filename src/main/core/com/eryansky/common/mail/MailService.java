/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.eryansky.common.mail;

import com.eryansky.common.mail.config.SubscriptionConfig;
import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.entity.Condition;
import com.eryansky.common.mail.entity.Mail;
import com.eryansky.common.mail.event.SubscriptionListener;
import com.eryansky.common.mail.receiver.Receiver;
import com.eryansky.common.mail.sender.Sender;

import javax.mail.Message;
import javax.mail.SendFailedException;
import java.util.*;

/**
 * 实现可订阅提醒的邮件服务器的综合应用
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-14
 */
@Deprecated
public class MailService implements Runnable {
	public static final int DEFAUL_INTERVAL = 1000; // 默认每间隔一秒检测一次是否存在新邮件
	/**
	 * 接收服务器
	 */
	private Receiver receiver;

	/**
	 * 发送服务器
	 */
	private Sender sender;

	/**
	 * 账户信息
	 */
	private Account account;

	/**
	 * 是否启用订阅
	 */
	private boolean enabled = false;

	/**
	 * 订阅配置信息，未实现
	 * TODO 实现订阅配置
	 */
	private SubscriptionConfig config;

	/**
	 * 订阅者列表
	 */
	private List<SubscriptionListener> listeners = new ArrayList<SubscriptionListener>();

	/**
	 * 订阅监听线程
	 * TODO 后期可以考虑合并到订阅配置<tt>SubscriptionConfig</tt>中
	 */
	private Thread monitor;

	/**
	 * 监听分发订阅的频率，单位为毫秒
	 */
	private int interval = DEFAUL_INTERVAL;

	/**
	 * 当前本地的邮件数据，主键为邮件UID，值为<tt>Mail</tt>电子邮件对象
	 * TODO 后期可更改为数据库DAO获取，并做缓存优化，建议使用SQLITE
	 */
	private Map<String, Mail> data = new HashMap<String, Mail>();

	public MailService() {
	}

	/**
	 * 初始化与邮件服务器的会话
	 *
	 * @throws Exception
	 */
	public void initialize() {
	}

	/**
	 * 发送单封邮件
	 *
	 * @param mail
	 *            邮件内容实体类
	 *
	 * @return true 发送成功 false 发送失败
	 */
	public boolean send(Mail mail) throws SendFailedException {
		return sender.send(mail.getMessage());
	}

	/**
	 * 从邮件服务器获取邮件，并保持最新状态同步
	 *
	 * @return 获取到的新邮件集合
	 */
	public List<Mail> fetch() {
		Vector<Mail> newList = null;

		if (receiver == null) {
			return null;
		}
		receiver.open();
		Mail[] list = null;
		list = new Mail[10];
		receiver.close();

		if (list != null) {
			newList = new Vector<Mail>();

			for (Mail item : list) {
				// 注意： 如果item为null，或者item中的uid为null将会引起异常
				if (!data.containsKey(item.getUid())) {
					data.put(item.getUid(), item);
					newList.add(item);
				}
			}
		}

		return newList;
	}

	/**
	 * 从本地查找符合条件的邮件
	 *
	 * @param condition
	 *            查找条件
	 *
	 * @return 符合条件的邮件
	 */
	public Message[] find(Condition condition) {
		// TODO 未实现
		return null;
	}

	@Override
	public void run() {
		while (true) {
			if (this.enabled) {
				List<Mail> list = this.fetch();

				if (list != null) {
					for (int i = 0; i < listeners.size(); ++i) {
						// 避免一个订阅者发送失败而影响其他订阅者
						try {
							listeners.get(i).onDeliver(list);
						} catch (Exception e) {

						}
					}
				}

				try {
					Thread.sleep(interval); // 休息指定时钟数
				} catch (InterruptedException e) {

				}
			} else {
				break; // 如果通知被关闭，即进入休眠状态
			}
		}
	}

	/**
	 * 工具方法，判断给定的<tt>list</tt>中是否包含目前存储空间中没有的新邮件
	 * 如果存在这样的邮件，那么将这些邮件存入一个集合返回
	 *
	 * @param list 待判断的电子邮件集合
	 * @return <code>null</code>，如果不存在新邮件，否则返回新邮件集合
	 */
	public Vector<Mail> hasNewMail(Mail[] list) {
		Vector<Mail> newList = null;

		if (list != null) {
			newList = new Vector<Mail>();

			for (Mail item : list) {
				// 注意： 如果item为null，或者item中的uid为null将会引起异常
				if (!data.containsKey(item.getUid())) {
					newList.add(item);
				}
			}
		}

		return newList;
	}

	/**
	 * 工具方法，判断给定的<tt>list</tt>中是否包含目前存储空间中没有的新邮件
	 * 如果存在这样的邮件，返回<code>true</code>，如果不存在则返回<code>false</code>
	 *
	 * @param list 待判断的电子邮件集合
	 * @return <code>true</code>，如果存在新邮件，否则返回<code>false</code>
	 */
	public boolean isExistsNewMail(Mail[] list) {
		boolean flag = false;

		if (list != null) {
			for (Mail item : list) {
				// 注意： 如果item为null，或者item中的uid为null将会引起异常
				if (!data.containsKey(item.getUid())) {
					flag = true;
					break;
				}
			}
		}

		return flag;
	}

	/**
	 * 订阅新邮件通知
	 *
	 * @param listener
	 *            订阅者，即被通知对象
	 *
	 * @return true 订阅成功 false 其他情况
	 */
	public boolean addSubscriptionListener(SubscriptionListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
			return true;
		}
		return false;
	}

	/**
	 * 取消订阅新邮件通知
	 *
	 * @param listener
	 *            订阅者，即被通知对象
	 */
	public void removeSubscriptionListener(SubscriptionListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * 开启或关闭订阅通知功能，默认开启
	 *
	 * @param enabled
	 *            true 开启 false 关闭
	 */
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;

			try{
				if (this.enabled) {
					if (monitor == null) {
						monitor = new Thread(this);

					}
					monitor.start();
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 设置新邮件通知服务属性
	 *
	 * @param config
	 *            配置属性
	 */
	public void setConfig(SubscriptionConfig config) {
		this.config = config;
	}

	/**
	 * 获取当前新邮件通知服务配置属性
	 *
	 * @return 当前配置属性对象
	 */
	public SubscriptionConfig getConfig() {
		return this.config;
	}

	/**
	 * 检查新邮件通知服务是否开启
	 *
	 * @return true 开启 false 关闭
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * 设置账户信息，注意正确配置发送服务器和接收服务器，否则会引起服务异常
	 *
	 * @param account 账户信息
	 */
	public void setAccount(Account account) {
		this.account = account;

		// 设置接收服务器
		Receiver receiver = null;
		try {
			receiver = Receiver.make(account);
		} catch (Exception e) {

		}
		this.receiver = receiver;

		// 设置发送服务器
		Sender sender = null;
		try {
			sender = Sender.make(account);
		} catch (Exception e) {

		}
		this.sender = sender;
	}

	/**
	 * 获取配置的账户信息
	 * @return 账户信息
	 */
	public Account getAccount() {
		return this.account;
	}

	/**
	 * 获取监听频率，单位为毫秒
	 *
	 * @return 频率
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * 设置监听频率，单位为毫秒
	 *
	 * @param interval 频率
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}
}