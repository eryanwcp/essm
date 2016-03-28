package com.eryansky.fastweixin.company.api.config;

import com.eryansky.fastweixin.api.config.ChangeType;
import com.eryansky.fastweixin.api.entity.BaseModel;

import java.util.Date;

/**
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYConfigChangeNotice extends BaseModel {

    private Date noticeTime;
    private String corpID;
    private ChangeType type;
    private String value;

    public QYConfigChangeNotice() {
        this.noticeTime = new Date();
    }

    public QYConfigChangeNotice(String corpID, ChangeType type, String value) {
        this();
        this.corpID = corpID;
        this.type = type;
        this.value = value;
    }

    public Date getNoticeTime() {
        return noticeTime;
    }

    public QYConfigChangeNotice setNoticeTime(Date noticeTime) {
        this.noticeTime = noticeTime;
        return this;
    }

    public String getCorpID() {
        return corpID;
    }

    public QYConfigChangeNotice setCorpID(String corpID) {
        this.corpID = corpID;
        return this;
    }

    public ChangeType getType() {
        return type;
    }

    public QYConfigChangeNotice setType(ChangeType type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public QYConfigChangeNotice setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString(){
        return "QYConfigChangeNotice{" +
                "noticeTime" + noticeTime +
                ", corpid='" + corpID + "'" +
                ", type=" + type +
                ", value='" + value + "'" +
                "}";
    }
}
