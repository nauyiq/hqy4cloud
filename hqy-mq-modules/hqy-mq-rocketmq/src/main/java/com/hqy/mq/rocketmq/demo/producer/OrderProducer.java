package com.hqy.mq.rocketmq.demo.producer;

import com.hqy.mq.rocketmq.demo.OrderStep;
import com.hqy.util.JsonUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 顺序消息生产者
 *
 *  顺序消息需要Producer和Consumer都保证顺序。Producer需要保证消息被路由到正确的分区，消息需要保证每个分区的数据只有一个线程消息，那么就会有一些缺陷：
 *
 * 发送顺序消息无法利用集群的Failover特性，因为不能更换MessageQueue进行重试
 * 因为发送的路由策略导致的热点问题，可能某一些MessageQueue的数据量特别大
 * 消费的并行读依赖于分区数量
 * 消费失败时无法跳过
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/18 9:34
 */
public class OrderProducer {

    public static void main(String[] args) throws Exception {

        //创建消息生产者对象 构造指定生产者组
        DefaultMQProducer producer = new DefaultMQProducer("hqy-rocketmq-producer");
        //指定nameserver
        producer.setNamesrvAddr("47.106.168.100:9876");
        //启动生产者
        producer.start();

        List<OrderStep> orderStepList = OrderStep.orderSteps();
        //发送消息
        int i = 0;
        for (OrderStep order : orderStepList) {
            Message message = new Message("demo-order-topic", "order", i + "", JsonUtil.toJson(order).getBytes(StandardCharsets.UTF_8));
            /*
                消费者顺序发消息, 讲消息顺序发到某个QUEUE里面
                param1：消息对象 Message
                param2: 消息选择器 MessageQueueSelector
                param3: 选择队列的业务标识, 这里举例使用订单id
             */
            producer.send(message, new MessageQueueSelector() {
                /**
                 * @param mqs 队列集合
                 * @param msg 消息对象
                 * @param arg 业务标识id
                 * @return
                 */
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Long orderId = (Long) arg;
                    return mqs.get(Math.abs(orderId.hashCode()) % mqs.size());
                }
            }, order.id);

            ++i;
        }

        producer.shutdown();

    }

}
