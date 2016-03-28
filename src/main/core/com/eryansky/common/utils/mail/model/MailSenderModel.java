/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.mail.model;

import com.eryansky.common.utils.io.FileUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * 发送邮件需要使用的基本信息entity.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-5-5 下午3:39:55
 */
public class MailSenderModel {
	/**
	 * 发送邮件的服务器的IP或域名
	 */
	private String mailServerHost;
	/**
	 * 发送邮件的服务器的端口 (默认：25)
	 */
	private String mailServerPort = "25";
	/**
	 * 邮件发送者的地址
	 */
	private String fromAddress;
	/**
	 * 邮件接收者的地址 （地址以","分割）
	 */
	private String toAddress;
	/**
	 * 抄送地址（地址以","分割）
	 */
	private String ccAddress;
	/**
	 * 暗送地址 （地址以","分割）
	 */
	private String bccAddress;
	/**
	 * 登陆邮件发送服务器的用户名
	 */
	private String userName;
	/**
	 * 登陆邮件发送服务器的密码
	 */
	private String password;
	/**
	 * 是否需要身份验证 (默认：false)
	 */
	private boolean validate = false;
	/**
	 * 邮件主题
	 */
	private String subject;
	/**
	 * 邮件的文本内容
	 */
	private String content;
	/**
	 * 邮件附件的文件名
	 */
	private String[] attachFileNames;

	/**
	 * 获得邮件会话属性
	 */
	public Properties getProperties() {
		Properties p = new Properties();
		p.put("mail.smtp.host", this.mailServerHost);
		p.put("mail.smtp.port", this.mailServerPort);
		p.put("mail.smtp.auth", validate ? "true" : "false");
		return p;
	}

	public MailSenderModel() {

	}

	/**
	 * 
	 * 发送邮件需要使用的基本信息entity构造函数.
	 * 
	 * @param mailServerHost
	 *            发送邮件的服务器的IP或域名
	 * @param mailServerPort
	 *            发送邮件的服务器的端口
	 * @param fromAddress
	 *            邮件发送者的地址
	 * @param toAddress
	 *            邮件接收者的地址 （地址以","分割）
	 * @param ccAddress
	 *            抄送地址（地址以","分割）
	 * @param bccAddress
	 *            暗送地址 （地址以","分割）
	 * @param userName
	 *            登陆邮件发送服务器的用户名
	 * @param password
	 *            登陆邮件发送服务器的密码
	 * @param validate
	 *            是否需要身份验证 (默认：false)
	 * @param subject
	 *            邮件主题
	 * @param content
	 *            邮件的文本内容
	 * @param attachFileNames
	 *            邮件附件的文件名
	 */
	public MailSenderModel(String mailServerHost, String mailServerPort,
			String fromAddress, String toAddress, String ccAddress,
			String bccAddress, String userName, String password,
			boolean validate, String subject, String content,
			String[] attachFileNames) {
		super();
		this.mailServerHost = mailServerHost;
		this.mailServerPort = mailServerPort;
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.ccAddress = ccAddress;
		this.bccAddress = bccAddress;
		this.userName = userName;
		this.password = password;
		this.validate = validate;
		this.subject = subject;
		this.content = content;
		this.attachFileNames = attachFileNames;
	}

	public String ftlContent(String templatePath,Map map) {
		Template template = null;
		Configuration freeMarkerConfig = null;
		String htmlText = null;
		try {
			freeMarkerConfig = new Configuration();
			freeMarkerConfig.setDirectoryForTemplateLoading(new File(templatePath));
			// 获取模板
			template = freeMarkerConfig.getTemplate(FileUtils.getFileName(templatePath),
					new Locale("Zh_cn"), "UTF-8");
			// 模板内容转换为string
			htmlText = FreeMarkerTemplateUtils
					.processTemplateIntoString(template, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlText;
	}
	
	private static String getFilePath() {
  		String path = FileUtils.getAppPath(MailSenderModel.class);
  		path = path + File.separator +"mailtemplate"+File.separator;
		path = path.replace("\\", "/");
		return path;
	}
	
	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String[] getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(String[] fileNames) {
		this.attachFileNames = fileNames;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getCcAddress() {
		return ccAddress;
	}

	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}

	public String getBccAddress() {
		return bccAddress;
	}

	public void setBccAddress(String bccAddress) {
		this.bccAddress = bccAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}
}