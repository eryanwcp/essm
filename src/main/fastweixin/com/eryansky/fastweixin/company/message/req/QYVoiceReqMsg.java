package com.eryansky.fastweixin.company.message.req;

/**
 *  微信企业号语音消息事件
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYVoiceReqMsg extends QYBaseReqMsg{
    private String mediaId;
    private String format;

    public QYVoiceReqMsg(String mediaId, String format) {
        this.mediaId = mediaId;
        this.format = format;
    }

    public String getMediaId() {
        return mediaId;
    }

    public QYVoiceReqMsg setMediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public QYVoiceReqMsg setFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    public String toString() {
        return "QYVoiceReqMsg [format=" + format + ", mediaId=" + mediaId
                + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", msgId=" + msgId + ", agentId=" + agentId + "]";
    }
}
