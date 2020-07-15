package com.yangk.baseproject.common.util.distributedlock.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Description 测试curator实现分布式锁
 * @Author yangkun
 * @Date 2020/6/24
 * @Version 1.0
 * @blame yangkun
 */
@RestController
@RequestMapping("testCurator")
@Slf4j
public class CuratorTest {

    @Autowired
    private CuratorFramework curatorFramework;

    @RequestMapping("getCurator")
    public String getCurator(String lockPath) throws Exception {
        String s = "/" + lockPath;
        log.info("测试zk加锁:{}",s);
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, s);
        boolean acquire = lock.acquire(5, TimeUnit.SECONDS);
//        Thread.sleep(5000);
//        log.info("测试zk释放锁:{}",s);
//        lock.release();
        return "获取锁结果："+String.valueOf(acquire);
    }

    @RequestMapping("releaseCurator")
    public void releaseCurator(String lockPath) throws Exception {
        String s = "/" + lockPath;
        log.info("测试zk释放锁:{}",s);
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, s);
        lock.release();
    }
}
