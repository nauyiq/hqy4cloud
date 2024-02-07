package com.hqy.cloud.shardingsphere.server.support;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.db.common.SqlConstants;
import com.hqy.cloud.shardingsphere.server.ShardingJdbcContext;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.shardingsphere.shardingjdbc.jdbc.adapter.AbstractDataSourceAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultShardingJdbcContext implements ShardingJdbcContext, InitializingBean {

    private volatile boolean init = false;

    /**
     * context jdbc templates.
     */
    private final List<JdbcTemplate> jdbcTemplates = new ArrayList<>();

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


    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, JdbcTemplate> beans = SpringUtil.getBeansOfType(JdbcTemplate.class);
        if (MapUtils.isNotEmpty(beans)) {
            Collection<JdbcTemplate> templates = beans.values();
            this.jdbcTemplates.addAll(templates);
            if (CommonSwitcher.ENABLE_DELAY_LOADING_SHARDINGSPHERE_JDBC_CONTEXT.isOff()) {
                log.info("Loading shardingsphere jdbc template on spring context start.");
                initialize();
            }
        } else {
            log.warn("Not found jdbc templates.");
        }
    }

    @Override
    public Set<String> getTables(String datasource) {
        if (!init) {
            initialize();
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
            initialize();
        }
        return actualTablesCache;
    }

    @Override
    public JdbcTemplate getJdbcTemplate(String dbAlias) {
        return getJdbcTemplateByCache(dbAlias);
    }


    @Override
    public Set<JdbcTemplate> getActualJdbcTemplates() {
        return new HashSet<>(jdbcTemplateMap.values());
    }

    @Override
    public Set<JdbcTemplate> getContextTemplates() {
        return new HashSet<>(this.jdbcTemplates);
    }

    @Override
    public DataSource getDataSource(String dbAlias) {
        JdbcTemplate template = getJdbcTemplateByCache(dbAlias);
        return template == null ? null : template.getDataSource();
    }


    private void initialize() {
        for (JdbcTemplate template : this.jdbcTemplates) {
            DataSource dataSource = template.getDataSource();
            if (dataSource instanceof AbstractDataSourceAdapter adapter) {
                Map<String, DataSource> dataSourceMap = adapter.getDataSourceMap();
                for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
                    String datasource = entry.getKey();
                    if (!jdbcTemplateMap.containsKey(datasource)) {
                        // 创建Jdbc Template
                        JdbcTemplate jdbcTemplate = new JdbcTemplate(entry.getValue());
                        jdbcTemplateMap.put(datasource, jdbcTemplate);
                        // 加载每个数据源的真实表到内存中.
                        String database = jdbcTemplate.queryForObject(SqlConstants.GET_USING_DATABASE_NAME, String.class);
                        List<String> tables = jdbcTemplate.queryForList(SqlConstants.getSelectAllTableNameByDbName(database), String.class);
                        actualTablesCache.put(datasource, new HashSet<>(tables));
                    }
                }
            }
        }
        init = true;
    }


    private JdbcTemplate getJdbcTemplateByCache(String datasource) {
        AssertUtil.notEmpty(datasource, "Datasource alias should not be empty.");
        return this.jdbcTemplateMap.computeIfAbsent(datasource, v -> {
            for (JdbcTemplate jdbcTemplate : this.jdbcTemplates) {
                DataSource dataSource = jdbcTemplate.getDataSource();
                if (dataSource instanceof AbstractDataSourceAdapter adapter) {
                    Map<String, DataSource> dataSourceMap = adapter.getDataSourceMap();
                    if (dataSourceMap.containsKey(datasource)) {
                        return new JdbcTemplate(dataSourceMap.get(datasource));
                    }
                }
            }
            log.warn("Not found datasource {} from shardingDatasource.", datasource);
            return null;
        });
    }


}
