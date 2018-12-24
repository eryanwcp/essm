package com.eryansky.j2cache.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的一个callback类<br>
 * 〈功能详细描述〉
 * 
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2018-12-24
 */
public abstract class DefaultLockCallback<T> implements LockCallback<T> {
    private final Logger logger = LoggerFactory.getLogger(DefaultLockCallback.class);
    private T returnValueForhandleNotObtainLock;
    private T returnValueForhandleException;

    /**
     * @param returnValueForhandleNotObtainLock
     *            没有获取到锁时，返回值
     * @param returnValueForhandleException
     *            获取到锁后内部执行报错时，返回值
     */
    public DefaultLockCallback(T returnValueForhandleNotObtainLock, T returnValueForhandleException) {
        super();
        this.returnValueForhandleNotObtainLock = returnValueForhandleNotObtainLock;
        this.returnValueForhandleException = returnValueForhandleException;
    }

    @Override
    public T handleNotObtainLock() {
        logger.error("LockCantObtainException");
        return returnValueForhandleNotObtainLock;
    }

    @Override
    public T handleException(LockInsideExecutedException e) {
        logger.error("LockInsideExecutedException", e);
        return returnValueForhandleException;
    }

}