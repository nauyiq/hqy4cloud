package com.hqy.cloud.mq.api.transactional.service;

import com.hqy.cloud.mq.api.transactional.common.MqTransactionalException;

/**
 * mq事务消息service，封装常用的mq消息逻辑
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/7
 */
public interface MqTransactionalService {

    /**
     * 添加事务消息到本地事务表中，并且发送该事务消息到mq。
     * 需要注意的话，采用本地消息表作为分布式事务方案时，事务的时长肯定会拉长，因为在事务中进行了外部调用（发消息到mq）
     * @param message 事务消息
     * @throws MqTransactionalException 抛出异常
     */
    <T> void saveAndSendLocalMessage(LocalTransactionalMessage<T> message) throws MqTransactionalException;


}
