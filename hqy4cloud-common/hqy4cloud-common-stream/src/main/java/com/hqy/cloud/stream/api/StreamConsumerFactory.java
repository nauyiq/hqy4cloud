package com.hqy.cloud.stream.api;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/7
 */
@FunctionalInterface
public interface StreamConsumerFactory {

    /**
     * 创建消费者模型
     * @param config 消费者配置
     * @return       消费者
     */
   StreamConsumer create(StreamConsumer.Config config);

}
