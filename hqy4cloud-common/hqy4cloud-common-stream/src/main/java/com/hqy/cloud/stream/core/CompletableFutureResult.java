package com.hqy.cloud.stream.core;

import com.hqy.cloud.util.concurrent.async.tool.worker.ResultState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 用于操作异步结果的result
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/24
 */
public class CompletableFutureResult<V> extends StreamResult<V> {
    private final CompletableFuture<V> future;

    public CompletableFutureResult(ResultState state, CompletableFuture<V> future) {
        super(state);
        this.future = future;
    }

    public CompletableFutureResult(Exception e) {
        super(ResultState.EXCEPTION);
        this.future = null;
    }

    @Override
    public V getResult() {
        if (this.future != null && getState() != null && getState() == ResultState.DEFAULT) {
            // 说明还未从future中获取结果.
            try {
                setResult(future.get());
                setState(ResultState.SUCCESS);
            } catch (ExecutionException e) {
                setException(e);
                setState(ResultState.EXCEPTION);
            } catch (InterruptedException e) {
                setException(e);
                setState(ResultState.TIMEOUT);
            }
        }
        return super.getResult();
    }

    public CompletableFuture<V> getFuture() {
        return future;
    }

    public static <V> CompletableFutureResult<V> of(Exception cause) {
        return new CompletableFutureResult<>(cause);
    }

    public static <V> CompletableFutureResult<V> of(CompletableFuture<V> future) {
        return new CompletableFutureResult<>(ResultState.DEFAULT, future);
    }

    public static <V> CompletableFutureResult<V> ofDefault() {
        return new CompletableFutureResult<>(ResultState.DEFAULT, null);
    }

}
