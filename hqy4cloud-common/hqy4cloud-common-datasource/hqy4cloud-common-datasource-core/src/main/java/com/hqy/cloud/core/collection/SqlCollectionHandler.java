package com.hqy.cloud.core.collection;

import com.hqy.cloud.core.collection.support.CollectionModel;

/**
 * sql采集器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/8 16:51
 */
public interface SqlCollectionHandler {

    /**
     * 执行SQL采集
     * @param model {@link CollectionModel}
     */
    void collect(CollectionModel model);

}
