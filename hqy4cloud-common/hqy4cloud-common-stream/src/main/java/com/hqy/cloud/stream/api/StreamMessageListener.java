package com.hqy.cloud.stream.api;

import java.util.Collection;

/**
 * 消息监听器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/7
 */
public interface StreamMessageListener {

    /**
     * 获取监听消息的class类型
     * @return 消息的类型
     */
    <T> Class<T> getMessageType();

    /**
     * 收到消息时的具体业务逻辑
     * @param messages 收到的消息
     */
    <T> void onMessage(Collection<T> messages);




}
