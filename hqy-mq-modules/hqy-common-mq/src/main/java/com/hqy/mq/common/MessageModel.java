package com.hqy.mq.common;

import cn.hutool.core.lang.UUID;
import com.hqy.base.common.support.Parameters;
import com.hqy.mq.common.lang.enums.MessageType;

/**
 * 消息中间件的消息.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:06
 */
public interface MessageModel {

    /**
     * 获取消息体Class.
     * @return  消息体Class
     */
    <T> Class<T> payloadClass();

    /**
     * 消息类型 默认为异步
     * @return MessageType.
     */
    default MessageType messageType() {
        return MessageType.ASYNC;
    }

    /**
     * json消息体.
     * @return 无特殊情况为json数据.
     */
    String payload();

    /**
     * 获取发送消息参数
     * @return 消息参数.
     */
    default Parameters parameters() { return null; }

    /**
     * 消息id
     * @return String
     */
    default String messageId() { return UUID.fastUUID().toString(true);
    }

}
