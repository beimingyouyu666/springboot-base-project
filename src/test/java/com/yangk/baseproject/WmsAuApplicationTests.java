package com.yangk.baseproject;

import com.yangk.baseproject.common.constant.RabbitmqConstant;
import com.yangk.baseproject.common.rabbitmq.domain.RabbitmqMsgDTO;
import com.yangk.baseproject.common.rabbitmq.service.MsgService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WmsAuApplicationTests {

    @Resource
    private MsgService msgService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void sendMq() {
        RabbitmqMsgDTO msg = new RabbitmqMsgDTO(null, "test", RabbitmqConstant.EXCHANGE,
                RabbitmqConstant.R_TEST);
        msgService.sendMsgForJSONField(msg);
    }

}
