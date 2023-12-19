package com.hqy.foundation.collection;

import com.hqy.foundation.common.EventType;

/**
 * 业务数据采集器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 17:06
 */
public interface Collector<T> {

    /**
     * 进行数据采集
     * @param data 采集的数据.
     */
    void collect(T data);

    /**
     * 获取业务采集的类型
     * @return {@link EventType}
     */
    EventType type();

    /**
     * 设置采集器配置类
     * @param config 配置类
     */
    void setConfig(CollectorConfig config);

}
