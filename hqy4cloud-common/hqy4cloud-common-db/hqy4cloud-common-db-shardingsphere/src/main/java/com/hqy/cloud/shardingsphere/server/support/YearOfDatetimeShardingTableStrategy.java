package com.hqy.cloud.shardingsphere.server.support;

import cn.hutool.core.date.DateField;
import com.hqy.cloud.shardingsphere.server.AbstractDatetimeShardingTableStrategy;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/1
 */
public class YearOfDatetimeShardingTableStrategy extends AbstractDatetimeShardingTableStrategy {
    private static final String TYPE = "year";
    private static final String PATTERN = "yyyy";

    @Override
    protected String getPattern() {
        return PATTERN;
    }

    @Override
    protected DateField getDateStep() {
        return DateField.YEAR;
    }

    @Override
    public String shardingType() {
        return TYPE;
    }
}
