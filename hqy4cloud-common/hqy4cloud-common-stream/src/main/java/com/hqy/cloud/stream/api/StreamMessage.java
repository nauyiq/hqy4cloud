package com.hqy.cloud.stream.api;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 流消息对象, 包含消息体和消息id、消息上下文参数等。
 * 参数说明: K = 消息ID的class type
 *         V = 消息体的class type
 *         R = 消息执行某个操作后的回调class type
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/18
 */
public interface StreamMessage<K extends Comparable<K>, V> {

    /**
     * 获取消息id
     * @return 消息id
     */
     MessageId<K> getId();

    /**
     * 获取消息内容/值
     * @return 消息内容/值
     */
    V gerValue();

    /**
     * 获取消息的主题，用于表示消息属于哪个分组，哪个主题
     * @return 消息的主题
     */
    String getTopic();


    /**
     * 获取消息属性等
     * @return 消息属性
     */
    default Map<String, Object> getAttributes() {
        return Maps.newHashMapWithExpectedSize(4);
    }


}
