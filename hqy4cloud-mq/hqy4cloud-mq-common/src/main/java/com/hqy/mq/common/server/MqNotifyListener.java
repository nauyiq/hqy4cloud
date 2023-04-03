package com.hqy.mq.common.server;

import com.hqy.mq.common.bind.MessageModel;

/**
 * 监听器.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 17:41
 */
public interface MqNotifyListener<T extends MessageModel> {

    /**
     * 收到mq消息进行通知.
     * @param message mq消息
     */
    void notify(T message);

}
