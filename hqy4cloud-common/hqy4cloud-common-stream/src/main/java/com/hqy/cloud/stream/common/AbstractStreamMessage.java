package com.hqy.cloud.stream.common;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.api.StreamMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/27
 */
@Slf4j
public abstract class AbstractStreamMessage<K extends Comparable<K>, V> implements StreamMessage<K, V> {

    private final MessageId<K> messageId;
    private final V value;
    private volatile Class<V> messageType;

    public AbstractStreamMessage(MessageId<K> messageId, V value) {
        this.messageId = messageId;
        this.value = value;
    }

    @Override
    public MessageId<K> getId() {
        return this.messageId;
    }

    @Override
    public V gerValue() {
        return this.value;
    }

    public Map<String, Object> addProperty(String key, Object value) {
        Map<String, Object> attributes = getAttributes();
        attributes.put(key, value);
        return attributes;
    }

    public String getProperty(String key) {
        return getProperty(key, StrUtil.EMPTY);
    }

    public String getProperty(String key, String defaultValue) {
        Map<String, Object> attributes = getAttributes();
        if (attributes.containsKey(key)) {
            try {
                return (String) attributes.get(key);
            } catch (Exception cause) {
                log.error(cause.getMessage(), cause);
                return defaultValue;
            }
        }
        return defaultValue;
    }



}
