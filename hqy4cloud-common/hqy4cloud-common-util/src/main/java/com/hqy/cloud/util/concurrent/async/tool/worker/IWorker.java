package com.hqy.cloud.util.concurrent.async.tool.worker;

import com.hqy.cloud.util.concurrent.async.tool.wrapper.WorkerWrapper;

import java.util.Map;

/**
 * 每个最小执行单元需要实现该接口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 10:48
 */
public interface IWorker<T, V> {

    /**
     * 具体需要执行的业务逻辑，耗时的操作...
     * @param obj         入参
     * @param allWrappers 所有的worker包装引用
     * @return            结果
     */
    @SuppressWarnings("rawtypes")
    V action(T obj, Map<String, WorkerWrapper> allWrappers);

    /**
     * 超时、异常返回的默认值
     * @return 默认值
     */
    V defaultValue();

}
