package com.hqy.cloud.sharding.strategy.support;

import com.hqy.cloud.sharding.strategy.ShardingTableStrategy;

/**
 * 默认采用hash算法进行分表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/8
 */
public class DefaultShardingTableStrategy implements ShardingTableStrategy {

    @Override
    public int getTableIndex(String externalId, int tableCount) {
        int hashcode = externalId.hashCode();
        return (int) Math.abs((long) hashcode) % tableCount;
    }
}
