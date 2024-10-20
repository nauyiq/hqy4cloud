package com.hqy.cloud.sharding.strategy.support;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import com.hqy.cloud.sharding.algorithm.DateShardingTableStandardAlgorithm;
import com.hqy.cloud.sharding.strategy.AbstractDatetimeShardingTableStrategy;

/**
 * 按天分表策略类
 * @see DateShardingTableStandardAlgorithm
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/1
 */
public class DayOfDatetimeShardingTableStrategy extends AbstractDatetimeShardingTableStrategy {
    private static final String TYPE = "day";
    private static final String PATTERN = DatePattern.PURE_DATE_PATTERN;

    @Override
    public String shardingType() {
        return TYPE;
    }

    @Override
    public String getPattern() {
        return PATTERN;
    }

    @Override
    protected DateField getDateStep() {
        return DateField.DAY_OF_YEAR;
    }


}
