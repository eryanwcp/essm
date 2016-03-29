package com.eryansky.fastweixin.company.message.req;
/**
 *  微信企业号事件消息基类
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYBaseEvent extends QYBaseReq{

    String event;

    public QYBaseEvent() {
        setMsgType(QYReqType.EVENT);
    }

    public String getEvent() {
        return event;
    }

    public QYBaseEvent setEvent(String event) {
        this.event = event;
        return this;
    }
}
