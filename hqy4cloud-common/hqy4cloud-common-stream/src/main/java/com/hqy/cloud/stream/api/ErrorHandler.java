package com.hqy.cloud.stream.api;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/10
 */
@FunctionalInterface
public interface ErrorHandler {

    /**
     * 处理消费消息异常时
     * @param message   消息
     * @param exception 异常原因
     */
    void onError(Object message, Exception exception);

}
