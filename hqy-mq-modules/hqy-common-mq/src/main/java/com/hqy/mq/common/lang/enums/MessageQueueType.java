package com.hqy.mq.common.lang.enums;

import com.hqy.mq.common.transaction.stategy.AbstractKafkaDeliveryMessageAdaptor;
import com.hqy.mq.common.transaction.stategy.AbstractRabbitMqDeliveryMessageAdaptor;
import com.hqy.mq.common.transaction.stategy.DeliveryMessageStrategy;

/**
 * 消息队列枚举
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/12 15:48
 */
public enum MessageQueueType {

    /**
     * rabbitmq
     */
    RABBITMQ(AbstractRabbitMqDeliveryMessageAdaptor.class),

    /**
     * kafka
     */
    KAFKA(AbstractKafkaDeliveryMessageAdaptor.class)


    ;

    /**
     * 投递消息策略
     */
    public final Class<? extends DeliveryMessageStrategy> strategyClass;

    MessageQueueType(Class<? extends DeliveryMessageStrategy> strategyClass) {
        this.strategyClass = strategyClass;
    }
}
