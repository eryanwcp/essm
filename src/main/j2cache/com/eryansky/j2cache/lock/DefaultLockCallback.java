package com.eryansky.j2cache.lock;

/**
 * 默认的一个callback类<br>
 * 〈功能详细描述〉
 * 
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2018-12-24
 */
public abstract class DefaultLockCallback<T> implements LockCallback<T> {

    private T handleNotObtainLock;
    private T handleException;

    /**
     * @param handleNotObtainLock
     *            没有获取到锁时，返回值
     * @param handleException
     *            获取到锁后内部执行报错时，返回值
     */
    public DefaultLockCallback(T handleNotObtainLock, T handleException) {
        super();
        this.handleNotObtainLock = handleNotObtainLock;
        this.handleException = handleException;
    }

    @Override
    public T handleNotObtainLock() {
        return handleNotObtainLock;
    }

    @Override
    public T handleException(LockInsideExecutedException e) {
        return handleException;
    }

}