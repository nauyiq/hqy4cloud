package com.hqy.cloud.stream.api;

import com.hqy.cloud.util.concurrent.async.tool.worker.ResultState;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/25
 */
public interface Result<R> {

    /**
     * 获取结果
     * @return 封装请求的结果.
     */
    R getResult();

    /**
     * 请求结果的状态
     * @return 请求结果的状态，
     */
    ResultState getState();

    /**
     * 获取异常的原因
     * @return 异常
     */
    Throwable getException();

}
