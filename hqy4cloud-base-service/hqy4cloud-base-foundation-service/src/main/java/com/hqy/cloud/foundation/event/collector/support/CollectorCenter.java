package com.hqy.cloud.foundation.event.collector.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.foundation.common.EventType;
import com.hqy.foundation.event.collection.Collector;
import com.hqy.foundation.event.collection.CollectorConfig;

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

    private static final Map<EventType, Collector<?>> COLLECTOR_MAP = MapUtil.newConcurrentHashMap();
    private static final Map<EventType, CollectorConfig> CONFIG_MAP = MapUtil.newConcurrentHashMap();

    static {
        CollectorConfig sqlConfig = new CollectorConfig(true, 1);
        CONFIG_MAP.put(EventType.SQL, sqlConfig);
        CollectorConfig exceptionConfig = new CollectorConfig(true, 50);
        CONFIG_MAP.put(EventType.EXCEPTION, exceptionConfig);
        CollectorConfig throttleConfig = new CollectorConfig(true, 1);
        CONFIG_MAP.put(EventType.THROTTLES, throttleConfig);
    }
    private final static CollectorConfig DEFAULT_CONFIG = new CollectorConfig();

    public static CollectorCenter getInstance() {
        return INSTANCE;
    }

    public <T> void registry(Collector<T> collector) {
        AssertUtil.notNull(collector, "Collector should not be null.");
        COLLECTOR_MAP.put(collector.type(), collector);
    }

    public CollectorConfig getConfig(EventType type) {
        return CONFIG_MAP.getOrDefault(type, DEFAULT_CONFIG);
    }

    public void setConfig(EventType type, CollectorConfig config) {
        AssertUtil.notNull(type, "Collection type should not be null.");
        AssertUtil.notNull(config, "Collection config should not be null.");
        CONFIG_MAP.put(type, config);
    }


    @SuppressWarnings("unchecked")
    public <T> Collector<T> getCollector(EventType type) {
        return (Collector<T>) COLLECTOR_MAP.get(type);
    }

}
