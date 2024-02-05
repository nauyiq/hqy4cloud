package com.hqy.cloud.shardingsphere.server;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 根据时间分表策略模型.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/1
 */
public interface DatetimeShardingTableStrategy {

    /**
     * 该策略支持的分表类型, 年月日?
     * @return 分表类型
     */
    String shardingType();


    /**
     * 返回时间格式的pattern
     * @return 根据时间分表的pattern
     */
    String getPattern();

    /**
     * 精确分表.
     * @param shardingValue        用于分表的时间值
     * @param logicTableName       逻辑表名
     * @return                     value映射到哪个表
     */
    String sharding(Date shardingValue, String logicTableName);

    /**
     * 范围分表.
     * @param lowerEndpointDate    开始时间值
     * @param upperEndpointDate    结束时间值
     * @param logicTableName       逻辑表名
     * @param availableTargetNames 可用表名列表
     * @return                     范围分到的表
     */
    List<String> sharding(Date lowerEndpointDate,
                          Date upperEndpointDate,
                          String logicTableName,
                          Collection<String> availableTargetNames);




}
