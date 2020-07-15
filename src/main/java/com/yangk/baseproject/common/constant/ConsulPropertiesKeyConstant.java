
package com.yangk.baseproject.common.constant;

/**
 * 定义在consul配置的key
 * @author
 * @date 2020年4月14日
 */
public class ConsulPropertiesKeyConstant {

    /**
     * 监控开关：关闭
     */
    public static final String MONITOR_OFFSET_OFF = "OFF";
    /**
     * 监控开关：开启
     */
    public static final String MONITOR_OFFSET_ON = "ON";
    /**
     * redis 集群地址配置
     */
    public static final String REDIS_CLUSTER_NODES = "spring.redis.cluster.nodes";

    /**
     * 异常预警开关
     */
    public static final String EXCEPTION_WARN_OFFSET_KEY = "opp.exception.warn.offset";
    
}
