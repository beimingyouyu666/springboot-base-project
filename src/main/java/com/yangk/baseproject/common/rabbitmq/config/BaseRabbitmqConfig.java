package com.yangk.baseproject.common.rabbitmq.config;

import com.yangk.baseproject.common.rabbitmq.converter.FastJsonMessageConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @Description mq基础配置
 * @Author yangkun
 * @Date 2020/3/9
 * @Version 1.0
 * @blame yangkun
 */
@Configuration
@ImportResource(locations = { "classpath:rabbitmq/spring-rabbitmq.xml" })
public class BaseRabbitmqConfig {

    @Resource
    private Environment env;

    @Bean(name = "fastjsonConverter")
    public FastJsonMessageConverter fastjsonConverter() {
        return new FastJsonMessageConverter();
    }

    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            @Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 设置消费者使用手动确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(new FastJsonMessageConverter());
        try {
            String currentConsumers = env.getProperty("spring.rabbitmq.currentConsumers");
            // 设置消费者数量
            factory.setConcurrentConsumers(
                    StringUtils.isBlank(currentConsumers) ? 5 : Integer.valueOf(currentConsumers));
        } catch (Exception e) {
            factory.setConcurrentConsumers(5);
        }
        try {
            String prefetchCount = env.getProperty("spring.rabbitmq.prefetchCount");
            // 设置一次从mq中取多少条数据
            factory.setPrefetchCount(StringUtils.isBlank(prefetchCount) ? 10 : Integer.valueOf(prefetchCount));
        } catch (Exception e) {
            factory.setPrefetchCount(10);
        }
        return factory;
    }

    @Bean(name = "singleRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory singleRabbitListenerContainerFactory(
            @Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(new FastJsonMessageConverter());
        // 设置只有一个消费者
        factory.setConcurrentConsumers(1);
        try {
            String prefetchCount = env.getProperty("spring.rabbitmq.prefetchCount");
            factory.setPrefetchCount(StringUtils.isBlank(prefetchCount) ? 1 : Integer.valueOf(prefetchCount));
        } catch (Exception e) {
            factory.setPrefetchCount(10);
        }
        return factory;
    }

    @Bean(name = "muiltRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory muiltRabbitListenerContainerFactory(
            @Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(new FastJsonMessageConverter());
        // 设置20个消费者
        factory.setConcurrentConsumers(20);
        try {
            String prefetchCount = env.getProperty("spring.rabbitmq.prefetchCount");
            factory.setPrefetchCount(StringUtils.isBlank(prefetchCount) ? 10 : Integer.valueOf(prefetchCount));
        } catch (Exception e) {
            factory.setPrefetchCount(10);
        }
        return factory;
    }
}
