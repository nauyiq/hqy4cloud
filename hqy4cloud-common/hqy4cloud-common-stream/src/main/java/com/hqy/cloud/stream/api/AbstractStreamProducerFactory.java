package com.hqy.cloud.stream.api;

import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.concurrent.IExecutorService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/6
 */
@Slf4j
public abstract class AbstractStreamProducerFactory<R> implements StreamProducerFactory<R> {

    @Override
    public StreamProducer<R> create(StreamProducer.Config config) {
        AssertUtil.notNull(config, "Stream producer config should not be null.");

        IExecutorService executorService = config.getExecutorService();
        if (executorService == null) {
            log.info("Producer config executor is null, using default executor.");
        }
        return doCreate(config);
    }

    /**
     * 创建生产者
     * @param config 生产者配置类
     * @return       生产者
     */
    protected abstract StreamProducer<R> doCreate(StreamProducer.Config config);
}
