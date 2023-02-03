package com.hqy.mq.common.server;

import cn.hutool.core.lang.UUID;

import java.util.Map;

/**
 * 消息中间件的消息.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:06
 */
public interface MqMessage {

    /**
     * 获取消息体Class.
     * @return  消息体Class
     */
    <T> Class<T> payloadType();

    /**
     * 消息体.
     * @return 无特殊情况为json数据.
     */
    String payload();

    /**
     * 获取发送消息参数
     * @return 消息参数.
     */
    default Map<String, Object> params() { return null; }

    /**
     * 消息id
     * @return String
     */
    default String messageId() { return UUID.fastUUID().toString(true);
    }

}
