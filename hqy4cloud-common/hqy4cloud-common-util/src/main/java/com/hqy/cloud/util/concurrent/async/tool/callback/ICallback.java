package com.hqy.cloud.util.concurrent.async.tool.callback;

import com.hqy.cloud.util.concurrent.async.tool.worker.WorkerResult;

/**
 * 每个执行单元执行完毕后，会回调该接口</p>
 * 需要监听执行结果的，实现该接口即可
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 11:23
 */
public interface ICallback<T, V> {

    /**
     * 开始执行
     */
    default void begin() {}

    /**
     * 执行结果， 耗时操作执行完毕后，就会往WorkerResult注入值
     * @param result       执行结果
     * @param param        入参
     * @param workerResult 回调结果 {@link WorkerResult}
     */
    default void result(boolean result, T param, WorkerResult<V> workerResult) {}

}



