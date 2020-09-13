package com.rocketmq.transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.TimeUnit;

/**
 * 发送同步消息
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        //1.创建消息生产者producer，并制定生产者组名
        TransactionMQProducer producer = new TransactionMQProducer("group5");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("192.168.1.120:9876;192.168.1.129:9876");

        //添加事务监听器，当broker接收到消息时，会执行这个方法，这个方法是在生产者中执行的
        producer.setTransactionListener(new TransactionListener() {
            /**
             * 在该方法中执行本地事务
             * @param msg：要发送的消息对象
             * @param arg ：
             * @return
             */
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

                //如果消息的tag为TAGA，进行事务的提交
                if (StringUtils.equals("TAGA", msg.getTags())) {
                    return LocalTransactionState.COMMIT_MESSAGE;

                    //如果消息的tag为TAGB，进行事务的回滚
                    //事务回滚时为啥会触发checkLocalTransaction方法？有bug
                } else if (StringUtils.equals("TAGB", msg.getTags())) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;

                    //如果消息的tag为TAGC，不做处理，会触发checkLocalTransaction方法
                } else if (StringUtils.equals("TAGC", msg.getTags())) {
                    return LocalTransactionState.UNKNOW;
                }

                //不符合上面三种类型，也不做任何处理，会触发checkLocalTransaction方法
                return LocalTransactionState.UNKNOW;
            }

            /**
             * 该方法是MQ进行消息事务状态回查
             * @param msg
             * @return
             * 当没有进行消息的事务的说明时也就是LocalTransactionState.UNKNOW，会在broker中执这个方法（猜的）
             */
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                System.out.println("消息的Tag:" + msg.getTags());
                //进行消息的提交
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        //3.启动producer
        producer.start();

        String[] tags = {"TAGA", "TAGB", "TAGC"};

        for (int i = 0; i < 3; i++) {
            //4.创建消息对象，指定主题Topic、Tag和消息体
            /**
             * 参数一：消息主题Topic
             * 参数二：消息Tag
             * 参数三：消息内容
             */
            Message msg = new Message("TransactionTopic", tags[i], ("Hello World" + i).getBytes());
            //5.发送消息，第二个参数设置的事务的范围，如果设置为null代表对整个producer发送的消息都进行事务控制
            //当然也可以对单个消息进行事务控制
            SendResult result = producer.sendMessageInTransaction(msg, null);
            //发送状态
            SendStatus status = result.getSendStatus();

            System.out.println("发送结果:" + result);

            //线程睡1秒
            TimeUnit.SECONDS.sleep(1);
        }

        //6.关闭生产者producer，关闭的话，好像就无法进行方法的回调，这里就不关了
        //producer.shutdown();
    }
}
