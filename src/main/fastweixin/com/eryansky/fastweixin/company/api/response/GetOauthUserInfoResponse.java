package com.eryansky.fastweixin.company.api.response;

import com.eryansky.fastweixin.api.response.BaseResponse;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Response -- 从Oauth中获取的用户信息
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class GetOauthUserInfoResponse extends BaseResponse {

    @JSONField(name = "UserId")
    private String userid;
    @JSONField(name = "OpenId")
    private String openid;
    @JSONField(name = "DeviceId")
    private String deviceid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
}
