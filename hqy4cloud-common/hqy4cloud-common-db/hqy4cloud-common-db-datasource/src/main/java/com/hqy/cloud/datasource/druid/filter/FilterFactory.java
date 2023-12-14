package com.hqy.cloud.datasource.druid.filter;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.core.env.Environment;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/14 14:34
 */
public class FilterFactory {

    public static final String STAT_FILTER_PREFIX = "spring.datasource.druid.filter.stat";
    public static final String STAT_FILTER_ENABLED = STAT_FILTER_PREFIX + ".enabled";
    public static final String STAT_FILTER_SLOW_SQL_MILLIS = STAT_FILTER_PREFIX + ".slowSqlMillis";
    public static final String STAT_FILTER_MERGER_SQL = STAT_FILTER_PREFIX + ".mergeSql";

    private static final String FILTER_WALL_PREFIX = "spring.datasource.druid.filter.wall";
    private static final String FILTER_WALL_ENABLED = FILTER_WALL_PREFIX + ".enabled";

    private static final String FILTER_SLF4J_PREFIX = "spring.datasource.druid.filter.slf4j";
    private static final String FILTER_SLF4J_ENABLED = FILTER_SLF4J_PREFIX + ".enabled";

    /**
     * 根据配置Environment创建StatFilterConfig
     * @param environment {@link StatFilterConfig}
     * @return            StatFilterConfig
     */
    public static StatFilterConfig createStatConfig(Environment environment) {
        return new StatFilterConfig() {
            @Override
            public boolean logSlowSql() {
                return slowSqlMillis() > 0;
            }

            @Override
            public long slowSqlMillis() {
                return environment.getProperty(STAT_FILTER_SLOW_SQL_MILLIS, Long.class, 2500L);
            }

            @Override
            public boolean mergeSql() {
                return environment.getProperty(STAT_FILTER_MERGER_SQL, Boolean.class, true);
            }
        };
    }

    public static StatFilter createStatFilter(Environment environment) {
        StatFilterConfig statConfig = createStatConfig(environment);
        if (environment.getProperty(STAT_FILTER_ENABLED, Boolean.class, true)) {
            return new ExtendDruidStatFilter(statConfig);
        }
        return null;
    }

    public static WallFilter createWallFilter(Environment environment) {
        if (environment.getProperty(FILTER_WALL_ENABLED, Boolean.class, true)) {
            WallFilter wallFilter = new WallFilter();
            wallFilter.setConfig(new WallConfig());
            return wallFilter;
        }
        return null;
    }

    public static Slf4jLogFilter createSlf4jLogFilter(Environment environment) {
        if (environment.getProperty(FILTER_SLF4J_ENABLED, Boolean.class, true)) {
            return new Slf4jLogFilter();
        }
        return null;
    }



}
