package com.rocketmq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//加上@RunWith注解
@RunWith(SpringRunner.class)
//传入启动类
@SpringBootTest(classes = {MqProducerApplication.class})
public class ProduerTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void test(){
        //springboot-mq相当于topic
        rocketMQTemplate.convertAndSend("springboot-mq","hello springboot rocketmq");
    }
}
