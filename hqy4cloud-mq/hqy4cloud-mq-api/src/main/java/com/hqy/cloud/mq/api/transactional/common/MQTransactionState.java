package com.hqy.cloud.mq.api.transactional.common;

/**
 * MQ事务状态
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/7
 */
public enum MQTransactionState {

    /**
     * 发送mq消息失败， 一般不会存在该状态，因为如果消息发送超时或者失败则应该回滚事务。
     */
    SEND_FAILED(0),

    /**
     * 发送MQ消息成功, 等待事务消息被消费
     */
    WAITING(1),

    /**
     * 事务消息已经被确认，即业务消费成功
     */
    CONFIRM(2),

    /**
     * 消费超时，即mq消息一直没有被订阅者使用，应该进行业务报警，防止mq消息堆积。
     */
    TIMEOUT(3),


    ;

    final Integer state;

    MQTransactionState(Integer state) {
        this.state = state;
    }



}
