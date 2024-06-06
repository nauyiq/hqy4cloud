package com.hqy.cloud.stream.api;

import com.hqy.cloud.util.AssertUtil;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/7
 */
public abstract class AbstractStreamConsumerFactory implements StreamConsumerFactory {

    @Override
    public  StreamConsumer create(StreamConsumer.Config config) {
        AssertUtil.notNull(config, "Consumer config should not be null.");

        return doCreate(config);
    }

    /**
     * 交给子类创建消费者
     * @param config 消费者配置类
     * @return       消费者
     */
    protected abstract StreamConsumer doCreate(StreamConsumer.Config config);
}
