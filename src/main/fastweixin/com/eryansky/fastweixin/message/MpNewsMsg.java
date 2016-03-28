package com.eryansky.fastweixin.message;
/**
 * 提交至微信的图文消息素材
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class MpNewsMsg extends BaseMsg {

    private String mediaId;

    public MpNewsMsg() {
    }

    public MpNewsMsg(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public MpNewsMsg setMediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }
}
