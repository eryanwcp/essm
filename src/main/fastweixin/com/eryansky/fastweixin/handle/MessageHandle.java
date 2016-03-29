package com.eryansky.fastweixin.handle;

import com.eryansky.fastweixin.message.req.BaseReqMsg;
import com.eryansky.fastweixin.message.BaseMsg;

/**
 * 微信消息处理器接口
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public interface MessageHandle<M extends BaseReqMsg> {
    /**
     * 处理微信消息
     *
     * @param message 微信消息
     * @return 回复用户的消息
     */
    BaseMsg handle(M message);

    /**
     * 在处理之前，判断本条消息是否符合处理的条件
     *
     * @param message 消息
     * @return 是否需要处理
     */
    boolean beforeHandle(M message);
}
