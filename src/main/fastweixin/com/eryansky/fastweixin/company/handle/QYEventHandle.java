package com.eryansky.fastweixin.company.handle;

import com.eryansky.fastweixin.company.message.resp.QYBaseRespMsg;
import com.eryansky.fastweixin.company.message.req.QYBaseEvent;

/**
 *  微信企业号事件处理器接口
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public interface QYEventHandle<E extends QYBaseEvent>{

    /**
     * 处理微信事件
     *
     * @param event 微信事件
     * @return 回复用户的消息
     */
    QYBaseRespMsg handle(E event);

    /**
     * 在处理之前，判断本事件是否符合处理的条件
     *
     * @param event 事件
     * @return 是否需要处理
     */
    boolean beforeHandle(E event);
}
