package com.yangk.baseproject.common.rabbitmq.service;

import com.yangk.baseproject.common.rabbitmq.domain.BaseMsgDTO;

/**
 * @Description 发送mq消息
 * @Author yangkun
 * @Date 2020/3/9
 * @Version 1.0
 * @blame yangkun
 */
public interface MsgService {

    void sendMsgForJSONField(BaseMsgDTO baseMsgDTO);
}
