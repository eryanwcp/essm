package com.eryansky.fastweixin.company.message.resp;

import com.eryansky.fastweixin.message.util.MessageBuilder;

import java.io.Serializable;

/**
 *  微信企业号被动响应消息基类
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYBaseRespMsg implements Serializable {

    private String toUserName;
    private String fromUserName;
    private long createTime;
    private String msgType;

    public QYBaseRespMsg() {
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String toXml(){
        // 159 = 106 + 28(ToUserName) + 15(FromUserName) + 10(CreateTime)
        MessageBuilder builder = new MessageBuilder(159);
        builder.addData("ToUserName", getToUserName());
        builder.addData("FromUserName", getFromUserName());
        builder.addTag("CreateTime", String.valueOf(System.currentTimeMillis() / 1000));
        return builder.toString();
    }

    @Override
    public String toString() {
        return toXml();
    }
}
