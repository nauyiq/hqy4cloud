package com.hqy.cloud.collection.api;

import com.hqy.cloud.collection.common.BusinessCollectionType;
import com.hqy.cloud.collection.core.CollectionConfig;

/**
 * 业务数据采集器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11
 */
public interface Collector<T> {

    /**
     * 进行数据采集
     * @param data 采集的数据.
     */
    void collect(T data);

    /**
     * 获取业务采集的类型
     * @return {@link BusinessCollectionType}
     */
    BusinessCollectionType type();


}
