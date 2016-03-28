package com.eryansky.fastweixin.company.message.req;
/**
 *  微信企业号异步任务类型
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public final class QYBatchJobType {

    private String SYNCUSER     = "sync_user";// 增量更新成员
    private String REPLACEUSER  = "replace_user";// 全量覆盖成员
    private String INVITEUSER   = "invite_user";// 邀请成员关注
    private String REPLACEPARTY = "replace_party";// 全量覆盖部门

    private QYBatchJobType() {
    }
}
