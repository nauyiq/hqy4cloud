package com.hqy.mq.common.lang;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 14:31
 */
public interface Constants {

    /**
     * 顺序消息
     */
    String ORDERLY_MESSAGE_KEY = "orderly_message_key";

    /**
     * 超时key
     */
    String SEND_MESSAGE_TIMEOUT_KEY = "produce-message-timeout";

    /**
     * 默认发送的超时时间
     */
    long DEFAULT_TIMEOUT = 3000;



}
