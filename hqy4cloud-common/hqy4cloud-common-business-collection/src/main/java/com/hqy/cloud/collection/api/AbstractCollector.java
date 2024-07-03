package com.hqy.cloud.collection.api;

import com.hqy.cloud.collection.common.BusinessCollectionType;
import com.hqy.cloud.collection.core.CollectionConfig;
import com.hqy.cloud.limiter.api.Limiter;
import com.hqy.cloud.limiter.core.GuavaCacheTokenBucketLimiter;
import com.hqy.cloud.limiter.flow.FlowLimitConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据采集器基类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11
 */
@Slf4j
@Getter
public abstract class AbstractCollector<T> implements Collector<T> {

    /**
     * 采集器配置类
     */
    private final CollectionConfig config;

    /**
     * 限流器 防止采集过多
     */
    private final Limiter limiter;

    /**
     * 频率计数器， 可能不太准
     * 因为hashcode判断时很大概率发生hash冲突。 但是采集业务允许误差
     */
    private final Map<BusinessCollectionType, AtomicLong> frequencyMap = new ConcurrentHashMap<>();


    @Override
    public void collect(T data) {
        if (data == null || !config.isEnabled()) {
            return;
        }

        // 判断是否达到采集频率了,
        int frequency = config.getFrequency();
        boolean isCollect = true;
        AtomicLong dataFrequency = frequencyMap.computeIfAbsent(type(), v -> new AtomicLong(0));
        if (dataFrequency.incrementAndGet() % frequency != 0) {
            isCollect = false;
        }

        // 判断是否采集超限了
        if (isCollect && limiter.isOverLimit(type().name())) {
            // 采集过多了 暂时不采集了...
            log.warn("Collection too many data, type: {}.", type().name());
            isCollect = false;
        }

        // 数据采集
        if (isCollect) {
            try {
                doCollect(data);
            } catch (Throwable cause) {
              log.error("Failed execute to do collect, cause: {}.", cause.getMessage(), cause);
            }
        }
    }


    /**
     * 采集数据
     * @param data 被采集数据
     */
    protected abstract void doCollect(T data);


    public AbstractCollector(CollectionConfig config) {
        this.config = config;
        // 创建限流器
        this.limiter = getLimiter(config);
    }

    private GuavaCacheTokenBucketLimiter getLimiter(CollectionConfig config) {
        return new GuavaCacheTokenBucketLimiter(new FlowLimitConfig(config.getCount(), config.getWindowSize()));
    }


}
