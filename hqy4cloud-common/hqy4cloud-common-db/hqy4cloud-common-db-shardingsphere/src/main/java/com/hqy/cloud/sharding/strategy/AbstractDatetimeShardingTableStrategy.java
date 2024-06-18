package com.hqy.cloud.sharding.strategy;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/1
 */
@Slf4j
public abstract class AbstractDatetimeShardingTableStrategy implements DatetimeShardingTableStrategy {

    @Override
    public String sharding(Date shardingValue, String logicTableName) {
        AssertUtil.notNull(shardingValue, "Sharding date should not be null");
        AssertUtil.notNull(logicTableName, "Logic table name should not be empty");
        return logicTableName + StrUtil.UNDERLINE + DateUtil.format(shardingValue, getPattern());
    }

    @Override
    public List<String> sharding(Date lowerEndpointDate, Date upperEndpointDate, String logicTableName, Collection<String> availableTargetNames) {
        List<Date> dates = availableTargetNames.stream().map(this::getDateFromTableName).filter(Objects::nonNull).sorted().toList();
        if (CollectionUtils.isEmpty(availableTargetNames) || CollectionUtils.isEmpty(dates)) {
            return CommonSwitcher.ENABLE_SUING_LOGIC_TABLE_WHEN_ACTUAL_NODES_EMPTY.isOn() ? List.of(logicTableName) : Collections.emptyList();
        }
        lowerEndpointDate = lowerEndpointDate == null ? dates.get(0) : lowerEndpointDate;
        upperEndpointDate = upperEndpointDate == null ? dates.get(dates.size() - 1) : upperEndpointDate;

        List<String> resultTables = new ArrayList<>();
        List<DateTime> dateTimes = DateUtil.rangeToList(DateUtil.beginOfDay(lowerEndpointDate), DateUtil.beginOfDay(upperEndpointDate), getDateStep());
        for (DateTime dateTime : dateTimes) {
            String resultTableName = logicTableName + StrUtil.UNDERLINE + DateUtil.format(dateTime, getPattern());
            if (!availableTargetNames.contains(resultTableName)) {
                log.warn("Unknown sharding result table name {} in availableTargetNames: {}.", resultTableName, availableTargetNames);
            }
            resultTables.add(resultTableName);
        }
        return resultTables;
    }

    private Date getDateFromTableName(String tableName) {
        String pattern = getPattern();
        AssertUtil.notEmpty(pattern, "Sharding table data pattern should not be empty.");
        try {
            // 默认分表表名规则为logicTableName_pattern
            String dataValue = tableName.substring(tableName.lastIndexOf(StrUtil.UNDERLINE) + 1);
            return DateUtil.parse(dataValue, pattern);
        } catch (Throwable cause) {
            return null;
        }
    }


    /**
     * 返回时间的步长
     * @return 根据时间分表的步长
     */
    protected abstract DateField getDateStep();



}
