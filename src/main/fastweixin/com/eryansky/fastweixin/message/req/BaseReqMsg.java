package com.eryansky.fastweixin.message.req;

public class BaseReqMsg extends BaseReq {

    String msgId;

    public String getMsgId() {
        return msgId;
    }

    public BaseReqMsg setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

}
