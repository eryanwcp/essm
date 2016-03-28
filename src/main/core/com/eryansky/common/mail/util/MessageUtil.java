/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */

package com.eryansky.common.mail.util;

import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.entity.TipMessage;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 关于邮件消息的常用方法封装
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-09-14
 */
public class MessageUtil {
	/**
	 * email地址的正则表达式，包含边界匹配
	 */
	public static final String REGEX_MAIL_ADDRESS = "\\b([\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+)\\b";

	/**
	 * 工厂方法，创建符合<tt>TipWindow</tt>内使用的消息实体
	 * 
	 * @param account 账户
	 * @param message 原邮件消息
	 * @return 符合<tt>TipWindow</tt>内使用的消息实体
	 */
	public static TipMessage makeTipMessage(Account account, Message message) {
		TipMessage result = null;

		if (message != null) {
			try {
				result = new TipMessage();
				result.setTitleURL(account.getWebMailServerUrl());

				result.setTitle(message.getSubject());

				Address[] froms = message.getFrom();
				if (froms != null && froms.length > 0) {
					Pattern pattern = Pattern.compile(REGEX_MAIL_ADDRESS);
					Matcher matcher = pattern.matcher(froms[0].toString());
					if (matcher.find()) {
						result.setSmallTitle("来自 " + matcher.group(0));
					} else {
						result.setSmallTitle("来自匿名邮件");
					}
				} else {
					result.setSmallTitle("来自匿名邮件");
				}
			} catch (MessagingException e) {

			}

			MimeMultipart parts;

			try {
				// 判断是邮件内容实体是否是复合式
				Object content = message.getContent();
				System.out.println(content);
				if (content instanceof MimeMultipart) {
					parts = (MimeMultipart) message.getContent();
					result.setContent(parseMailContent(parts));
				} else {
					result.setContent(message.getContent().toString());
				}
			} catch (Exception e) {
				result.setContent("");
			}
		}

		return result;
	}

	/**
	 * 读取复合消息中的文本信息（包括文本和HTML两种类型）
	 * 
	 * @param parts 复合消息实体
	 * @return 复合消息中的文本信息
	 */
	public static String parseMailContent(MimeMultipart parts) {
		try {
			for (int i = 0; i < parts.getCount(); ++i) {
				MimeBodyPart part = (MimeBodyPart) parts.getBodyPart(i);
				String disposition = part.getDisposition();
				
				System.out.println(part.getContent());

				if (!MimeBodyPart.ATTACHMENT.equals(disposition) && part instanceof MimeBodyPart) {
					if (part.getContent() instanceof MimeMultipart) {
						return parseMailContent((MimeMultipart) part
								.getContent());
					} else {
						// 支持两种格式，忽略其他格式
						if (part.isMimeType("text/plain")
								|| part.isMimeType("text/html")) {
							return part.getContent().toString();
						}
					}
				}
			}
		} catch (Exception e) {

		}

		return null;
	}
}
