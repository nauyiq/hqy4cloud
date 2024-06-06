package com.hqy.cloud.stream.api;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.stream.core.StreamResult;
import com.hqy.cloud.stream.core.CompletableFutureResult;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.concurrent.ExecutorTask;
import com.hqy.cloud.util.concurrent.IExecutorService;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import com.hqy.cloud.util.concurrent.async.tool.callback.ICallback;
import com.hqy.cloud.util.concurrent.async.tool.worker.ResultState;
import com.hqy.cloud.util.concurrent.async.tool.worker.WorkerResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 * 生产者基类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/24
 */
@Slf4j
public abstract class AbstractStreamProducerTemplate<R> implements StreamProducer<R> {
    private final Config config;
    private volatile boolean shutdown = false;

    public AbstractStreamProducerTemplate(Config config) {
        AssertUtil.notNull(config, "Create stream producer configuration should not be null.");
        this.config = config;
    }

    @Override
    public <K extends Comparable<K>, V> StreamResult<R> syncSend(StreamMessage<K, V> streamMessage, StreamCallback<R> callback) {
        return sendMessage(SendMode.SYNC, streamMessage, callback);
    }

    @Override
    public <K extends Comparable<K>, V> CompletableFutureResult<R> asyncSend(StreamMessage<K, V> streamMessage, StreamCallback<R> callback) {
        return (CompletableFutureResult<R>) sendMessage(SendMode.ASYNC, streamMessage, callback);
    }

    @Override
    public <K extends Comparable<K>, V> void onewaySend(StreamMessage<K, V> streamMessage, StreamCallback<R> callback) {
        sendMessage(SendMode.ONEWAY, streamMessage, callback);
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public synchronized void shutdown() {
        if (isShutdown()) {
            return;
        }
        try {
            IExecutorService executorService = config.getExecutorService();
            if (executorService != null && !executorService.getExecutorService().isShutdown()) {
                executorService.getExecutorService().shutdown();
            }
        } finally {
            shutdown = true;
        }
    }

    private <K extends Comparable<K>, V> StreamResult<R> sendMessage(SendMode sendMode, StreamMessage<K, V> streamMessage, StreamCallback<R> callback) {
        if (!checkMessageEnabled(streamMessage)) {
            // 判断消息是否可用
            IllegalArgumentException argumentException = new IllegalArgumentException("The message is invalid.");
            return sendMode == SendMode.SYNC ? StreamResult.ofError(argumentException) : CompletableFutureResult.of(argumentException);
        }

        if (sendMode == SendMode.ONEWAY) {
            // oneway 不在乎执行结果.
            ExecutorTask<StreamMessage<K, V>, R> task = getExecutorTask(streamMessage, callback);
            getConfig().getExecutorService().executeFuture(task);
            return StreamResult.ofSuccess(null);
        } else if (sendMode == SendMode.SYNC) {
            // 同步执行方法
            StreamResult<R> result = null;
            try {
                R r = doSyncSendMessage(streamMessage);
                result = StreamResult.ofSuccess(r);
                return result;
            } catch (Exception cause) {
                log.error("Failed execute to do sync message, cause: {}.", cause.getMessage());
                result = StreamResult.ofError(cause);
                return result;
            } finally {
                if (callback != null && result != null) {
                    // 执行回调
                    try {
                        ResultState state = result.getState();
                        if (state == ResultState.SUCCESS) {
                            callback.onSuccess(result.getResult());
                        } else {
                            callback.onFailed(result.getException());
                        }
                    } catch (Throwable cause) {
                        log.error("Failed execute to do message callback, cause: {}.", cause.getMessage());
                    }
                }
            }
        } else {
            if (config.isSupportAsyncApi()) {
                doAsyncSendMessage(streamMessage, callback);
                return CompletableFutureResult.ofDefault();
            }
            // 异步执行， 返回future封装的result
            ExecutorTask<StreamMessage<K, V>, R> task = getExecutorTask(streamMessage, callback);
            CompletableFuture<R> future = getExecutorService().executeFuture(task);
            return CompletableFutureResult.of(future);
        }

    }


    private <K extends Comparable<K>, V> boolean checkMessageEnabled(StreamMessage<K, V> streamMessage) {
        return streamMessage != null && !StringUtils.isBlank(streamMessage.getTopic());
    }


    /**
     * 交给子类去实现发消息逻辑，同步发消息
     * @param  message  消息
     * @return          结果
     */
    protected abstract <K extends Comparable<K>, V> R doSyncSendMessage(StreamMessage<K, V> message);

    /**
     * 异步发消息，交给子类去实现。子类不支持异步消息API情况下直接 return null即可
     * @param message 消息
     */
    protected abstract <K extends Comparable<K>, V> void doAsyncSendMessage(StreamMessage<K, V> message, StreamCallback<R> callback);



    @Override
    public Config getConfig() {
        return this.config;
    }


    private <K extends Comparable<K>, V>  ExecutorTask<StreamMessage<K, V>, R> getExecutorTask(StreamMessage<K, V> message, StreamCallback<R> callback) {
        ExecutorTask<StreamMessage<K,V>, R> task = new ExecutorTask<>(message, null) {
            @Override
            public R action(StreamMessage<K, V> param) {
                return doSyncSendMessage(message);
            }
        };
        if (callback != null) {
            // 封装异步回调.
            ICallback<StreamMessage<K, V>, R> iCallback = new ICallback<>() {
                @Override
                public void result(boolean result, StreamMessage<K, V> param, WorkerResult<R> workerResult) {
                    if (result) {
                        callback.onSuccess(workerResult.getResult());
                    } else {
                        callback.onFailed(workerResult.getCause());
                    }
                }
            };
            task.setCallback(iCallback);
        }
        return task;
    }


    protected IExecutorService getExecutorService() {
        IExecutorService executorService = getConfig().getExecutorService();
        if (executorService == null) {
            log.warn("Producer executor not config executor, using default executor.");
            return IExecutorsRepository.newExecutor(this.getClass().getSimpleName() + StrUtil.DASHED + getType());
        }
        return executorService;
    }

    enum SendMode {

        /**
         * 同步
         */
        SYNC,

        /**
         * 异步
         */
        ASYNC,

        /**
         * oneway
         */
        ONEWAY
    }


}
