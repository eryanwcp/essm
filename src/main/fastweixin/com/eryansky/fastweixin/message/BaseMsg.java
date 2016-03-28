package com.eryansky.fastweixin.message;

import com.eryansky.fastweixin.message.util.MessageBuilder;

import java.io.Serializable;

public class BaseMsg implements Serializable{

    private String toUserName;
    private String fromUserName;
    private long   createTime;
    private String msgType;

    public BaseMsg() {
    }

    public String getToUserName() {
        return toUserName;
    }

    public BaseMsg setToUserName(String toUserName) {
        this.toUserName = toUserName;
        return this;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public BaseMsg setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
        return this;
    }

    public long getCreateTime() {
        return createTime;
    }

    public BaseMsg setCreateTime(long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getMsgType() {
        return msgType;
    }

    public BaseMsg setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    public String toXml() {
        // 159 = 106 + 28(ToUserName) + 15(FromUserName) + 10(CreateTime)
        MessageBuilder builder = new MessageBuilder(159);
        builder.addData("ToUserName", getToUserName());
        builder.addData("FromUserName", getFromUserName());
        builder.addTag("CreateTime", String.valueOf(System.currentTimeMillis()/1000));
        return builder.toString();
    }

    @Override
    public String toString() {
        return toXml();
    }

}
