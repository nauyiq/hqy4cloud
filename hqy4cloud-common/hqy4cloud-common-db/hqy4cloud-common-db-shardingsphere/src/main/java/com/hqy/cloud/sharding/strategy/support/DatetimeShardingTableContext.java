package com.hqy.cloud.sharding.strategy.support;

import com.google.common.collect.Maps;
import com.hqy.cloud.util.spi.SpiInstanceServiceLoad;
import com.hqy.cloud.sharding.strategy.DatetimeShardingTableStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

/**
 * 通过SPI的方式加载 DatetimeShardingTableStrategy到上下文中
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/1
 */
@Slf4j
public class DatetimeShardingTableContext {
    private static final Map<String, DatetimeShardingTableStrategy> STRATEGY_MAP = Maps.newHashMapWithExpectedSize(4);

    static {
        SpiInstanceServiceLoad.register(DatetimeShardingTableStrategy.class);
        Collection<DatetimeShardingTableStrategy> serviceInstances = SpiInstanceServiceLoad.getServiceInstances(DatetimeShardingTableStrategy.class);
        for (DatetimeShardingTableStrategy serviceInstance : serviceInstances) {
            STRATEGY_MAP.put(serviceInstance.shardingType(), serviceInstance);
        }
    }

    public static DatetimeShardingTableStrategy getDatetimeShardingTableStrategy(String shardingType) {
        return STRATEGY_MAP.get(shardingType);
    }


}
