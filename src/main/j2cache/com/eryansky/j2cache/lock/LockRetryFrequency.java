package com.eryansky.j2cache.lock;

/**
 * 锁重试获取频率策略
 *
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2018-12-24
 */
public enum LockRetryFrequency {

    VERY_QUICK(10), QUICK(50), NORMAL(100), SLOW(500), VERY_SLOW(1000);

    private int retryInterval = 100;

    /**
     * 
     * @param retryInterval 重试间隔 单位：毫秒
     */
    LockRetryFrequency(int retryInterval) {
        retryInterval = retryInterval;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

}
