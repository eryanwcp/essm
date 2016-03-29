package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class UpstreamMsgDist extends BaseDataCube {

    @JSONField(name = "count_interval")
    private Integer countInterval;
    @JSONField(name = "msg_user")
    private Integer msgUser;

    public Integer getCountInterval() {
        return countInterval;
    }

    public UpstreamMsgDist setCountInterval(Integer countInterval) {
        this.countInterval = countInterval;
        return this;
    }

    public Integer getMsgUser() {
        return msgUser;
    }

    public UpstreamMsgDist setMsgUser(Integer msgUser) {
        this.msgUser = msgUser;
        return this;
    }
}
