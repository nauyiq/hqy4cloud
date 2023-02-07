package com.hqy.mq.rabbitmq.lang;

import com.hqy.mq.common.MessageModel;

/**
 * RabbitmqMessageModel.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/7 14:54
 */
public interface RabbitmqMessageModel extends MessageModel {

    /**
     * 获取交换机名
     * @return 交换机
     */
    String exchange();

    /**
     * 获取routingKey.
     * @return routingKey.
     */
    String routingKey();



}
