package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class UpstreamMsg extends BaseDataCube {

    @JSONField(name = "msg_type")
    private Integer msgType;
    @JSONField(name = "msg_user")
    private Integer msgUser;
    @JSONField(name = "msg_count")
    private Integer msgCount;

    public Integer getMsgType() {
        return msgType;
    }

    public UpstreamMsg setMsgType(Integer msgType) {
        this.msgType = msgType;
        return this;
    }

    public Integer getMsgUser() {
        return msgUser;
    }

    public UpstreamMsg setMsgUser(Integer msgUser) {
        this.msgUser = msgUser;
        return this;
    }

    public Integer getMsgCount() {
        return msgCount;
    }

    public UpstreamMsg setMsgCount(Integer msgCount) {
        this.msgCount = msgCount;
        return this;
    }
}
