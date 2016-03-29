package com.eryansky.fastweixin.company.message.req;
/**
 *  微信企业号普通消息事件基类
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYBaseReqMsg extends QYBaseReq {

    String msgId;

    public String getMsgId() {
        return msgId;
    }

    public QYBaseReqMsg setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }
}
