package com.hqy.cloud.shardingsphere.server.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.db.common.SqlConstants;
import com.hqy.cloud.shardingsphere.server.ShardingJdbcContext;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultShardingJdbcContext implements ShardingJdbcContext {

    private volatile boolean init = false;

    /**
     * sharding jdbcTemplate.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * 已配置数据源对应的jdbcTemplate.
     * key = 连接池名字
     * value = 对应的jdbc template
     */
    private final Map<String, JdbcTemplate> jdbcTemplateMap = MapUtil.newConcurrentHashMap(4);

    /**
     * 数据源存在表的缓存.
     * key = 连接池名字
     * value = 数据库中所有的表名
     */
    private final Map<String, Set<String>> actualTablesCache = MapUtil.newConcurrentHashMap(4);



    @PostConstruct
    public void init() {
        AssertUtil.notNull(jdbcTemplate, "sharding jdbc template should not be null.");
        if (jdbcTemplate.getDataSource() instanceof ShardingDataSource shardingDataSource) {
            if (CommonSwitcher.ENABLE_DELAY_LOADING_SHARDINGSPHERE_JDBC_CONTEXT.isOff()) {
                log.info("Loading shardingsphere jdbc template on spring context start.");
                // 加载所有的元数据到缓存中.
                loadMetadataToCaches(shardingDataSource);
            }
        } else {
            throw new ShardingSphereException("Not found sharding jdbc template.");
        }
    }


    @Override
    public Set<String> getTables(String datasource) {
        if (!init) {
            loadMetadataToCaches(getShardingDateSource());
        }
        return actualTablesCache.get(datasource);
    }

    @Override
    public void addTableName(String table) {
        actualTablesCache.values().forEach(tables -> tables.add(table));
    }

    @Override
    public void addTableName(String datasourceName, String table) {
        Set<String> tables = actualTablesCache.computeIfAbsent(datasourceName, v -> new HashSet<>());
        tables.add(table);
    }

    @Override
    public Map<String, Set<String>> getAllTables() {
        if (!init) {
            loadMetadataToCaches(getShardingDateSource());
        }
        return actualTablesCache;
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
    public ShardingDataSource getShardingDateSource() {
        return (ShardingDataSource) jdbcTemplate.getDataSource();
    }

    private void loadMetadataToCaches(ShardingDataSource shardingDataSource) {
        Map<String, DataSource> dataSourceMap = shardingDataSource.getDataSourceMap();
        for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
            String datasource = entry.getKey();
            // 创建Jdbc Template
            JdbcTemplate jdbcTemplate = new JdbcTemplate(entry.getValue());
            jdbcTemplateMap.put(datasource, jdbcTemplate);
            // 加载每个数据源的真实表到内存中.
            String database = jdbcTemplate.queryForObject(SqlConstants.GET_USING_DATABASE_NAME, String.class);
            List<String> tables = jdbcTemplate.queryForList(SqlConstants.getSelectAllTableNameByDbName(database), String.class);
            actualTablesCache.put(datasource, new HashSet<>(tables));
        }
        init = true;
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

}
