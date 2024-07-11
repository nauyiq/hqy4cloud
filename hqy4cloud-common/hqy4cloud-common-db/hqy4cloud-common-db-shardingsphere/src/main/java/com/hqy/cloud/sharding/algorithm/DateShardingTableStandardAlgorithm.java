package com.hqy.cloud.sharding.algorithm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Range;
import com.hqy.cloud.sharding.strategy.DatetimeShardingTableStrategy;
import com.hqy.cloud.sharding.strategy.support.DatetimeShardingTableContext;
import com.hqy.cloud.sharding.service.ShardingService;
import com.hqy.cloud.util.AssertUtil;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.springframework.core.env.Environment;

import java.util.Collection;
import java.util.Date;

/**
 * 根据时间进行分片的标准算法, 支持按照日、月、年进行分片, 当表不存在时会自动创建表
 * @see ShardingTableAlgorithmTool
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/1
 */
public abstract class DateShardingTableStandardAlgorithm extends ShardingTableAlgorithmTool<Date> implements NodeNameMatchingFunction {
    private static final String PREFIX = "spring.shardingsphere.sharding.tables.";
    private static final String SUFFIX = ".table-strategy.standard.type";


    public DateShardingTableStandardAlgorithm() {
        ShardingService.INITIALIZE_TABLES.put(getLogicTableName(), this);
    }

    private volatile DatetimeShardingTableStrategy strategy = null;


    @Override
    public boolean isMatching(String name) {
        String logicTableName = getLogicTableName();
        DatetimeShardingTableStrategy strategy = getShardingDatetimeStrategy(logicTableName);
        String pattern = strategy.getPattern();
        String dateStr = name.substring(name.lastIndexOf(StrUtil.UNDERLINE) + 1);
        try {
            DateUtil.parse(dateStr, pattern);
            return true;
        } catch (Throwable cause) {
            return false;
        }
    }

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Date> preciseShardingValue) {
        String logicTableName = preciseShardingValue.getLogicTableName();
        DatetimeShardingTableStrategy strategy = getShardingDatetimeStrategy(logicTableName);
        // 分表, 分表不存在时则创建该表
        String shardingTable = strategy.sharding(preciseShardingValue.getValue(), logicTableName);
        return checkAndCreateShardingTableName(logicTableName, shardingTable);
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Date> rangeShardingValue) {
        String logicTableName = rangeShardingValue.getLogicTableName();
        DatetimeShardingTableStrategy strategy = getShardingDatetimeStrategy(logicTableName);
        // 范围分表
        Range<Date> range = rangeShardingValue.getValueRange();
        Date lowerEndpoint =  range.hasLowerBound() ? range.lowerEndpoint() : null;
        Date upperEndpoint =  range.hasUpperBound() ? range.upperEndpoint() : null;
        return strategy.sharding(lowerEndpoint, upperEndpoint, logicTableName, availableTargetNames);
    }

    private DatetimeShardingTableStrategy getShardingDatetimeStrategy(String logicTableName) {
        if (strategy == null) {
            // 获取分表的类型.
            Environment environment = SpringUtil.getBean(Environment.class);
            String shardingType = environment.getProperty(PREFIX + logicTableName + SUFFIX);
            AssertUtil.notEmpty(shardingType, "Sharding type should not be empty, please check your environment configuration.");
            // 根据分表类型获取分表策略
            strategy = DatetimeShardingTableContext.getDatetimeShardingTableStrategy(shardingType);
            AssertUtil.notNull(strategy, "Unknown sharding datetime type " + shardingType);
        }
        return strategy;
    }



}
