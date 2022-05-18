package com.hqy.mq.rocketmq.demo.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 顺序消费者
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/18 10:24
 */
public class OrderConsumer {

    public static void main(String[] args) throws Exception {
        //创建消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("hqy-rocketmq-consumer");
        //指定nameserver
        consumer.setNamesrvAddr("47.106.168.100:9876");
        //订阅主题 tag
        consumer.subscribe("demo-order-topic", "*");
        //注册顺序监听消费器
        consumer.registerMessageListener(new MessageListenerOrderly() {
            /**
             * @param msgs    同个队列的消息列表
             * @param context 上下文
             * @return
             */
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {

                for (MessageExt msg : msgs) {
                    String json = new String(msg.getBody());
                    System.out.println(Thread.currentThread().getName() +  " 消费消息, data: " + json);
                }

                return ConsumeOrderlyStatus.SUCCESS;
            }
        });

        consumer.start();

        System.out.printf("Consumer Started.%n");
    }


}
