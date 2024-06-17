package com.hqy.cloud.netty.mqtt.protocol;

import java.io.Serializable;

/**
 * mqtt报文由三部分组成， 固定的header（固定头必须有） + 可变的header + payload
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/14
 */
public class MqttProtocol implements Serializable {

    private String messageId;
    private String topic;
    private Object message;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
