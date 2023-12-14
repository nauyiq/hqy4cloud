package com.hqy.cloud.foundation.collector.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.foundation.collection.CollectionType;
import com.hqy.foundation.collection.Collector;
import com.hqy.foundation.collection.CollectorConfig;

import java.util.Map;

/**
 * 采集器上下文类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12 17:11
 */
public enum CollectorCenter {

    /**
     * 单例模式的实例对象
     */
    INSTANCE

    ;

    private static final Map<CollectionType, Collector<?>> COLLECTOR_MAP = MapUtil.newConcurrentHashMap();
    private static final Map<CollectionType, CollectorConfig> CONFIG_MAP = MapUtil.newConcurrentHashMap();

    static {
        CollectorConfig sqlConfig = new CollectorConfig(true, 1, true);
        CONFIG_MAP.put(CollectionType.SQL, sqlConfig);
        CollectorConfig exceptionConfig = new CollectorConfig(true, 50, true);
        CONFIG_MAP.put(CollectionType.EXCEPTION, exceptionConfig);
        CollectorConfig throttleConfig = new CollectorConfig(true, 1, true);
        CONFIG_MAP.put(CollectionType.THROTTLES, throttleConfig);
    }
    private final static CollectorConfig DEFAULT_CONFIG = new CollectorConfig();

    public static CollectorCenter getInstance() {
        return INSTANCE;
    }

    public <T> void registry(Collector<T> collector) {
        AssertUtil.notNull(collector, "Collector should not be null.");
        COLLECTOR_MAP.put(collector.type(), collector);
    }

    public CollectorConfig getConfig(CollectionType type) {
        return CONFIG_MAP.getOrDefault(type, DEFAULT_CONFIG);
    }

    public void setConfig(CollectionType type, CollectorConfig config) {
        AssertUtil.notNull(type, "Collection type should not be null.");
        AssertUtil.notNull(config, "Collection config should not be null.");
        CONFIG_MAP.put(type, config);
    }


    @SuppressWarnings("unchecked")
    public <T> Collector<T> getCollector(CollectionType type) {
        return (Collector<T>) COLLECTOR_MAP.get(type);
    }

}
