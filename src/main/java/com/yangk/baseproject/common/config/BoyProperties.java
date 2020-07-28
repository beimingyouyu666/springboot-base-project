package com.yangk.baseproject.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description 测试配置
 * @Author yangkun
 * @Date 2020/7/27
 * @Version 1.0
 * @blame yangkun
 */
@Component
@Data
@ConfigurationProperties(prefix="boy")
public class BoyProperties {

    String name;

    String age;
}
