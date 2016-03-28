/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */

package com.eryansky.common.mail.event;

import com.eryansky.common.mail.entity.Mail;

import java.util.List;

/**
 * 可订阅类接口，实现订阅接口的规范
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-09-14
 */
@Deprecated
public interface SubscriptionListener {
	/**
	 * 推送电子邮件给订阅者
	 * 
	 * @param mailList 被推送给订阅者的电子邮件列表
	 */
	public void onDeliver(List<Mail> mailList);
}
