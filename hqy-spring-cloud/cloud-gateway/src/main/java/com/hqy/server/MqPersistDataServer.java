package com.hqy.server;

import com.hqy.common.base.lang.MqConstants;
import com.hqy.mq.collector.entity.ThrottledIpBlock;
import com.hqy.util.JsonUtil;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 采用direct模式的交换机，专门用于mq采集
 * 交换机名称:coll_direct_exchange
 * 将需要持久化采集的数据 放进mq中...
 * @author qy
 * @create 2021/8/19 23:15
 */
@Component
public class MqPersistDataServer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void persistBlockIpAction(ThrottledIpBlock throttledIpBlock) {
        /**
         * 单独为设置TTL的时候 当队列的消息没有被消费而过期时
         * 消息不会放到死信队列中 而单独为QUEUE设置TTL时 消息过期将会放到死信队列里面
         * 两者都设置TTL的时候 则以最小的值为准.
         */
        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setExpiration(MqConstants.QUEUE_TTL + "");
            message.getMessageProperties().setContentEncoding("UTF-8");
            return message;
        };
        rabbitTemplate.convertAndSend(MqConstants.AMQP_COLL_EXCHANGE, "", JsonUtil.toJson(throttledIpBlock), messagePostProcessor);
    }


}
