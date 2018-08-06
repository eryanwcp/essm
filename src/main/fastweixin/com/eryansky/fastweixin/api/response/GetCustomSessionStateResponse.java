package com.eryansky.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * 客户会话信息
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-08-06
 */
public class GetCustomSessionStateResponse extends BaseResponse {

    /**
     * 正在接待的客服，为空表示没有人在接待
     */
    @JSONField(name = "openid")
    private String openid;

    /**
     * 会话接入的时间
     */
    @JSONField(name = "createtime")
    private Long createtime;

    public GetCustomSessionStateResponse() {
    }

    public GetCustomSessionStateResponse(String openid, Long createtime) {
        this.openid = openid;
        this.createtime = createtime;
    }

    public String getOpenid() {
        return openid;
    }

    public GetCustomSessionStateResponse setOpenid(String openid) {
        this.openid = openid;
        return this;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public GetCustomSessionStateResponse setCreatetime(Long createtime) {
        this.createtime = createtime;
        return this;
    }
}
