package com.yangk.baseproject.common.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Description Redisson操作
 * @Author yangkun
 * @Date 2020/6/30
 * @Version 1.0
 * @blame yangkun
 */
@Component
@Slf4j
public class RedissonUtil {

    @Autowired
    private RedissonClient redissonClient;

    public boolean lock(String key,long waitTime, long leaseTime, TimeUnit timeUnit) {
        String resourceName = key;
        // 还可以getFairLock(), getReadWriteLock()
        RLock redLock = redissonClient.getLock(resourceName);
        boolean isLock;
        try {
            log.debug("try lock : {}", key);
            isLock = redLock.tryLock(waitTime, leaseTime, timeUnit);
            log.debug("isLock : {}", isLock);
            if (isLock) {
                return true;
            }
        } catch (Exception e) {
            log.error("尝试加redis分布式锁失败！", e);
        }
        log.info("没有获取到分布式锁。key: {} , leaseTime: {}",key,leaseTime);
        return false;
    }
}
