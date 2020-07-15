package com.yangk.baseproject.common.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @Description zookeeper配置
 * @Author yangkun
 * @Date 2020/6/30
 * @Version 1.0
 * @blame yangkun
 */
@Component
@Data
@ConfigurationProperties(prefix="zookeeper")
public class ZookeeperProperties {

    private String addresses;

    private Integer timeout;

}
