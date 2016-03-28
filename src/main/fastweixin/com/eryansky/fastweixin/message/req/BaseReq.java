package com.eryansky.fastweixin.message.req;

public class BaseReq {

    String toUserName;
    String fromUserName;
    long   createTime;
    String msgType;

    public String getToUserName() {
        return toUserName;
    }

    public BaseReq setToUserName(String toUserName) {
        this.toUserName = toUserName;
        return this;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public BaseReq setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
        return this;
    }

    public long getCreateTime() {
        return createTime;
    }

    public BaseReq setCreateTime(long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getMsgType() {
        return msgType;
    }

    public BaseReq setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

}
