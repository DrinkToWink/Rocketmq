package com.rocketmq.order;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import java.util.List;

public class Consumer {
    public static void main(String[] args) throws MQClientException {
        //1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        //2.指定Nameserver地址
        consumer.setNamesrvAddr("192.168.1.120:9876;192.168.1.129:9876");
        //3.订阅主题Topic和Tag
        consumer.subscribe("OrderTopic", "*");

        //4.注册消息监听器，MessageListenerOrderly作用：每一个队列的数据都由同一个线程去取（这个地方有问题）
        //出现bug，第一次消费消息，三个线程，第二次发送同样消息一下子出来10个线程。猜测：可能不支持相同数据重复消费
        consumer.registerMessageListener(new MessageListenerOrderly() {
            //这个集合是消息对象集合
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println("线程名称：【" + Thread.currentThread().getName() + "】:" + new String(msg.getBody()));
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });

        //5.启动消费者
        consumer.start();

        System.out.println("消费者启动");

    }
}
