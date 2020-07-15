package com.yangk.baseproject.common.util.distributedlock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author yangkun
 * @Date 2020/7/3
 * @Version 1.0
 * @blame yangkun
 */
public class ZkLockWithCuratorTemplateLock {
    // zk host地址
    private String host = "localhost";

    // zk自增存储node
    private String lockPath = "/curatorLock";

    // 重试休眠时间
    private static final int SLEEP_TIME_MS = 1000;
    // 最大重试1000次
    private static final int MAX_RETRIES = 1000;
    //会话超时时间
    private static final int SESSION_TIMEOUT = 30 * 1000;
    //连接超时时间
    private static final int CONNECTION_TIMEOUT = 3 * 1000;
    //curator核心操作类
    private CuratorFramework curatorFramework;

    // 共享可重入锁（Shared Reentrant Lock）：全局同步锁，同一时间不会有两个客户端持有一个锁
    InterProcessMutex lock;

    public ZkLockWithCuratorTemplateLock() {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(host)
                .connectionTimeoutMs(CONNECTION_TIMEOUT)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(SLEEP_TIME_MS, MAX_RETRIES))
                .build();
        curatorFramework.start();
        lock = new InterProcessMutex(curatorFramework, lockPath);
    }

    public void getLock() throws Exception {
        //5s后超时释放锁
        lock.acquire(5, TimeUnit.SECONDS);
    }

    public void unlock() throws Exception {
        lock.release();
    }
}
