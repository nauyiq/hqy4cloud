package com.hqy.cloud.stream.api;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/24
 */
public interface StreamProducerFactory<R> {

    /**
     * 创建生产者
     * @param config 生产者配置类
     * @return       创建的生产者
     */
    StreamProducer<R> create(StreamProducer.Config config);

}
