package com.yangk.baseproject.common.rabbitmq.service.impl;

import com.yangk.baseproject.common.rabbitmq.domain.RabbitmqMsgDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author yangkun
 * @Date 2020/3/9
 * @Version 1.0
 * @blame yangkun
 */
@Slf4j
@Component("rabbitmqConfirmService")
public class RabbitmqConfirmServiceImpl implements ConfirmCallback {

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("消息[{}]确认成功", correlationData.getId());
        RabbitmqMsgDTO rabbitmqMsgDTO = new RabbitmqMsgDTO();
        rabbitmqMsgDTO.setMsgId(correlationData.getId());
        // 确保消息发送到mq，可在发送mq时保存数据，在这里更新数据状态为已发送，在消费者那里删除数据
        
    }
}
