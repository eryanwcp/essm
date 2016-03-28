package com.eryansky.fastweixin.company.message.req;
/**
 *
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYVideoReqMsg extends QYBaseReqMsg {

    private String mediaId;
    private String thumbMediaId;

    public QYVideoReqMsg(String mediaId, String thumbMediaId) {
        super();
        this.mediaId = mediaId;
        this.thumbMediaId = thumbMediaId;
        setMsgType(QYReqType.VIDEO);
    }

    public String getMediaId() {
        return mediaId;
    }

    public QYVideoReqMsg setMediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public QYVideoReqMsg setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
        return this;
    }

    @Override
    public String toString() {
        return "QYVideoReqMsg [mediaId=" + mediaId + ", thumbMediaId=" + thumbMediaId
                + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", msgId=" + msgId + ", agentId=" + agentId + "]";
    }
}
