package com.yangk.baseproject.common.rabbitmq.test;

import com.yangk.baseproject.common.constant.RabbitmqConstant;
import com.yangk.baseproject.common.rabbitmq.domain.RabbitmqMsgDTO;
import com.yangk.baseproject.common.rabbitmq.service.MsgService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description 发送mq测试类
 * @Author yangkun
 * @Date 2020/6/24
 * @Version 1.0
 * @blame yangkun
 */
@RestController
@RequestMapping("test")
public class MqSendTest {
    @Resource
    private MsgService msgService;

    @RequestMapping("sendMq")
    public void sendMq(String msg) {
        RabbitmqMsgDTO dto = new RabbitmqMsgDTO(null, msg, RabbitmqConstant.EXCHANGE,
                RabbitmqConstant.R_TEST);
        msgService.sendMsgForJSONField(dto);
    }
}
