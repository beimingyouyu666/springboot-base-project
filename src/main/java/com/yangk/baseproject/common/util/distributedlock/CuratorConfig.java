package com.yangk.baseproject.common.util.distributedlock;

import com.yangk.baseproject.common.zookeeper.ZookeeperProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description Zookeeper工具 Curator 的配置文件
 * @Author yangkun
 * @Date 2020/7/3
 * @Version 1.0
 * @blame yangkun
 */
@Configuration
public class CuratorConfig {

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    // zk host地址
//    private String host;

    // zk自增存储node
//    private String lockPath;

    // 重试休眠时间
    private static final int SLEEP_TIME_MS = 1000;
    // 最大重试1000次
    private static final int MAX_RETRIES = 1000;
    //会话超时时间
    private static final int SESSION_TIMEOUT = 30 * 1000;
    //连接超时时间
    private static final int CONNECTION_TIMEOUT = 3 * 1000;

    private CuratorFramework curatorFramework;

    @Bean
    public CuratorFramework curatorFramework() {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zookeeperProperties.getAddresses())
                .connectionTimeoutMs(CONNECTION_TIMEOUT)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(SLEEP_TIME_MS, MAX_RETRIES))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }
}
