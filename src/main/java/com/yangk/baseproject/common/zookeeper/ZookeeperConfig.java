package com.yangk.baseproject.common.zookeeper;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

/**
 * @Description
 * @Author yangkun
 * @Date 2020/7/1
 * @Version 1.0
 * @blame yangkun
 */

@Configuration
public class ZookeeperConfig {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperConfig.class);

    @Autowired
    private ZookeeperProperties zookeeperProperties;


    @Bean(name = "zooKeeperClient")
    public ZooKeeper zooKeeperClient(){
        ZooKeeper zooKeeperClient=null;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            //连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后，直接调用后续代码
            //  可指定多台服务地址 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
            zooKeeperClient = new ZooKeeper(zookeeperProperties.getAddresses(), zookeeperProperties.getTimeout(), event -> {
                if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                    //如果收到了服务端的响应事件,连接成功
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            logger.info("【初始化ZooKeeper连接状态....】={}",zooKeeperClient.getState());
        }catch (Exception e){
            logger.error("初始化ZooKeeper连接异常....】={}",e);
        }
        return  zooKeeperClient;
    }


}


