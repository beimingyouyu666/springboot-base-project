package com.yangk.baseproject.common.rabbitmq.service.impl;

import com.alibaba.fastjson.JSON;
import com.yangk.baseproject.common.rabbitmq.domain.BaseMsgDTO;
import com.yangk.baseproject.common.rabbitmq.domain.RabbitmqMsgDTO;
import com.yangk.baseproject.common.rabbitmq.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @Description
 * @Author yangkun
 * @Date 2020/3/9
 * @Version 1.0
 * @blame yangkun
 */

@Slf4j
@Service("rabbitmqService")
public class RabbitmqServiceImpl implements MsgService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendMsgForJSONField(BaseMsgDTO baseMsgDTO) {
        // 转换成相对应的实体类，并检查消息id
        RabbitmqMsgDTO msg = (RabbitmqMsgDTO) baseMsgDTO;
        if (StringUtils.isBlank(msg.getMsgId())) {
            msg.setMsgId(UUID.randomUUID().toString());
        }

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(msg.getMsgId());
        messageProperties.setCorrelationId(msg.getMsgId());
        // 发送前先保存消息
        try {
            rabbitTemplate.convertAndSend(msg.getExchageName(), msg.getRoutingKey(),
                    new Message(JSON.toJSONString(msg.getData()).getBytes(StandardCharsets.UTF_8), messageProperties),
                    new CorrelationData(messageProperties.getMessageId()));
        } catch (Exception e) {
            log.error("Rabbitmq消息发送异常", e);
            // 发送失败，修改消息记录状态为发送失败
        }
    }

}
