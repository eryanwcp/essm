package com.eryansky.fastweixin.company.message.req;
/**
 *  微信公众号异步任务完成事件
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYBatchJobEvent extends QYBaseEvent{

    private String jobId;
    private String jobType;
    private int errCode;
    private String errMsg;

    public QYBatchJobEvent(String jobId, String jobType, int errCode, String errMsg) {
        this.jobId = jobId;
        this.jobType = jobType;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getJobId() {
        return jobId;
    }

    public QYBatchJobEvent setJobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getJobType() {
        return jobType;
    }

    public QYBatchJobEvent setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public int getErrCode() {
        return errCode;
    }

    public QYBatchJobEvent setErrCode(int errCode) {
        this.errCode = errCode;
        return this;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public QYBatchJobEvent setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    @Override
    public String toString(){
        return "QYMenuEvent [jobId=" + jobId + ", jobType=" + jobType + ", errCode=" + errCode + ", errMsg=" + errMsg
                + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", event=" + event + ", agentId=" + agentId + "]";
    }
}
