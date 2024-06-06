package com.hqy.cloud.stream.api;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/10
 */
@Slf4j
public abstract class AbstractRetryErrorHandler implements ErrorHandler {
    private final StreamMessageListener listener;

    protected AbstractRetryErrorHandler(StreamMessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void onError(Object message, Exception exception) {
        if (message != null) {
            listener.onMessage(List.of(message));
        }
    }
}
