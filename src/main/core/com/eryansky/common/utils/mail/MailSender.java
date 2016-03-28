/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.utils.mail;

import com.eryansky.common.utils.mail.model.MailSenderModel;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Date;

/**
 * 简单邮件发送器. <br>
 * 支持文本邮件、HTML邮件、带附件的HTML邮件
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-5-5 下午3:40:06
 */
public class MailSender {

    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * 以文本格式发送邮件
     * 
     * @param entity
     *            待发送的邮件的信息
     */
    public boolean sendTextMail(MailSenderModel entity) {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        if (entity.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(entity.getUserName(),
                    entity.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(
                entity.getProperties(), authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(entity.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            // Address to = new InternetAddress(entity.getToAddress());
            mailMessage.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(entity.getToAddress()));
            // 抄送
            if (entity.getCcAddress() != null
                    && !"".equals(entity.getCcAddress())) {
                mailMessage.setRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(entity.getCcAddress()));
            }
            // 暗送
            if (entity.getBccAddress() != null
                    && !"".equals(entity.getBccAddress())) {
                mailMessage.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(entity.getBccAddress()));
            }
            // 设置邮件消息的主题
            mailMessage.setSubject(entity.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件消息的主要内容
            String mailContent = entity.getContent();
            mailMessage.setText(mailContent);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 以HTML格式发送邮件
     * 
     * @param entity
     *            待发送的邮件信息
     */
    public boolean sendHtmlMail(MailSenderModel entity) {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        // 如果需要身份认证，则创建一个密码验证器
        if (entity.isValidate()) {
            authenticator = new MyAuthenticator(entity.getUserName(),
                    entity.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(
                entity.getProperties(), authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(entity.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            // Address to = new InternetAddress();
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(entity.getToAddress()));
            // 抄送
            if (entity.getCcAddress() != null
                    && !"".equals(entity.getCcAddress())) {
                mailMessage.setRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(entity.getCcAddress()));
            }
            // 暗送
            if (entity.getBccAddress() != null
                    && !"".equals(entity.getBccAddress())) {
                mailMessage.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(entity.getBccAddress()));
            }
            // 设置邮件消息的主题
            mailMessage.setSubject(entity.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();
            // 设置HTML内容
            html.setContent(entity.getContent(), "text/html; charset="
                    + DEFAULT_ENCODING);
            mainPart.addBodyPart(html);
            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 发送带附件的(HTML)邮件
     * 
     * @param entity
     *            待发送的邮件信息
     */
    public boolean sendAffixMail(MailSenderModel entity) {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        // 如果需要身份认证，则创建一个密码验证器
        if (entity.isValidate()) {
            authenticator = new MyAuthenticator(entity.getUserName(),
                    entity.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(
                entity.getProperties(), authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(entity.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            // Address to = new InternetAddress(entity.getToAddress());
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(entity.getToAddress()));
            // 抄送
            if (entity.getCcAddress() != null
                    && !"".equals(entity.getCcAddress())) {
                mailMessage.setRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(entity.getCcAddress()));
            }

            // 暗送
            if (entity.getBccAddress() != null
                    && !"".equals(entity.getBccAddress())) {
                mailMessage.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(entity.getBccAddress()));
            }
            // 设置邮件消息的主题
            mailMessage.setSubject(entity.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();
            // 设置HTML内容
            html.setContent(entity.getContent(), "text/html; charset="
                    + DEFAULT_ENCODING);
            mainPart.addBodyPart(html);

            // 组装附件
            MimeBodyPart file;
            String[] filePaths = entity.getAttachFileNames();
            FileDataSource file_datasource = null;
            if (filePaths != null && filePaths.length > 0) {
                for (int i = 0; i < filePaths.length; i++) {
                    file = new MimeBodyPart();
                    file_datasource = new FileDataSource(new File(filePaths[i]));
                    DataHandler dh = new DataHandler(file_datasource);
                    file.setDataHandler(dh);
                    // 附件区别内嵌内容的一个特点是有文件名，为防止中文乱码要编码
                    file.setFileName(MimeUtility.encodeText(dh.getName(),
                            DEFAULT_ENCODING, null));
                    System.out.println(dh.getName());
                    mainPart.addBodyPart(file);
                }
            }

            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);

            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}