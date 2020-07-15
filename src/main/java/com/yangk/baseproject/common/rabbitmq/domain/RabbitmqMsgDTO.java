package com.yangk.baseproject.common.rabbitmq.domain;

import lombok.Data;

/**
 * @Description mq消息实体
 * @Author yangkun
 * @Date 2020/3/9
 * @Version 1.0
 * @blame yangkun
 */
@Data
public class RabbitmqMsgDTO extends BaseMsgDTO{

    /**
     消息id
     */
    private String msgId;

    /**
     业务数据
     */
    private Object data;

    /**
     交换器名称
     */
    private String exchageName;

    /**
     路由key
     */
    private String routingKey;

    public RabbitmqMsgDTO() {
    }

    public RabbitmqMsgDTO(String msgId, Object data, String exchageName, String routingKey) {
        this.msgId = msgId;
        this.data = data;
        this.exchageName = exchageName;
        this.routingKey = routingKey;
    }
}
