package com.yangk.baseproject.common.rabbitmq.test;

import com.yangk.baseproject.common.constant.RabbitmqConstant;
import com.yangk.baseproject.common.rabbitmq.consumer.AbstractRabbitConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description mq消费测试类1
 * @Author yangkun
 * @Date 2020/6/24
 * @Version 1.0
 * @blame yangkun
 */
@Component
@Slf4j
@RabbitListener(queues = RabbitmqConstant.Q_TEST1, containerFactory = "singleRabbitListenerContainerFactory", admin = "rabbitAdmin")
public class MqConsumerTest1 extends AbstractRabbitConsumer {

    @Override
    protected void handMessage(String msg, int toDeadCount) throws Exception {
        log.info("test1接收到队列的消息:"+msg);
    }
}
