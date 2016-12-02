package com.eryansky.fastweixin.api.entity;

/**
 * 会员信息
 */
public class UserInfo extends BaseModel {
    private String openid;
    private String lang = "zh_CN";

    public UserInfo() {

    }

    public UserInfo(String openid) {
        this.openid = openid;
    }

    public UserInfo(String openid, String lang) {
        this.openid = openid;
        this.lang = lang;
    }

    public String getOpenid() {
        return openid;
    }

    public UserInfo setOpenid(String openid) {
        this.openid = openid;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public UserInfo setLang(String lang) {
        this.lang = lang;
        return this;
    }
}

