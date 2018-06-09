/**
 * Copyright (c) 2014 http://www.jfit.com.cn
 * <p/>
 * 江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.notice.mapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.modules.notice._enum.MessageReceiveObjectType;
import com.eryansky.modules.notice.utils.MessageUtils;

/**
 * 消息发送表
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-14 
 */
public class MessageSender extends DataEntity<MessageSender> {
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 发送类型 会员：member 部门：organ {@link MessageReceiveObjectType}
     */
    private String objectType;
    /**
     * 对象ID
     */
    private String objectId;

    public MessageSender() {
    }

    public MessageSender(String messageId) {
        this.messageId = messageId;
    }

    public MessageSender(String id, String messageId) {
        super(id);
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    @JsonIgnore
    public Message getMessage() {
        return MessageUtils.get(messageId);
    }
}
