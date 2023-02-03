package com.hqy.mq.common.server.support;

import com.hqy.mq.common.server.Consumer;
import com.hqy.mq.common.server.Listeners;
import com.hqy.mq.common.server.MqMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractListeners.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/3 17:17
 */
@Slf4j
public abstract class AbstractListeners<T extends MqMessage> implements Listeners<T> {


    @Override
    public Consumer<T> getConsumer() {
        return null;
    }
}
