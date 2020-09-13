package com.rocketmq.order;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import java.util.List;

public class Producer {

    public static void main(String[] args) throws Exception {
        //1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("192.168.1.120:9876;192.168.1.129:9876");
        //3.启动producer
        producer.start();
        //构建消息集合,集合里面有多个消息对象
        List<OrderStep> orderSteps = OrderStep.buildOrders();
        //遍历orderSteps集合，发送消息
        for (int i = 0; i < orderSteps.size(); i++) {
            //将要发送的数据对象象变成字符串
            String body = orderSteps.get(i) + "";
            //创建消息对象，数据对象在发送的时候要转化成字节数组
            Message message = new Message("OrderTopic", "Order", "i" + i, body.getBytes());
            /**
             * 参数一：消息对象
             * 参数二：消息队列的选择器
             * 参数三：选择队列的业务标识（订单ID），通过这个把同一个业务放在一个队列中
             * 相同的业务只能放在一个队列中，但是，一个队列可以放多个不同业务
             */
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                /**
                 * @param mqs：队列集合
                 * @param msg：消息对象，message会穿给msg
                 * @param arg：业务标识的参数,orderSteps.get(i).getOrderId()的值为传递给arg
                 * @return 返回一个队列
                 */
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    //因为是用Object类型接收的，所以需要转化为
                    long orderId =(long)arg;
                    //对队列取余，获取取余结果的值
                    long index = orderId % mqs.size();
                    //获取该索引的队列，并返回
                    return mqs.get((int) index);
                }
            }, orderSteps.get(i).getOrderId());

            System.out.println("发送结果：" + sendResult);
        }
        producer.shutdown();
    }

}
