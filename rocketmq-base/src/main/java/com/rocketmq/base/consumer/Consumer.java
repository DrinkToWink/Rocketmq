package com.rocketmq.base.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import java.util.List;

/**
 * 消息的接受者
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        //1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        //2.指定Nameserver地址
        consumer.setNamesrvAddr("192.168.1.120:9876;192.168.1.129:9876");
        //3.订阅主题Topic和Tag
        //consumer.subscribe("base", "*");
        //consumer.subscribe("base", "Tag2");
        consumer.subscribe("springboot-mq","Tag1");

        //设定消费模式：负载均衡|广播模式(如果不设置，默认为负载均衡模式)
        consumer.setMessageModel(MessageModel.BROADCASTING);

        //4.设置回调函数，处理消息,消费者线程阻塞，一直在这监听
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            //接受消息内容，当消息队列中有自己订阅的消息时，会触发这个方法，这个集合是消息对象的集合
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println("consumeThread=" + Thread.currentThread().getName() + "," + new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        //5.启动消费者consumer
        consumer.start();
    }
}
