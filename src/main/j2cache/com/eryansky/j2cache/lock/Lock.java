package com.eryansky.j2cache.lock;

/**
 * 分布式并发锁<br>
 * 对给定的可以
 *
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2018-12-24
 */
public interface Lock {
    
    /**
     *  功能描述:    </br>
     *            一次性分布式并发锁，只获取一次锁，拿不到所直接返回失败 </br>
     * 
     * @author ningyu
     * @date 2017年10月20日 下午2:15:21
     *
     * @param key
     *              锁的key, 通过这个key来唯一确认锁，建议
     * @param expireSeconds
     *              锁的最大过期时间， 因为是并发锁，为了杜绝因为获得锁而没有释放造成的问题
     * @param lockCallback
     *              获取锁过程中的一些callback方法
     * @return
     * @throws LockInsideExecutedException
     *              在获得锁后，在进行业务操作是发生的异常
     * @throws LockCantObtainException
     *              获取锁的timeout, 仍未获得锁
     */
    <T> T lock(String key, long expireSeconds, LockCallback<T> lockCallback)
            throws LockInsideExecutedException, LockCantObtainException;

    /**
     * 
     * 功能描述:    </br>
     *          分布式并发锁 ,可重试多次拿锁,通过设置获取锁的超时时间(timeoutInSeconds)会根据常规频率(LockRetryFrequency.NORMAL)计算重试次数 </br>
     * 示例描述:    </br>
     *          timeoutInSeconds=1秒钟,计算得到的重试次数=10次 </br>
     *          timeoutInSeconds=5秒钟,计算得到的重试次数=50次 </br>
     *          timeoutInSeconds=10秒钟,计算得到的重试次数=100次 </br>
     * 
     * @param key
     *            锁的key, 通过这个key来唯一确认锁，建议
     * @param timeoutInSeconds
     *            获取锁的timeout,说明：1秒等于重试10次
     * @param expireSeconds
     *            锁的最大过期时间， 因为是并发锁，为了杜绝因为获得锁而没有释放造成的问题
     * @param lockCallback
     *            获取锁过程中的一些callback方法
     * @return
     * @throws LockInsideExecutedException
     *             在获得锁后，在进行业务操作是发生的异常
     * @throws LockCantObtainException
     *             获取锁的timeout, 仍未获得锁
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
     <T> T lock(String key, int timeoutInSeconds, long expireSeconds, LockCallback<T> lockCallback)
            throws LockInsideExecutedException, LockCantObtainException;

    /**
     * 
     * 功能描述:    </br>
     *          分布式并发锁,可重试多次拿锁,通过设置获取锁的超时时间(timeoutInSeconds)和获取频率(frequency)计算重试次数 </br>
     * 示例描述:    </br>
     *          timeoutInSeconds=1秒,frequency=LockRetryFrequency.VERY_QUICK,计算得到的重试次数=100次 </br>
     *          timeoutInSeconds=1秒,frequency=LockRetryFrequency.QUICK,计算得到的重试次数=20次 </br>
     *          timeoutInSeconds=1秒,frequency=LockRetryFrequency.NORMAL,计算得到的重试次数=10次 </br>
     *          timeoutInSeconds=1秒,frequency=LockRetryFrequency.SLOW,计算得到的重试次数=2次 </br>
     *          timeoutInSeconds=1秒,frequency=LockRetryFrequency.VERY_SLOW,计算得到的重试次数=1次 </br>
     * 
     * @param key
     *            锁的key, 通过这个key来唯一确认锁，建议
     * @param frequency
     *            获取锁的frequency,
     * @param timeoutInSeconds
     *            获取锁的timeout,
     * @param expireSeconds
     *            锁的最大过期时间， 因为是并发锁，为了杜绝因为获得锁而没有释放造成的问题
     * @param lockCallback
     *            一些了callback方法
     * @return
     * @throws LockInsideExecutedException
     *             在获得锁后，在进行业务操作是发生的异常
     * @throws LockCantObtainException
     *             获取锁的timeout, 仍未获得锁
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    <T> T lock(String key, LockRetryFrequency frequency, int timeoutInSeconds,
            long expireSeconds, LockCallback<T> lockCallback) throws LockInsideExecutedException,
            LockCantObtainException;

}