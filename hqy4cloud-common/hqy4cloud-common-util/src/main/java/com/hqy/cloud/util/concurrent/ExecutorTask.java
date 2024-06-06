package com.hqy.cloud.util.concurrent;

import com.hqy.cloud.util.concurrent.async.tool.callback.ICallback;
import com.hqy.cloud.util.concurrent.async.tool.worker.WorkerResult;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 15:28
 */
@Slf4j
public abstract class ExecutorTask<T, V> implements Supplier<V> {
    private final T param;
    private final V defaultValue;
    private ICallback<T, V> callback;

    public ExecutorTask(T param) {
        this(param, null);

    }

    public ExecutorTask(T param, V defaultValue) {
        this.param = param;
        this.defaultValue = defaultValue;
    }

    public ExecutorTask(T param, V defaultValue, ICallback<T, V> callback) {
        this.param = param;
        this.defaultValue = defaultValue;
        this.callback = callback;
    }

    @Override
    public V get() {
        WorkerResult<V> result = null;
        boolean isSuccess = false;
        try {
            V actionResult = action(this.param);
            isSuccess = true;
            result = WorkerResult.ofSuccess(actionResult);
            return actionResult;
        } catch (Exception cause) {
            result = WorkerResult.ofError(cause);
            log.error(cause.getMessage(), cause);
            return defaultValue;
        } finally {
            if (this.callback != null) {
                this.callback.result(isSuccess, this.param, result);
            }
        }
    }

    public WorkerResult<V> actionResult() {
        WorkerResult<V> result;
        try {
            V actionResult = action(this.param);
            result = WorkerResult.ofSuccess(actionResult);
        } catch (Exception cause) {
            result = WorkerResult.ofError(cause);
            log.error(cause.getMessage(), cause);
        }
        return result;
    }

    /**
     * 执行真正的业务
     * @param param 入参
     * @return      结果
     */
    public abstract V action(T param);

    public ICallback<T, V> getCallback() {
        return callback;
    }

    public void setCallback(ICallback<T, V> callback) {
        this.callback = callback;
    }

    public T getParam() {
        return param;
    }
}
