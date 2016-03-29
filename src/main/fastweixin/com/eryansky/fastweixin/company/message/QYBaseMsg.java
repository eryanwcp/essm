package com.eryansky.fastweixin.company.message;

import com.eryansky.fastweixin.api.entity.BaseModel;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYBaseMsg extends BaseModel implements Serializable {

    public static final class Safe {
        public static final String YES = "1";// 保密消息
        public static final String NO  = "0";// 非保密消息
    }

    @JSONField(name = "touser")
    private String toUser;
    @JSONField(name = "toparty")
    private String toParty;
    @JSONField(name = "totag")
    private String toTag;
    @JSONField(name = "msgtype")
    private String msgType;
    @JSONField(name = "agentid")
    private String agentId;
    @JSONField(name = "safe")
    private String safe = Safe.NO;

    public String getToUser() {
        return toUser;
    }

    public QYBaseMsg setToUser(String toUser) {
        this.toUser = toUser;
        return this;
    }

    public String getToParty() {
        return toParty;
    }

    public QYBaseMsg setToParty(String toParty) {
        this.toParty = toParty;
        return this;
    }

    public String getToTag() {
        return toTag;
    }

    public QYBaseMsg setToTag(String toTag) {
        this.toTag = toTag;
        return this;
    }

    public String getMsgType() {
        return msgType;
    }

    public QYBaseMsg setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    public String getAgentId() {
        return agentId;
    }

    public QYBaseMsg setAgentId(String agentId) {
        this.agentId = agentId;
        return this;
    }

    public String getSafe() {
        return safe;
    }

    public QYBaseMsg setSafe(String safe) {
        this.safe = safe;
        return this;
    }
}
