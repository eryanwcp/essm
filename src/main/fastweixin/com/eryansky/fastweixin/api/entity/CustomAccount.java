package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 客服帐号对象
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class CustomAccount extends BaseModel {

    @JSONField(name = "kf_account")
    private String accountName;

    @JSONField(name = "kf_nick")
    private String nickName;

    private String password;

    @JSONField(name = "kf_id")
    private String id;

    @JSONField(name = "kf_headimg")
    private String headImg;

    public String getAccountName() {
        return accountName;
    }

    public CustomAccount setAccountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public CustomAccount setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CustomAccount setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getId() {
        return id;
    }

    public CustomAccount setId(String id) {
        this.id = id;
        return this;
    }

    public String getHeadImg() {
        return headImg;
    }

    public CustomAccount setHeadImg(String headImg) {
        this.headImg = headImg;
        return this;
    }
}
