package com.hqy.mq.common.server.support;

import cn.hutool.core.thread.NamedThreadFactory;
import com.hqy.mq.common.MessageModel;
import com.hqy.mq.common.lang.enums.MessageQueue;
import com.hqy.mq.common.server.Consumer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 18:10
 */
@Slf4j
public abstract class AbstractConsumer<T extends MessageModel> implements Consumer<T> {
    private final ScheduledExecutorService service;
    private static final int DELAY = 5000;
    public AbstractConsumer(MessageQueue messageQueue) {
        service = Executors.
                newSingleThreadScheduledExecutor(new NamedThreadFactory(messageQueue.name + "-thread-", false));

    }

    /**
     * 执行回调补偿
     * @param message 消息
     * @param cause   Throwable
     */
    protected abstract void doCallback(T message, Throwable cause);


    @Override
    public void compensate(T message, Throwable cause) {
        try {
            doCallback(message, cause);
        } catch (Throwable exception) {
            doCallbackSchedule(message, cause);
            log.error("Failed execute do failCallback, cause: {}.", cause.getMessage(), cause);
        }
    }

    private synchronized void doCallbackSchedule(T message, Throwable cause) {
        service.schedule(new FailCallbackTask<>(message, cause,this), DELAY, TimeUnit.MILLISECONDS);
    }

    @Slf4j
    private static class FailCallbackTask<T extends MessageModel> implements Runnable {
        private final T message;
        private final Throwable exception;
        private final AbstractConsumer<T> consumer;

        public FailCallbackTask(T payload, Throwable exception, AbstractConsumer<T> consumer) {
            this.message = payload;
            this.consumer = consumer;
            this.exception = exception;
        }

        @Override
        public void run() {
            try {
                consumer.doCallback(message, exception);
            } catch (Exception e) {
                log.error("Failed execute to retry do callback, cause {}.", e.getMessage(), e);
            }
        }
    }


}
