/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.mail.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.modules.mail._enum.RecycleBinFromBox;
import com.eryansky.modules.mail.entity.common.BaseReferencesEmail;
import com.eryansky.modules.mail.entity.common.IEmail;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 回收站
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_mail_recyclebin")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler","fieldHandler"})
public class RecycleBin extends BaseReferencesEmail<RecycleBin> implements IEmail,Serializable {

    /**
     * 归属人
     */
    private String userId;
	/**
	 * 邮件删除时间
	 */
	private Date delTime;
	/**
	 * 删除位置 {@link com.eryansky.modules.mail._enum.RecycleBinFromBox}
	 */
	private Integer fromBox;

    public RecycleBin() {
    }

    @Column(length = 36)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone =  TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDelTime() {
        return delTime;
    }

    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }

    public Integer getFromBox() {
        return fromBox;
    }

    public void setFromBox(Integer fromBox) {
        this.fromBox = fromBox;
    }


    /**
     * 删除来源 显示
     * @return
     */
    @Transient
    public String getFromBoxView() {
        RecycleBinFromBox s = RecycleBinFromBox.getRecycleBinFromBox(this.fromBox);
        String str = "";
        if(s != null){
            str =  s.getDescription();
        }
        return str;
    }

    /**
     * 归属人
     * @return
     */
    @Transient
    public String getUserName(){
        return UserUtils.getUserName(this.userId);
    }

}
