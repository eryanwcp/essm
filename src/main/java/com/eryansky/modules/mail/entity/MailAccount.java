/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.entity;

import com.eryansky.common.mail.config.IMAPServerConfig;
import com.eryansky.common.mail.config.POP3ServerConfig;
import com.eryansky.common.mail.config.SMTPServerConfig;
import com.eryansky.common.mail.config.ServerConfig;
import com.eryansky.common.mail.entity.Account;
import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.DataEntity;
import com.eryansky.modules.mail._enum.AccountActivite;

import javax.persistence.*;

/**
 * 邮件账号
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-08-13 
 */
@Entity
@Table(name = "T_MAIL_ACCOUNT")
@Delete(propertyName = "status", type = PropertyType.S)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class MailAccount extends DataEntity<MailAccount> {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 显示名称
     */
    private String name;
    /**
     * Email邮件地址
     */
    private String mailAddress;
    /**
     * 邮件帐号
     */
    private String username;
    /**
     * 邮件密码
     */
    private String password;
    /**
     * 账号是否活动 默认值：1 1：活动 0：不活动 {@link AccountActivite}
     */
    private Integer activate = AccountActivite.ACTIVITE.getValue();


    /**
     * 接收设置
     */
    private String receiverProtocol;
    private String receiverAddress;
    private Integer receiverPort;
    private Boolean isReceiverNeedAuth;
    private String receiverEncryptionType;

    /**
     * 发送设置
     */
    private String senderProtocol;
    private String senderAddress;
    private Integer senderPort;
    private Boolean isSenderNeedAuth;
    private String senderEncryptionType;
    private String senderContentType;

    protected Integer connectionTimeout;

    @Column(length = 36)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(length = 36)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(length = 128)
    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    @Column(length = 64)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(length = 64)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(length = 1)
    public Integer getActivate() {
        return activate;
    }

    public void setActivate(Integer activate) {
        this.activate = activate;
    }

    @Column(length = 36)
    public String getReceiverProtocol() {
        return receiverProtocol;
    }

    public void setReceiverProtocol(String receiverProtocol) {
        this.receiverProtocol = receiverProtocol;
    }
    @Column(length = 128)
    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public Integer getReceiverPort() {
        return receiverPort;
    }

    public void setReceiverPort(Integer receiverPort) {
        this.receiverPort = receiverPort;
    }

    public Boolean getIsReceiverNeedAuth() {
        return isReceiverNeedAuth;
    }

    public void setIsReceiverNeedAuth(Boolean isReceiverNeedAuth) {
        this.isReceiverNeedAuth = isReceiverNeedAuth;
    }
    @Column(length = 36)
    public String getReceiverEncryptionType() {
        return receiverEncryptionType;
    }

    public void setReceiverEncryptionType(String receiverEncryptionType) {
        this.receiverEncryptionType = receiverEncryptionType;
    }
    @Column(length = 36)
    public String getSenderProtocol() {
        return senderProtocol;
    }

    public void setSenderProtocol(String senderProtocol) {
        this.senderProtocol = senderProtocol;
    }
    @Column(length = 128)
    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public Integer getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(Integer senderPort) {
        this.senderPort = senderPort;
    }

    public Boolean getIsSenderNeedAuth() {
        return isSenderNeedAuth;
    }

    public void setIsSenderNeedAuth(Boolean isSenderNeedAuth) {
        this.isSenderNeedAuth = isSenderNeedAuth;
    }
    @Column(length = 36)
    public String getSenderEncryptionType() {
        return senderEncryptionType;
    }

    public void setSenderEncryptionType(String senderEncryptionType) {
        this.senderEncryptionType = senderEncryptionType;
    }

    @Column(length = 4)
    public String getSenderContentType() {
        return senderContentType;
    }

    public void setSenderContentType(String senderContentType) {
        this.senderContentType = senderContentType;
    }


    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Transient
    public String getActivateString(){
        Integer activate=this.activate;
        if(activate!=null&&activate==1){
            return "是";
        }
        return "否";
    }

    @Transient
    @JsonIgnore
    public Account toAccount(){
        Account account = new Account();
        account.setMailAddress(this.getMailAddress());
        account.setUsername(this.getUsername());
        account.setPassword(this.getPassword());
        ServerConfig receiverServer = null;
        if(ServerConfig.Protocol.POP3.toString().equals(this.getReceiverProtocol())){
            receiverServer = new POP3ServerConfig();
        }else if(ServerConfig.Protocol.IMAP.toString().equals(this.getReceiverProtocol())){
            receiverServer = new IMAPServerConfig();
        }


        receiverServer.setAddress(this.getReceiverAddress());
        receiverServer.setPort(this.getReceiverPort());
//        receiverServer.setUsername(this.getUsername());
//        receiverServer.setPassword(this.getPassword());
        receiverServer.setNeedAuth(this.getIsReceiverNeedAuth());
        receiverServer.setEncryptionType(Enum.valueOf(ServerConfig.EncryptionType.class, this.getReceiverEncryptionType()));

        if(connectionTimeout != null){
            receiverServer.setConnectionTimeout(connectionTimeout);
        }
        account.setReceiverServer(receiverServer);

        ServerConfig senderServer = null;
        if(ServerConfig.Protocol.SMTP.toString().equals(this.getSenderProtocol())){
            senderServer = new SMTPServerConfig();
        }
        senderServer.setAddress(this.getSenderAddress());
        senderServer.setPort(this.getSenderPort());
//        senderServer.setUsername(this.getUsername());
//        senderServer.setPassword(this.getPassword());
        senderServer.setNeedAuth(this.getIsSenderNeedAuth());
        senderServer.setEncryptionType(Enum.valueOf(ServerConfig.EncryptionType.class, this.getSenderEncryptionType()));
        senderServer.setContentType(Enum.valueOf(ServerConfig.ContentType.class, this.getSenderContentType()));

        account.setSenderServer(senderServer);
        return account;
    }

    @Transient
    @JsonIgnore
    public MailAccount copy(String userId){
        MailAccount mailAccount = new MailAccount();
        mailAccount.setName(this.name);
        mailAccount.setActivate(AccountActivite.ACTIVITE.getValue());
        mailAccount.setMailAddress(this.mailAddress);
        mailAccount.setIsReceiverNeedAuth(this.getIsReceiverNeedAuth());
        mailAccount.setReceiverAddress(this.getReceiverAddress());
        mailAccount.setReceiverPort(this.getReceiverPort());
        mailAccount.setReceiverProtocol(this.getReceiverProtocol());
        mailAccount.setReceiverEncryptionType(this.getReceiverEncryptionType());
        mailAccount.setSenderAddress(this.senderAddress);
        mailAccount.setSenderPort(this.senderPort);
        mailAccount.setSenderProtocol(this.getSenderProtocol());
        mailAccount.setSenderEncryptionType(this.senderEncryptionType);
        mailAccount.setSenderContentType(this.senderContentType);
        mailAccount.setUserId(userId);
        return mailAccount;
    }

}