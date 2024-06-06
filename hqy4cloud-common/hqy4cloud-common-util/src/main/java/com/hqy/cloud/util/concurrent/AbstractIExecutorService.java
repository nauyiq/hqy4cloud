package com.hqy.cloud.util.concurrent;

import com.hqy.cloud.util.concurrent.async.tool.callback.ICallback;
import com.hqy.cloud.util.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 14:50
 */
@Slf4j
public abstract class AbstractIExecutorService implements IExecutorService {
    private final String name;
    private final int capacity;
    private final BlockingQueue<Runnable> workerQueue;
    private final ThreadPoolExecutor executor;

    public AbstractIExecutorService(String name) {
        this(name, DEFAULT_CORE_SIZE);
    }

    public AbstractIExecutorService(String name, int codeSize) {
        this(DEFAULT_CORE_SIZE, DEFAULT_MAX_SIZE, DEFAULT_QUEUE_SIZE, DEFAULT_KEEP_ALIVE_MILLS, name);
    }

    public AbstractIExecutorService(String name, int codeSize, int maxSize) {
        this(codeSize, maxSize, DEFAULT_QUEUE_SIZE, DEFAULT_KEEP_ALIVE_MILLS, name);
    }

    public AbstractIExecutorService(int codeSize, int maxSize, int capacity, long keepAliveMills, String name) {
        this(codeSize, maxSize, capacity, keepAliveMills, name, new NamedThreadFactory(name), DEFAULT_REJECTED_EXECUTION_HANDLER);
    }

    public AbstractIExecutorService(int codeSize, int maxSize, int capacity, long keepAliveMills, String name, ThreadFactory factory, RejectedExecutionHandler handler) {
        this.capacity = capacity;
        this.name = name;
        this.workerQueue = new LinkedBlockingDeque<>(capacity);
        this.executor = new ThreadPoolExecutor(codeSize, maxSize, keepAliveMills, TimeUnit.MILLISECONDS, workerQueue, factory, handler);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isQueueNearlyFull() {
        float using = (float) getQueueSize() / getQueueCapacity();
        return using > 0.75F;
    }

    @Override
    public int getQueueSize() {
        return workerQueue.size();
    }

    @Override
    public int getQueueCapacity() {
        return this.capacity;
    }

    @Override
    public ExecutorService getExecutorService() {
        return executor;
    }

    @Override
    public void execute(Runnable runnable) {
        this.executor.execute(runnable);
    }

    @Override
    public <V, T> V executeResult(ExecutorTask<T, V> task) {
        CompletableFuture<V> supplyAsync = CompletableFuture.supplyAsync(task, this.executor);
        try {
            return supplyAsync.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T, V> V executeResult(ExecutorTask<T, V> task, ICallback<T, V> callback) {
        task.setCallback(callback);
        CompletableFuture<V> supplyAsync = CompletableFuture.supplyAsync(task, this.executor);
        try {
            return supplyAsync.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <V, T> CompletableFuture<V> executeFuture(ExecutorTask<T, V> task) {
        return CompletableFuture.supplyAsync(task, this.executor);
    }
}
