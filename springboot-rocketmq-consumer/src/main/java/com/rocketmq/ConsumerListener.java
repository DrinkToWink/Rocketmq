package com.rocketmq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

//消息的topic、消息的组名（从配置文件中获取的）
@RocketMQMessageListener(topic = "springboot-mq",
        consumerGroup = "${rocketmq.consumer.group}")
@Component
public class ConsumerListener implements RocketMQListener<String> {

    //这个参数s就是生产者传过来的那个消息，生产者那边已经确定了消息类型String
    public void onMessage(String s) {
        System.out.println(s);
    }
}
