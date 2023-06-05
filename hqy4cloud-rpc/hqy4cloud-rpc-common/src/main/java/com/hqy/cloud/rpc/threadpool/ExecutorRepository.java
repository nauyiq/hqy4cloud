package com.hqy.cloud.rpc.threadpool;

import com.hqy.cloud.rpc.model.RPCModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * ExecutorRepository.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 13:59
 */
public interface ExecutorRepository {

    /**
     * create executor.
     * @param name     executor name
     * @param count    executor thread count.
     * @return         ExecutorService.
     */
    ExecutorService createExecutorIfAbsent(String name, int count);

    /**
     * get executor.
     * @param name     executor name
     * @return         ExecutorService.
     */
    ExecutorService getExecutor(String name);

    /**
     * Modify some of the threadpool's properties according to the url, for example, coreSize, maxSize, ...
     * @param name   name
     * @param executor ExecutorService.
     */
    void updateThreadPool(String name, ExecutorService executor);


    /**
     * common ServiceScheduledExecutor
     * @return ScheduledExecutorService.
     */
    ScheduledExecutorService getCommonScheduledExecutorService();


    /**
     * destroy all
     */
    void destroyAll();


}
