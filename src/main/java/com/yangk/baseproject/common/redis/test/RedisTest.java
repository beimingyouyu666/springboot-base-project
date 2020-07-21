package com.yangk.baseproject.common.redis.test;
import com.alibaba.fastjson.JSON;
import com.yangk.baseproject.common.redis.RedisUtil;
import com.yangk.baseproject.domain.dto.ParcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 发送mq测试类
 * @Author yangkun
 * @Date 2020/6/24
 * @Version 1.0
 * @blame yangkun
 */
@RestController
@RequestMapping("testRedis")
@Slf4j
public class RedisTest {
    @Resource
    private RedisUtil redisUtil;

    @RequestMapping("setRedis")
    public void setRedis(String msg) {
        ParcelDTO parcelDTO = new ParcelDTO();
        parcelDTO.setCreateId(msg);
        parcelDTO.setCreateTime(new Date());
        parcelDTO.setFpxTrackingNo("666");
        parcelDTO.setBusinessType("啊哈哈");
        parcelDTO.setWarehouseCode("哈哈");
        parcelDTO.setWeight(new BigDecimal("1.3"));
        parcelDTO.setTotalPrice(new BigDecimal("2.3"));
//        redisUtil.set("parcel"+":"+msg, JSON.toJSONString(parcelDTO),600L);
        log.info("parcel"+":"+msg, JSON.toJSONString(parcelDTO));
        redisUtil.set("parcel"+":"+msg, JSON.toJSONString(parcelDTO));
    }

    @RequestMapping("getRedis")
    public String getRedis(String msg) {
        Object o = redisUtil.get("parcel"+":"+msg);
        ParcelDTO parcelDTO = JSON.parseObject((String) o, ParcelDTO.class);
        System.out.println(parcelDTO);
        return (String) o;
    }
}
