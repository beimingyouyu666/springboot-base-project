package com.yangk.baseproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableRabbit
@EnableFeignClients
@MapperScan("com.yangk.baseproject.mapper")
public class WmsAuApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmsAuApplication.class, args);
    }

}
