package com.hqy.cloud.mq.api.transactional.service;

import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.common.AbstractStreamMessage;
import com.hqy.cloud.stream.core.StreamId;
import com.hqy.mq.common.lang.Constants;

/**
 * 本地事务消息， 一般与业务事务一起落库...
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/7
 */
public abstract class LocalTransactionalMessage<T> extends AbstractStreamMessage<String, T> {

    /**
     * 消息ID，唯一ID 不能为空
     */
    private String messageId;

    /**
     * 消息主题， 不能为空
     */
    private String topic;

    /**
     * 消息标签或其他标志性词
     */
    private String tags;

    /**
     * mq类型
     */
    private String mqType;

    /**
     * 消息发送时间戳
     */
    private Long messageTimestamp;

    public LocalTransactionalMessage(MessageId<String> messageId, String topic, T value) {
        super(messageId, value);
        this.topic = topic;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public MessageId<String> getId() {
        return StreamId.of(Constants.MQ_TRANSACTION_SCENE, messageId);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getMqType() {
        return mqType;
    }

    public void setMqType(String mqType) {
        this.mqType = mqType;
    }

    public Long getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(Long messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }
}
