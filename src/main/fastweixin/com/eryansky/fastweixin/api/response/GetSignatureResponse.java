package com.eryansky.fastweixin.api.response;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class GetSignatureResponse extends BaseResponse {

    private String noncestr;
    private long   timestamp;
    private String url;
    private String signature;

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
