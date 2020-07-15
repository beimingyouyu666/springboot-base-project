package com.yangk.baseproject.common.util.distributedlock;

import java.util.concurrent.TimeUnit;

/**
 * @Description 分布式锁
 * @Author yangkun
 * @Date 2020/7/2
 * @Version 1.0
 * @blame yangkun
 */
public interface DistributedLock {

    /**
    * @Description:  获取锁
    * @Author: yangkun
    * @Date: 2020/7/2
    * @Param:
     * @param key
     * @param value
     * @param tryLockTime
     * @param unit
     * @param expireTime
    * @return: boolean
    */
    boolean getlock(String key, String value, long tryLockTime, TimeUnit unit, int expireTime);

    /**
    * @Description:  释放锁
    * @Author: yangkun
    * @Date: 2020/7/2
    * @Param:
     * @param key
     * @param value
    * @return: boolean
    */
    boolean unLock(String key, String value);

}
