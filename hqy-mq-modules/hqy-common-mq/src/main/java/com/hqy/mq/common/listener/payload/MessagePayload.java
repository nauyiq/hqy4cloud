package com.hqy.mq.common.listener.payload;

/**
 * rabbit consumer listener payload.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:06
 */
public interface MessagePayload {

    /**
     * 获取消息唯一id
     * @return String
     */
    String obtainMessageId();

}
