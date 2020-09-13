package com.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerAppclication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerAppclication.class);
        System.out.println("消费者启动成功");
    }
}
