package com.eryansky.fastweixin.message.req;

/**
 * 发生消息事件
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class SendMessageEvent extends BaseEvent {
    private String msgId;//群发的消息ID
    private String status;//群发消息状态
    private Integer totalCount;//发送总数
    private Integer filterCount;//过滤后的数量
    private Integer sentCount;//发送成功数量
    private Integer errorCount;//发送失败数量

    public SendMessageEvent() {
        super();
    }

    public SendMessageEvent(String msgId, String status, Integer totalCount, Integer filterCount, Integer sentCount, Integer errorCount) {
        super();
        this.msgId = msgId;
        this.status = status;
        this.totalCount = totalCount;
        this.filterCount = filterCount;
        this.sentCount = sentCount;
        this.errorCount = errorCount;
    }

    public String getMsgId() {
        return msgId;
    }

    public SendMessageEvent setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public SendMessageEvent setStatus(String status) {
        this.status = status;
        return this;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public SendMessageEvent setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public Integer getFilterCount() {
        return filterCount;
    }

    public SendMessageEvent setFilterCount(Integer filterCount) {
        this.filterCount = filterCount;
        return this;
    }

    public Integer getSentCount() {
        return sentCount;
    }

    public SendMessageEvent setSentCount(Integer sentCount) {
        this.sentCount = sentCount;
        return this;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public SendMessageEvent setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
        return this;
    }
}
