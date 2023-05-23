package com.hqy.cloud.canal.core.processor;

import com.hqy.cloud.canal.model.CanalBinLogEvent;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:37
 */
@FunctionalInterface
public interface ExceptionHandler {

    /**
     * 发成异常时执行内容
     * @param event     {@link CanalBinLogEvent}
     * @param throwable 异常
     */
    void onError(CanalBinLogEvent event, Throwable throwable);

    ExceptionHandler NO_OP = (event, throwable) -> {
    };
}
