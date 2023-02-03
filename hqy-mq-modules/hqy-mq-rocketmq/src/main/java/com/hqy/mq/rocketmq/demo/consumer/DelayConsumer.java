package com.hqy.mq.rocketmq.demo.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * 延迟消息消费者
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/18 11:27
 */
public class DelayConsumer {

    public static void main(String[] args) throws Exception {
        //创建消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("hqy-rocketmq-consumer");
        //指定nameserver
        consumer.setNamesrvAddr("47.106.168.100:9876");
        //订阅主题 tag
        consumer.subscribe("rocket-topic-test", "delay");

        //广播模式 MessageModel.BROADCASTING
        consumer.setMessageModel(MessageModel.BROADCASTING);
        //设置回调 处理消息
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msg);
                System.out.println("body:" + new String(msg.getBody()));
                //msg.getStoreTimestamp() 消息的存储时间
                System.out.println("系统时间" + System.currentTimeMillis());
                System.out.println("存储时间" + msg.getStoreTimestamp());
                System.out.println("延迟时间:" + (System.currentTimeMillis() - msg.getStoreTimestamp()));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        //Launch the consumer instance.
        consumer.start();

        System.out.printf("Consumer Started.%n");

    }

}
