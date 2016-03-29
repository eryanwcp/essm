package com.eryansky.fastweixin.company.message.req;
/**
 *  微信企业号进入应用事件消息
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYEnterAgentEvent extends QYMenuEvent {

    public QYEnterAgentEvent() {
        super("");
    }

    @Override
    public String toString(){
        return "QYMenuEvent [eventKey=" + getEventKey()
                + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", event=" + event + ", agentId=" + agentId + "]";
    }
}
