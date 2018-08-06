package com.eryansky.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * 客户会话状态
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-08-06
 */
public class GetCustomSessionResponse extends BaseResponse {

    /**
     * 正在接待的客服，为空表示没有人在接待
     */
    @JSONField(name = "kf_account")
    private String accountName;

    /**
     * 会话接入的时间
     */
    @JSONField(name = "createtime")
    private Long createtime;

    public GetCustomSessionResponse() {
    }

    public GetCustomSessionResponse(String accountName, Long createtime) {
        this.accountName = accountName;
        this.createtime = createtime;
    }

    public String getAccountName() {
        return accountName;
    }

    public GetCustomSessionResponse setAccountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public GetCustomSessionResponse setCreatetime(Long createtime) {
        this.createtime = createtime;
        return this;
    }
}
