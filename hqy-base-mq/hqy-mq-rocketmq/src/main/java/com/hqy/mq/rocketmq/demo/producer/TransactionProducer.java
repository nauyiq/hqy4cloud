package com.hqy.mq.rocketmq.demo.producer;

import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/18 16:16
 */
public class TransactionProducer {

    public static void main(String[] args) throws Exception {
        //创建消息生产者对象 构造指定生产者组
        TransactionMQProducer producer = new TransactionMQProducer("hqy-rocketmq-producer");
        //指定nameserver
        producer.setNamesrvAddr("47.106.168.100:9876");

        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                String tags = msg.getTags();
                if (tags.equals("transaction")) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                System.out.println("消息tag：" + msg.getTags());
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });



        //启动生产者
        producer.start();

        for (int i = 0; i < 10; i++) {
            //构建消息对象
            Message message = new Message("rocket-topic-test", "transaction",
                    ("Hello World" + i).getBytes(StandardCharsets.UTF_8));
            //发消息 sync
            SendResult result = producer.sendMessageInTransaction(message, null);
            SendStatus sendStatus = result.getSendStatus();
            System.out.println("发送结果" + result);
            System.out.println("发送状态:" + sendStatus);
            TimeUnit.SECONDS.sleep(1);
        }

//        producer.shutdown();


    }
}
