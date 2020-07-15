package com.yangk.baseproject.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @Description redis cluster配置
 * @Author yangkun
 * @Date 2020/6/30
 * @Version 1.0
 * @blame yangkun
 */
@Component
@Data
@ConfigurationProperties(prefix="spring.redis.cluster.cache")
public class RedisClusterProperties {

    private String clusterNodes;

    private Integer commandTimeout;

    private String password;

}
