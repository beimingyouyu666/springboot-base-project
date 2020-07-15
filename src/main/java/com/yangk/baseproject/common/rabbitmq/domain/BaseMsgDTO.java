package com.yangk.baseproject.common.rabbitmq.domain;

import lombok.Data;

@Data
public abstract class BaseMsgDTO {

    protected String msgId;// 消息id

    protected Object data;// 业务数据

}
