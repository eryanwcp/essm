/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.vo;

import com.eryansky.modules.mail._enum.EmailPriority;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 邮件页面查询条件
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-01-28
 */
public class EmailQueryVo implements Serializable {

    /**
     * 邮件账号ID
     */
    private String mailAccountId;
    /**
     * 邮件标题
     */
    private String title;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 邮件重要性 {@link EmailPriority}
     */
    private Integer priority;
    /**
     * 邮件大小
     */
    private Long emailSize;
    /**
     * 起始时间
     */
    private Date startTime;
    /**
     * 截止时间
     */
    private Date endTime;

    /**
     * 收件人/联系人ID
     */
    private List<String> receiveObjectIds;
    /**
     * 发件人/联系人Id
     */
    private String sendObjectId;
    /**
     * 发件人/联系人集合
     */
    private List<String> sendObjectIds;
    /**
     * 收件箱读取状态
     */
    private Integer inboxReadStatus;

    public String getMailAccountId() {
        return mailAccountId;
    }

    public void setMailAccountId(String mailAccountId) {
        this.mailAccountId = mailAccountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getEmailSize() {
        return emailSize;
    }

    public void setEmailSize(Long emailSize) {
        this.emailSize = emailSize;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<String> getReceiveObjectIds() {
        return receiveObjectIds;
    }

    public void setReceiveObjectIds(List<String> receiveObjectIds) {
        this.receiveObjectIds = receiveObjectIds;
    }

    public String getSendObjectId() {
        return sendObjectId;
    }

    public void setSendObjectId(String sendObjectId) {
        this.sendObjectId = sendObjectId;
    }

    public List<String> getSendObjectIds() {
        return sendObjectIds;
    }

    public void setSendObjectIds(List<String> sendObjectIds) {
        this.sendObjectIds = sendObjectIds;
    }

    public Integer getInboxReadStatus() {
        return inboxReadStatus;
    }

    public void setInboxReadStatus(Integer inboxReadStatus) {
        this.inboxReadStatus = inboxReadStatus;
    }

    /**
     * 将截止时间设置到当天最后1秒钟 23h 59m 59s
     */
    public void syncEndTime(){
        if(this.endTime != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.endTime);
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            this.endTime = calendar.getTime();
        }
    }
}
