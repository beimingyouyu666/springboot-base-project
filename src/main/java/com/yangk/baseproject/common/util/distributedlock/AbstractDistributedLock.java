package com.yangk.baseproject.common.util.distributedlock;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author yangkun
 * @Date 2020/7/2
 * @Version 1.0
 * @blame yangkun
 */
public abstract class AbstractDistributedLock implements DistributedLock {

    @Override
    public boolean getlock(String key, String value, long tryLockTime, TimeUnit unit, int expireTime) {
        long start = System.currentTimeMillis();
        if (tryLock(key, value, tryLockTime, unit,expireTime)) {
            return true;
        }
        // 等待锁
        waitLock(key, value, tryLockTime, unit, expireTime);
        // 继续获取锁
        getlock(key, value, tryLockTime, unit,expireTime);
        return false;
    }

    @Override
    public boolean unLock(String key, String value) {
        return releaseLock(key,value);
    }

    protected abstract boolean tryLock(String key, String value, long tryLockTime, TimeUnit unit, int expireTime);

    protected abstract boolean waitLock(String key, String value, long tryLockTime, TimeUnit unit, int expireTime);

    protected abstract boolean releaseLock(String key, String value);
}
