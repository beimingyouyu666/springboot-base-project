package com.yangk.baseproject.common.rabbitmq.test;

import com.yangk.baseproject.common.constant.RabbitmqConstant;
import com.yangk.baseproject.common.rabbitmq.consumer.AbstractRabbitConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description mq消费测试类2
 * @Author yangkun
 * @Date 2020/6/24
 * @Version 1.0
 * @blame yangkun
 */
@Component
@Slf4j
@RabbitListener(queues = RabbitmqConstant.Q_TEST2, containerFactory = "singleRabbitListenerContainerFactory", admin =
        "rabbitAdmin")
public class MqConsumerTest2 extends AbstractRabbitConsumer {

    @Override
    protected void handMessage(String msg, int toDeadCount) throws Exception {
        log.info("test2接收到队列的消息:" + msg);
        log.info("test2接收到队列的消息:" + msg + "为了让其进入死信，人工报错代码");
        int i = 1 / 0;
    }
}
