package com.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//springboot程序的入口
@SpringBootApplication
public class MqProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MqProducerApplication.class);
        System.out.println("生产者启动成功");
    }

}
