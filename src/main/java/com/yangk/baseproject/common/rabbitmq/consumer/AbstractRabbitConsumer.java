package com.yangk.baseproject.common.rabbitmq.consumer;

import com.yangk.baseproject.common.constant.ConsulPropertiesKeyConstant;
import com.yangk.baseproject.common.rabbitmq.domain.RabbitmqMsgDTO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 消费者基类
 * @Author yangkun
 * @Date 2020/3/9
 * @Version 1.0
 * @blame yangkun
 */
@Slf4j
public abstract class AbstractRabbitConsumer implements ChannelAwareMessageListener {

    @Resource
    private Environment env;

    /**
    * @Description:  @RabbitListener 和 @RabbitHandler 搭配使用
     * @RabbitListener 可以标注在类上面，需配合 @RabbitHandler 注解一起使用
     *  @RabbitListener 标注在类上面表示当有收到消息的时候，就交给 @RabbitHandler的方法处理
    * @Author: yangkun
    * @Date: 2020/6/24
    * @Param:
     * @param message
     * @param channel
    * @return: void
    */
    @RabbitHandler
    @Override
    public final void onMessage(Message message, Channel channel) throws Exception {
        try {
            handMessage(new String(message.getBody(), StandardCharsets.UTF_8),
                    getCount(message.getMessageProperties()));
            // 通知消息代理把消息从队列删除
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            try {
                if (message.getMessageProperties() != null && message.getMessageProperties().getCorrelationId() != null) {
                    // 删除记录数据
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            log.error("消费MQ消息失败!" + e.getMessage(), e);
            // 返回死信队列
            if (getCount(message.getMessageProperties()) < 5) {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                if (message.getMessageProperties() != null && message.getMessageProperties().getCorrelationId() != null) {
                    RabbitmqMsgDTO rabbitmqMsgDTO = new RabbitmqMsgDTO();
                    rabbitmqMsgDTO.setMsgId(message.getMessageProperties().getCorrelationId());
                    // 更新消息记录的状态为消费失败
                }
            } else {
                // 集中处理，超过5次的不再处理
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                if (ConsulPropertiesKeyConstant.MONITOR_OFFSET_ON.equals(env.getProperty(ConsulPropertiesKeyConstant.EXCEPTION_WARN_OFFSET_KEY, ConsulPropertiesKeyConstant.MONITOR_OFFSET_ON))) {
                    // 发送钉钉消息通知
                }
            }
        } finally {

        }
    }

    protected abstract void handMessage(String msg, int toDeadCount) throws Exception;

    @SuppressWarnings("rawtypes")
    public static Integer getCount(MessageProperties messageProperties) {
        Object obj = messageProperties.getHeaders().get("x-death");
        if (obj != null) {
            List list = (ArrayList) obj;
            if (list != null && list.size() != 0) {
                Object mapObj = list.get(0);
                return Integer.valueOf(((Map) mapObj).get("count").toString());
            }
        }
        return 0;
    }
}
