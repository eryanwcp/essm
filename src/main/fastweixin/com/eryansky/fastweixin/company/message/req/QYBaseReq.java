package com.eryansky.fastweixin.company.message.req;

/**
 *  企业号基础消息模型
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYBaseReq {

    String toUserName;
    String fromUserName;
    long   createTime;
    String msgType;
    String agentId;// 企业应用的ID

    public String getToUserName() {
        return toUserName;
    }

    public QYBaseReq setToUserName(String toUserName) {
        this.toUserName = toUserName;
        return this;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public QYBaseReq setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
        return this;
    }

    public long getCreateTime() {
        return createTime;
    }

    public QYBaseReq setCreateTime(long createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getMsgType() {
        return msgType;
    }

    public QYBaseReq setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    public String getAgentId() {
        return agentId;
    }

    public QYBaseReq setAgentId(String agentId) {
        this.agentId = agentId;
        return this;
    }
}
