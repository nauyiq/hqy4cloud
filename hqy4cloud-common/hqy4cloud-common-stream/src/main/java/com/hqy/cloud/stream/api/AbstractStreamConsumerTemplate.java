package com.hqy.cloud.stream.api;

import com.hqy.cloud.stream.common.StreamConstants;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.concurrent.IExecutorService;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/7
 */
@Slf4j
public abstract class AbstractStreamConsumerTemplate implements StreamConsumer {
    private final Config config;
    private final StreamMessageListener listener;
    private volatile State state;

    public AbstractStreamConsumerTemplate(Config config, StreamMessageListener streamMessageListener) {
        AssertUtil.notNull(config, "Config should not be null.");
        this.config = config;
        this.listener = streamMessageListener;
        // 初始化
        doInit();
    }

    protected void doInit() {
        try {
            // 初始化error handler
            ErrorHandler errorHandler = config.getErrorHandler();
            if (errorHandler == null) {
                config.setErrorHandler(new AbstractRetryErrorHandler(this.listener) {});
            }
            // 初始化线程池.
            IExecutorService executorService = config.getExecutorService();
            if (executorService == null) {
                config.setExecutorService(IExecutorsRepository.newExecutor(StreamConstants.DEFAULT_PRODUCER_EXECUTOR_PREFIX));
            }
            onInit();
            // 修改状态
            this.state = State.INIT;
        } catch (Throwable cause) {
            log.error("Failed execute to init consumer, type:{}, cause: {}.", getType(), cause.getMessage());
            // 初始化阶段异常直接抛出
            throw new RuntimeException("Failed execute to init consumer.", cause);
        }
    }

    @Override
    public boolean start() {
        try {
            onStart();
            this.state = State.STATED;
            return true;
        } catch (Throwable cause) {
            log.error("Failed execute to on start, type: {}, cause: {}.", getType(), cause.getMessage());
            return false;
        }
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public StreamMessageListener getListener() {
        return listener;
    }

    @Override
    public boolean isShutdown() {
        return this.state == State.DESTROYED;
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
            onClose();
        } finally {
            this.state = State.DESTROYED;
        }
    }

    protected abstract void onInit();
    protected abstract void onStart();
    protected abstract void onClose();



}
