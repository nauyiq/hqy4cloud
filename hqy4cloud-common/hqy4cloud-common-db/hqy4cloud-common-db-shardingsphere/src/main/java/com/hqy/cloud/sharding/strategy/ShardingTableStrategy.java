package com.hqy.cloud.sharding.strategy;

/**
 * 分表策略接口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/8
 */
public interface ShardingTableStrategy {

    /**
     * 获取分表下标
     * @param externalId 外部id
     * @param tableCount 表数目
     * @return           分表下标
     */
    int getTableIndex(String externalId, int tableCount);

}
