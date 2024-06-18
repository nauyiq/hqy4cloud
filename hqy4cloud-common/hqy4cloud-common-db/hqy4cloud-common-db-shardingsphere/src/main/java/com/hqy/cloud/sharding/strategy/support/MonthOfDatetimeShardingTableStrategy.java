package com.hqy.cloud.sharding.strategy.support;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import com.hqy.cloud.sharding.strategy.AbstractDatetimeShardingTableStrategy;

/**
 * 按月分表策略类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/1
 */
public class MonthOfDatetimeShardingTableStrategy extends AbstractDatetimeShardingTableStrategy {
    private static final String TYPE = "month";
    private static final String PATTERN = DatePattern.SIMPLE_MONTH_PATTERN;

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    protected DateField getDateStep() {
        return DateField.MONTH;
    }

    @Override
    public String shardingType() {
        return TYPE;
    }
}
