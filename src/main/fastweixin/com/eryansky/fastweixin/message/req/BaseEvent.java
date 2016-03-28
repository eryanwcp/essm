package com.eryansky.fastweixin.message.req;

public class BaseEvent extends BaseReq {

    private String event;

    public BaseEvent() {
        setMsgType(ReqType.EVENT);
    }

    public String getEvent() {
        return event;
    }

    public BaseEvent setEvent(String event) {
        this.event = event;
        return this;
    }

}
