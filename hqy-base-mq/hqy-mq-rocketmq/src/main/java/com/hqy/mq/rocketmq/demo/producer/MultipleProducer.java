package com.hqy.mq.rocketmq.demo.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量消息生产者
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/18 15:56
 */
public class MultipleProducer {

    public static void main(String[] args) throws Exception {
        //创建消息生产者对象 构造指定生产者组
        DefaultMQProducer producer = new DefaultMQProducer("hqy-rocketmq-producer");
        //指定nameserver
        producer.setNamesrvAddr("47.106.168.100:9876");
        //启动生产者
        producer.start();

        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            //构建消息对象
            Message message = new Message("rocket-topic-test", "multiple",
                    ("Hello World" + i).getBytes(StandardCharsets.UTF_8));
            /*
               发消息 sync
               延迟消息的等级对应 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
             */
            messages.add(message);
        }
        SendResult sendResult = producer.send(messages);
        System.out.println("发送结果" + sendResult);
        System.out.println("发送状态:" + sendResult.getSendStatus());

        producer.shutdown();
    }

}
