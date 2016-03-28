package com.eryansky.fastweixin.exception;

/**
 * 微信API处理异常
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class WeixinException extends RuntimeException {

    public WeixinException() {
        super();
    }

    public WeixinException(String message) {
        super(message);
    }

    public WeixinException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeixinException(Throwable cause) {
        super(cause);
    }
}
