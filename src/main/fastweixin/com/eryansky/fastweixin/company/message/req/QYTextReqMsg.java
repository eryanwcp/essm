package com.eryansky.fastweixin.company.message.req;

/**
 *  微信企业号文本消息事件
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYTextReqMsg extends QYBaseReqMsg {

    private String content;

    public QYTextReqMsg(String content) {
        super();
        this.content = content;
        setMsgType(QYReqType.TEXT);
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "QYTextReqMsg [content=" + content
                + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", msgId=" + msgId + ", agentId=" + agentId + "]";
    }
}
