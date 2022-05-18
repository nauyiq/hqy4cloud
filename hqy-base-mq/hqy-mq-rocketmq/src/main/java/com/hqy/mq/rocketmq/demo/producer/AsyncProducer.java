package com.hqy.mq.rocketmq.demo.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 异步发消息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/17 15:23
 */
public class AsyncProducer {

    public static void main(String[] args) throws MQClientException, MQBrokerException, RemotingException, InterruptedException {

        //创建消息生产者对象 构造指定生产者组
        DefaultMQProducer producer = new DefaultMQProducer("hqy-rocketmq-producer");
        //指定nameserver
        producer.setNamesrvAddr("47.106.168.100:9876");
        //启动生产者
        producer.start();
        for (int i = 0; i < 10; i++) {
            //构建消息对象
            Message message = new Message("rocket-topic-test", "test",
                    ("Hello World" + i).getBytes(StandardCharsets.UTF_8));
            //发消息 async
             producer.send(message, new SendCallback() {
                 @Override
                 public void onSuccess(SendResult sendResult) {
                     SendStatus status = sendResult.getSendStatus();
                     System.out.println("发送结果" + sendResult);
                     System.out.println("发送状态:" + status);

                 }

                 @Override
                 public void onException(Throwable e) {
                     System.out.println(e.getMessage());
                 }
             });

            TimeUnit.SECONDS.sleep(1);
        }


        producer.shutdown();

    }



}
