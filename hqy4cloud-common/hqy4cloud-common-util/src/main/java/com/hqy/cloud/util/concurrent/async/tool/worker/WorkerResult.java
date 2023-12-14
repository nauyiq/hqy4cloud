package com.hqy.cloud.util.concurrent.async.tool.worker;

/**
 * 执行回调结果
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 11:25
 */
public class WorkerResult<V> {

    private V result;
    private ResultState state;
    private Exception cause;


    public WorkerResult(V result, ResultState state) {
        this(result, state, null);
    }

    public WorkerResult(V result, ResultState state, Exception cause) {
        this.result = result;
        this.state = state;
        this.cause = cause;
    }

    public static <V> WorkerResult<V> ofDefault() {
        return new WorkerResult<>(null, ResultState.DEFAULT);
    }

    public static <V> WorkerResult<V> ofSuccess(V result) {
        return new WorkerResult<>(result, ResultState.SUCCESS, null);
    }

    public static <V> WorkerResult<V> ofError(Exception cause) {
        return new WorkerResult<>(null, ResultState.EXCEPTION, cause);
    }



    @Override
    public String toString() {
        return "WorkerResult{" +
                "result=" + result +
                ", state=" + state +
                ", cause=" + cause +
                '}';
    }

    public V getResult() {
        return result;
    }

    public void setResult(V result) {
        this.result = result;
    }

    public ResultState getState() {
        return state;
    }

    public void setState(ResultState state) {
        this.state = state;
    }

    public Exception getCause() {
        return cause;
    }

    public void setCause(Exception cause) {
        this.cause = cause;
    }
}
