package com.hqy.cloud.stream.core;

import com.hqy.cloud.stream.api.Result;
import com.hqy.cloud.util.concurrent.async.tool.worker.ResultState;

/**
 * 封装了某次流操作的结果
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/24
 */
public class StreamResult<R> implements Result<R> {

    /**
     * 某次流操作后的结果状态
     */
    private ResultState state;

    /**
     * 结果值
     */
    private R result;

    /**
     * 异常的原因
     */
    private Throwable exception;


    public StreamResult(ResultState state) {
        this.state = state;
    }

    public StreamResult(ResultState state, R result) {
        this.state = state;
        this.result = result;
    }

    public StreamResult(ResultState state, R result, Exception exception) {
        this.state = state;
        this.result = result;
        this.exception = exception;
    }

    public static <V> StreamResult<V> ofDefault() {
        return new StreamResult<>(ResultState.DEFAULT);
    }

    public static <V> StreamResult<V> ofSuccess(V result) {
        return new StreamResult<>(ResultState.SUCCESS, result);
    }

    public static <V> StreamResult<V> ofError(Exception cause) {
        return new StreamResult<>(ResultState.EXCEPTION, null, cause);
    }

    @Override
    public R getResult() {
        return this.result;
    }

    public void setResult(R result) {
        this.result = result;
    }

    @Override
    public ResultState getState() {
        return state;
    }

    public void setState(ResultState state) {
        this.state = state;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
