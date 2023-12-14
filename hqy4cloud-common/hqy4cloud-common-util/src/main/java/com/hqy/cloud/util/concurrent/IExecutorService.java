package com.hqy.cloud.util.concurrent;

import com.hqy.cloud.util.concurrent.async.tool.callback.ICallback;
import com.hqy.cloud.util.concurrent.async.tool.worker.WorkerResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 10:21
 */
public interface IExecutorService {

    /**
     * 默认核心线程个数 2
     */
    int DEFAULT_CORE_SIZE = 2;

    /**
     * 默认最大线程池线程个数 cpu * 2
     */
    int DEFAULT_MAX_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 默认线程池队列长度 512
     */
    int DEFAULT_QUEUE_SIZE = 512;

    /**
     * 默认等待时间 2分钟
     */
    long DEFAULT_KEEP_ALIVE_MILLS = 2 * 60 * 1000;

    /**
     * 默认的拒绝的策略
     */
    RejectedExecutionHandler DEFAULT_REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.AbortPolicy();

    /**
     * 返回线程池名字
     * @return 线程池名字
     */
    String getName();

    /**
     * 线程池是否快满了 队列长度超过75%
     * @return 是否快满了
     */
    boolean isQueueNearlyFull();

    /**
     * 获取队列长度
     * @return 队列长度
     */
    int getQueueSize();

    /**
     * 获取队列初始长度
     * @return 队列初始长度
     */
    int getQueueCapacity();


    /**
     * 往线程池提交一个任务， 类似于oneway
     * @param runnable runnable task
     */
    void execute(Runnable runnable);

    /**
     * 往线程池提交一个任务， 返回任务的执行结果
     * @param task     任务
     * @return         执行结果
     */
    <V, T> V executeResult(ExecutorTask<T, V> task);

    /**
     * 往线程池提交一个任务， 返回任务的执行结果，并且任务执行会执行回调
     * @param task      任务
     * @param callback  回调
     * @return          执行结果
     */
    <T, V> V executeResult(ExecutorTask<T, V> task, ICallback<T, V> callback);

    /**
     * 往线程池提交一个任务， 返回一个future对象
     * @param task      任务
     * @return          future
     */
    <V, T> CompletableFuture<V> executeFuture(ExecutorTask<T, V> task);

    /**
     * 获取线程池
     * @return {@link ExecutorService}
     */
    ExecutorService getExecutorService();




}
