package com.eryansky.fastweixin.api.config;

import com.eryansky.fastweixin.api.entity.BaseModel;

import java.util.Date;

/**
 * 配置变化通知
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public final class ConfigChangeNotice extends BaseModel {

    private Date noticeTime;

    private String appid;

    private ChangeType type;

    private String value;

    public ConfigChangeNotice() {
        this.noticeTime = new Date();
    }

    public ConfigChangeNotice(String appid, ChangeType type, String value) {
        this();
        this.appid = appid;
        this.type = type;
        this.value = value;
    }

    public String getAppid() {
        return appid;
    }

    public ConfigChangeNotice setAppid(String appid) {
        this.appid = appid;
        return this;
    }

    public ChangeType getType() {
        return type;
    }

    public ConfigChangeNotice setType(ChangeType type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ConfigChangeNotice setValue(String value) {
        this.value = value;
        return this;
    }

    public Date getNoticeTime() {
        return noticeTime;
    }

    public ConfigChangeNotice setNoticeTime(Date noticeTime) {
        this.noticeTime = noticeTime;
        return this;
    }

    @Override
    public String toString() {
        return "ConfigChangeNotice{" +
                "noticeTime=" + noticeTime +
                ", appid='" + appid + '\'' +
                ", type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
