package com.eryansky.fastweixin.company.message.req;
/**
 *  微信企业号菜单事件
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYMenuEvent extends QYBaseEvent {

    private String eventKey;

    public QYMenuEvent(String eventKey) {
        super();
        this.eventKey = eventKey;
    }

    public String getEventKey() {
        return eventKey;
    }

    public QYMenuEvent setEventKey(String eventKey) {
        this.eventKey = eventKey;
        return this;
    }

    @Override
    public String toString(){
        return "QYMenuEvent [eventKey=" + eventKey
                + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", event=" + event + ", agentId=" + agentId + "]";
    }
}
