package com.hqy.cloud.util.concurrent.async.tool.callback;

import com.hqy.cloud.util.concurrent.async.tool.wrapper.WorkerWrapper;

import java.util.List;

/**
 * 如果是异步执行整组的话，可以用这个组回调。不推荐使用
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 14:27
 */
@SuppressWarnings("rawtypes")
public interface IGroupCallback {

    /**
     * 成功后，可以从wrapper里去getWorkResult
     * @param workerWrappers workers
     */
    default void success(List<WorkerWrapper> workerWrappers) {}

    /**
     * 失败了，也可以从wrapper里去getWorkResult
     * @param workerWrappers workers
     * @param e              exception
     */
    default void failure(List<WorkerWrapper> workerWrappers, Exception e) {}

}
