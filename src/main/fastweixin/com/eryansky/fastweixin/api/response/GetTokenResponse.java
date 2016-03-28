package com.eryansky.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class GetTokenResponse extends BaseResponse {

    @JSONField(name = "access_token")
    private String  accessToken;
    @JSONField(name = "expires_in")
    private Integer expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
