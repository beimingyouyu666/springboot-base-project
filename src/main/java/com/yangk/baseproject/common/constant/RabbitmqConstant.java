
package com.yangk.baseproject.common.constant;

/**
 * 
 * @author
 * @date 2020年4月11日
 */
public final class RabbitmqConstant {

    /**
     * 统一使用的交换器定义
     */
    public static final String EXCHANGE = "springboot-base-project-rabbit-x-exchange";
    public static final String EXCHANGE_DEAD = "springboot-base-project-rabbit-x-exchange-dead";

    /**
     * 测试队列的routekey及queue,routekey相同，一条消息会发送到两个队列
     */
    public static final String R_TEST = "springboot-base-project-rabbit-r-test";

    public static final String Q_TEST1 = "springboot-base-project-rabbit-q-test1";

    public static final String Q_TEST2 = "springboot-base-project-rabbit-q-test2";


}
