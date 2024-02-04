package com.hqy.cloud.shardingsphere.server.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.shardingsphere.server.ShardingJdbcContext;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultShardingJdbcContext implements ShardingJdbcContext {
    private final JdbcTemplate jdbcTemplate;
    private final Map<String, JdbcTemplate> jdbcTemplateMap = MapUtil.newConcurrentHashMap(4);

    @PostConstruct
    public void init() {
        // 检查一下shardingsphere datasource
        AssertUtil.notNull(jdbcTemplate, "sharding jdbc template should not be null.");
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource instanceof ShardingDataSource shardingDataSource) {
            // 立刻加载所有的jdbcTemplate
            if (CommonSwitcher.ENABLE_DELAY_LOADING_SHARDINGSPHERE_JDBC_TEMPLATE.isOff()) {
                log.info("Loading shardingsphere jdbc template on spring context start.");
                Map<String, DataSource> dataSourceMap = shardingDataSource.getDataSourceMap();
                for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
                    jdbcTemplateMap.put(entry.getKey(), new JdbcTemplate(entry.getValue()));
                }
            }

        } else {
            throw new ShardingSphereException("Not found sharding jdbc template.");
        }
    }

    private JdbcTemplate getJdbcTemplateByCache(String dbAlias) {
        AssertUtil.notEmpty(dbAlias, "Datasource alias should not be empty.");
        return this.jdbcTemplateMap.computeIfAbsent(dbAlias, v -> {
            DataSource dataSource = this.jdbcTemplate.getDataSource();
            if (dataSource instanceof ShardingDataSource shardingDataSource) {
                Map<String, DataSource> dataSourceMap = shardingDataSource.getDataSourceMap();
                if (dataSourceMap.containsKey(dbAlias)) {
                    return new JdbcTemplate(dataSourceMap.get(dbAlias));
                }
            }
            log.warn("Not found datasource {} from shardingDatasource.", dbAlias);
            return null;
        });
    }



    @Override
    public JdbcTemplate getJdbcTemplate(String dbAlias) {
        return getJdbcTemplateByCache(dbAlias);
    }

    @Override
    public JdbcTemplate getShardingJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public DataSource getDataSource(String dbAlias) {
        JdbcTemplate template = getJdbcTemplateByCache(dbAlias);
        return template == null ? null : template.getDataSource();
    }

    @Override
    public DataSource getShardingDateSource() {
        return jdbcTemplate.getDataSource();
    }
}
