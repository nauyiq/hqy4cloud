package com.hqy.mq.common.bind;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqy.mq.common.lang.enums.MessageType;
import com.hqy.util.JsonUtil;

/**
 * 消息中间件的消息.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:06
 */
public interface MessageModel {


    /**
     * 消息类型 默认为异步
     * @return MessageType.
     */
    default MessageType messageType() {
        return MessageType.SYNC;
    }

    /**
     * 获取发送消息参数
     * @return 消息参数.
     */
    @JsonIgnore
    MessageParams getParameters();

    /**
     * 获取json消息体.
     * @return json消息体.
     */
    default String jsonPayload() {
        return JsonUtil.toJson(this);
    }

    /**
     * 消息id
     * @return String
     */
    default String messageId() { return UUID.fastUUID().toString(true);
    }

}
