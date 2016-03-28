/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.monitor;


import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.mail.exception.NotSupportedException;
import com.eryansky.common.mail.receiver.Receiver;
import com.eryansky.common.mail.support.AbstractMonitor;
import com.eryansky.modules.mail.entity.MailAccount;
import com.eryansky.modules.mail.utils.EmailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

/**
 * 实现新邮件监听
 */
public class MailMonitor extends AbstractMonitor {

    protected Logger logger = LoggerFactory.getLogger(MailMonitor.class);

    private MailAccount mailAccount;


    private MailMonitor(Account account) throws NotSupportedException {
        super(account);
    }


    public static MailMonitor getMailMonitor(MailAccount mailAccount) throws NotSupportedException {
        MailMonitor mailMonitor = new MailMonitor(mailAccount.toAccount());
        mailMonitor.mailAccount = mailAccount;
        return mailMonitor;
    }

    /**
     * 是否是某个邮箱帐号
     * @param mailAccountId
     * @return
     */
    public boolean isMailAccount(String mailAccountId){
        return mailAccount.getId().equals(mailAccountId);
    }

    @Override
    public void reNewMessages() {
        Message[] messages = receiver.search(Receiver.SearchType.UNREAD);
        saveMessages(messages);
    }


    @Override
    public void newMessages(Message[] messages) {
        saveMessages(messages);
    }

    public void saveMessages(Message[] messages) {
        try {
            if(messages != null && messages.length >0){
                String[] uids = new String[messages.length];
                logger.info("mail[{}] count {} ",new Object[]{mailAccount.getId(),messages.length});
                for (int i = 0; i < messages.length; i++) {
                    if(this.isEnabled()){
                        MimeMessage mimeMessage = (MimeMessage) messages[i];
                        uids[i] = receiver.getUID(mimeMessage);
                        try {
                                EmailUtils.saveMessage(mailAccount.getUserId(), mailAccount.getId(), uids[i], messages[i]);
                        } catch (Exception e) {
                            logger.error("邮件["+uids[i]+"]解析异常：", e);
                        }
                    }

                }
//                EmailUtils.saveMessages(mailAccount.getUserId(), mailAccount.getId(),uids,messages);
            }
        } catch (Exception e) {
            logger.error("邮件解析异常：", e);
//			throw new SystemException(e);
        }
    }


}
