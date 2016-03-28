/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.mail.utils;

import com.eryansky.common.mail.receiver.Receiver;
import com.google.common.collect.Lists;
import com.eryansky.core.web.upload.exception.FileNameLengthLimitExceededException;
import com.eryansky.core.web.upload.exception.InvalidExtensionException;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QDecoderStream;
import org.apache.commons.fileupload.FileUploadBase;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-08-14
 */
public class MailUtils {

    /**
     * 获得邮件主题
     * @param msg 邮件内容
     * @return 解码后的邮件主题
     */
    public static String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
        String subject = msg.getSubject();
        if((subject==null)||(subject.length()==0)){
            subject="无主题";
        }else if ( subject.indexOf("=?x-unknown?") >=0 ){
            subject = subject.replaceAll("x-unknown","GBK" );
            subject = MimeUtility.decodeText(subject);
        }else{
            subject = MimeUtility.decodeText(subject);
        }

        subject =  decode(subject);
        return subject;
    }


    public static  String decode(String str){
        if(str == null || "".equals(str)){
            return str;
        }
        str = decodeWord(str);
        try {
            if (MimeUtility.decodeText(new String(str.getBytes("ISO8859-1"), "utf-8")).indexOf('?') < 0) {
                str = new String(str.getBytes("ISO8859-1"), "GBK");
            }
        } catch (UnsupportedEncodingException e) {
        }

        return str;
    }


    /**
     * 获得邮件发件人
     *
     * @param msg 邮件内容
     * @return 姓名 <Email地址>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public static InternetAddress getFrom(MimeMessage msg) throws MessagingException {
        Address[] froms = msg.getFrom();
        if (froms == null || froms.length < 1) {
            throw new MessagingException("没有发件人!");
        }
        InternetAddress address = (InternetAddress) froms[0];
        return address;
    }


    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     *
     * @param msg  邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws MessagingException
     */
    public static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        StringBuffer receiveAddress = new StringBuffer();
        Address[] addresss = null;
        if (type == null) {
            addresss = msg.getAllRecipients();
        } else {
            addresss = msg.getRecipients(type);
        }

        if (addresss == null || addresss.length < 1)
//            throw new MessagingException("没有收件人!");
            return null;
        for (Address address : addresss) {
            InternetAddress internetAddress = (InternetAddress) address;
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");
        }

        receiveAddress.deleteCharAt(receiveAddress.length() - 1);    //删除最后一个逗号
        return receiveAddress.toString();
    }


    public static List<InternetAddress> getRecipientAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        Address[] addresss = null;
        if (type == null) {
            addresss = msg.getAllRecipients();
        } else {
            addresss = msg.getRecipients(type);
        }
        List<InternetAddress> list = Lists.newArrayList();
        if (addresss == null || addresss.length < 1)
            return null;
        for (Address address : addresss) {
            InternetAddress internetAddress = (InternetAddress) address;
            list.add((InternetAddress) address);
        }


        return list;
    }





    /**
     * 判断邮件中是否包含附件
     *
     * @param part 邮件内容
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    public static boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("application") != -1) {
                        flag = true;
                    }

                    if (contentType.indexOf("name") != -1) {
                        flag = true;
                    }
                }

                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 判断邮件是否已读
     *
     * @param msg 邮件内容
     * @return 如果邮件已读返回true, 否则返回false
     * @throws MessagingException
     */
    public static boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    /**
     * 判断邮件是否需要阅读回执
     *
     * @param msg 邮件内容
     * @return 需要回执返回true, 否则返回false
     * @throws MessagingException
     */
    public static boolean isReplySign(MimeMessage msg) throws MessagingException {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null)
            replySign = true;
        return replySign;
    }

    /**
     * 获得邮件的优先级
     *
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException
     */
    public static String getPriority(MimeMessage msg) throws MessagingException {
        String headerPriority = "";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            headerPriority = headers[0];
        }
        return headerPriority;
    }

    /**
     * 获得邮件文本内容
     *
     * @param part    邮件体
     * @param content 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
              content.append((String) part.getContent());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }

    /**
     * 保存附件
     * @param userId       用户ID
     * @param part    邮件中多个组合体中的其中一个组合体
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveAttachment(String userId,Part part,List<String> files) throws UnsupportedEncodingException, MessagingException,
            FileNotFoundException, IOException {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    InputStream is = bodyPart.getInputStream();
                    String fileId = saveFile(userId, is, Receiver.decodeText(bodyPart.getFileName()));
                    files.add(fileId);
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(userId,bodyPart,files);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        String fileId = saveFile(userId,bodyPart.getInputStream(), Receiver.decodeText(bodyPart.getFileName()));
                        files.add(fileId);
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment(userId, (Part) part.getContent(), files);
        }
    }

    /**
     * 读取输入流中的数据保存至指定目录
     *
     * @param userId       用户ID
     * @param is       输入流
     * @param fileName 文件名
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static String saveFile(String userId,InputStream is, String fileName){
        try {
            if(fileName == null){
                fileName = userId;
            }
            com.eryansky.modules.disk.entity.File file = DiskUtils.saveUserMailFile(userId, is, fileName);
            return file.getId();
        } catch (InvalidExtensionException e) {
            e.printStackTrace();
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            e.printStackTrace();
        } catch (FileNameLengthLimitExceededException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //中文问题终极解决方案
    public static String decodeWord(String s) {
        if (!s.startsWith("=?"))
            return s;
        int i = 2;
        int j;
        if ((j = s.indexOf(63, i)) == -1)
            return s;
        String s1 = (s.substring(i, j));
        i = j + 1;
        if ((j = s.indexOf(63, i)) == -1)
            return s;
        String s2 = s.substring(i, j);
        i = j + 1;
        if ((j = s.indexOf("?=", i)) == -1)
            return s;
        String s3 = s.substring(i, j);
        try {
            ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(s3.getBytes());
            Object obj;
            if (s2.equalsIgnoreCase("B"))
                obj = new BASE64DecoderStream(bytearrayinputstream);
            else if (s2.equalsIgnoreCase("Q"))
                obj = new QDecoderStream(bytearrayinputstream);
            else
                return s;
            int k = bytearrayinputstream.available();
            byte abyte0[] = new byte[k];
            k = ((InputStream) (obj)).read(abyte0, 0, k);
            return new String(abyte0, 0, k);
        } catch (Exception ex) {
            return s;
        }

    }

}
