package com.eryansky.j2cache.lock;

/**
 * 回调接口
 *
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2018-12-24
 */
public interface LockCallback<T> {

    /**
     * 获取到锁时回调
     * 
     * @return
     */
    T handleObtainLock();

    /**
     * 没有获取到锁时回调
     * 
     * @return
     * @throws LockCantObtainException
     */
    T handleNotObtainLock() throws LockCantObtainException;

    /**
     * 获取到锁时，执行业务逻辑出错
     * 
     * @param e
     * @return
     * @throws LockInsideExecutedException
     */
    T handleException(LockInsideExecutedException e) throws LockInsideExecutedException;

}