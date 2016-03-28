/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.mail.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.modules.mail._enum.EmailReadStatus;
import com.eryansky.modules.mail._enum.ReceiveType;
import com.eryansky.modules.mail.entity.common.BaseReferencesEmail;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * 收件箱
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_mail_inbox")
@JsonFilter(" ")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler","fieldHandler"})
public class Inbox extends BaseReferencesEmail<Inbox>{

    /**
     * 接收类型 对应发件类型 {@link ReceiveType}
     */
    private Integer receiveType;

    /**
     * 收件时间
     */
    private Date receiveTime;


    /**
	 * 收件人
	 */
	private String userId;
	
	/**
	 * 收件人是否读取该邮件 默认值：未读
     * {@link EmailReadStatus}
	 */
	private Integer isRead;

	/**
	 * 读取时间
	 */
	private Date readTime;

    public Inbox() {
    }

    @Column(length = 1)
    public Integer getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(Integer receiveType) {
        this.receiveType = receiveType;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }



    @Column(length = 36)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    /**
     * 收件人姓名
     * @return
     */
    @Transient
    public String getUserName() {
        return UserUtils.getUserName(userId);
    }


    /**
     * 是否读取邮件 显示
     * @return
     */
    @Transient
    public String getIsReadView() {
        EmailReadStatus s = EmailReadStatus.getEmailReadStatus(isRead);
        String str = "";
        if(s != null){
            str =  s.getDescription();
        }
        return str;
    }

}
