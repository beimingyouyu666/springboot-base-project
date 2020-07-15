package com.yangk.baseproject.common.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;


/**
 * @Description RedisCluster配置
 * @Author yangkun
 * @Date 2020/6/30
 * @Version 1.0
 * @blame yangkun
 */

@Configuration
public class RedisClusterConfig {

    @Autowired
    private RedisClusterProperties redisClusterProperties;

    @Bean
    public JedisCluster getJedisCluster() {
        String[] serverArray = redisClusterProperties.getClusterNodes().split(",");
        Set<HostAndPort> nodes = new HashSet<>();
        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim()).intValue()));
        }
        return new JedisCluster(nodes, redisClusterProperties.getCommandTimeout().intValue(), 5000, 5, redisClusterProperties.getPassword(), new GenericObjectPoolConfig());
    }


}

