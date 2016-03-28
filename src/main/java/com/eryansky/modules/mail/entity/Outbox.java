/**
 * Copyright (c) 2013-2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.mail.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.modules.mail._enum.OutboxMode;
import com.eryansky.modules.mail.entity.common.BaseReferencesEmail;
import com.eryansky.modules.mail.entity.common.IEmail;
import com.eryansky.modules.mail.utils.EmailUtils;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.YesOrNo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 发件箱
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_mail_outbox")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class Outbox extends BaseReferencesEmail<Outbox> implements IEmail, Serializable {

    /**
     * 状态 {@link OutboxMode}
     */
    private Integer outboxMode = OutboxMode.Draft.getValue();


    /**
     * 发件人
     */
    private String userId;

    public Outbox() {
    }

    @Column(length = 36)
    public String getUserId() {
        return this.userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getOutboxMode() {
        return outboxMode;
    }

    public void setOutboxMode(Integer outboxMode) {
        this.outboxMode = outboxMode;
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
     * 判断是否有接收人读取了邮件
     *
     * @return
     */
    @Transient
    public boolean getIsReceivesRead() {
        return EmailUtils.isReceivesRead(this.getEmailId());
    }

    @Transient
    public String getOutboxModeView() {
        OutboxMode s = OutboxMode.getOutboxMode(outboxMode);
        String str = "";
        if(s != null){
            str =  s.getDescription();
        }
        return str;
    }
}
