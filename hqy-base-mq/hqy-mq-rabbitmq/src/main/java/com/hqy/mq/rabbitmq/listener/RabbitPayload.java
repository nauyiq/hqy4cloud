package com.hqy.mq.rabbitmq.listener;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * rabbit consumer listener payload.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:06
 */
public abstract class RabbitPayload implements Serializable {

    /**
     * message id.
     */
    public transient String messageId;

    /**
     * initialize message id
     */
    public abstract void setMessageId();


    public String getMessageId() {
        if (StringUtils.isBlank(messageId)) {
            setMessageId();
        }
        return messageId;
    }



}
