package com.hqy.cloud.mq.api.transactional.service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/7
 */
public interface MqMessageOperations {

    /**
     * 保存事务消息
     * @param message 事务消息
     * @return        是否保存成功
     */
    <T> boolean saveMqMessage(LocalTransactionalMessage<T> message);

    /**
     * 提获取本地消息事务表中未确认的消息
     * @return 未确认的消息
     */
    <T> List<LocalTransactionalMessage<T>> queryUnConfirmMessages();

    /**
     * 消息确认，修改本地消息表
     * @param messageId 消息id
     */
    void confirmMessage(String messageId);
}
