package com.yangk.baseproject.common.config;

import com.yangk.baseproject.common.constant.ConsulPropertiesKeyConstant;
import com.yangk.baseproject.common.exception.BusinessRuntimeException;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Description redisson配置
 * @Author yangkun
 * @Date 2020/6/30
 * @Version 1.0
 * @blame yangkun
 */
@Configuration
public class RedissonConfig {

    @Autowired
    private Environment env;

    /**
    * @Description:
     *  redisson所有指令都通过lua脚本执行，redis支持lua脚本原子性执行
     * redisson设置一个key的默认过期时间为30s,如果某个客户端持有一个锁超过了30s怎么办？
     * redisson中有一个watchdog的概念，翻译过来就是看门狗，它会在你获取锁之后，每隔10秒帮你把key的超时时间设为30s
     * 这样的话，就算一直持有锁也不会出现key过期了，其他线程获取到锁的问题了。
     * redisson的“看门狗”逻辑保证了没有死锁发生。
     * (如果机器宕机了，看门狗也就没了。此时就不会延长key的过期时间，到了30s之后就会自动过期了，其他线程可以获取到锁)
     *
    * @Author: yangkun
    * @Date: 2020/6/30
    * @Param:
     * @param
    * @return: org.redisson.api.RedissonClient
    */
    @Bean
    public RedissonClient redissonClient() {
        String property = env.getProperty(ConsulPropertiesKeyConstant.REDIS_CLUSTER_NODES);
        if (StringUtils.isBlank(property)) {
            throw BusinessRuntimeException.buildBusinessRuntimeException("spring.redis.cluster.nodes is blank");
        }
        String[] split = property.split(",");
        for (int i = 0; i < split.length; i++) {
            split[i] = "redis://" + split[i];
        }
        Config config = new Config();
        config.useClusterServers().addNodeAddress(split);
        return Redisson.create(config);
    }
}
