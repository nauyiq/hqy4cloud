package com.hqy.cloud.shardingsphere.service;

import com.hqy.cloud.db.common.CreateTableSql;
import com.hqy.cloud.db.common.SqlConstants;
import com.hqy.cloud.db.service.CommonDbService;
import com.hqy.cloud.shardingsphere.server.ShardingJdbcContext;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
@Slf4j
@RequiredArgsConstructor
public class ShardingCommonDbService implements CommonDbService {
    private final ShardingJdbcContext shardingJdbcContext;

    @Value("${spring.shardingsphere.sharding.default-data-source-name:master0}")
    private String dataSourceName;


    @Override
    public List<String> selectAllTableNameByDb(String db) {
        AssertUtil.notEmpty(db, "Db name should not be empty.");
        JdbcTemplate jdbcTemplate = shardingJdbcContext.getJdbcTemplate(dataSourceName);
        return jdbcTemplate.queryForList(SqlConstants.getSelectAllTableNameByDbName(db), String.class);
    }

    @Override
    public CreateTableSql selectTableCreateSql(String tableName) {
        AssertUtil.notEmpty(tableName, "Table name should not be empty.");
        JdbcTemplate jdbcTemplate = shardingJdbcContext.getJdbcTemplate(dataSourceName);
        Map<String, Object> map = jdbcTemplate.queryForMap(SqlConstants.getSelectCreateTableDdl(tableName));
        if (MapUtils.isNotEmpty(map)) {
            String table = (String) map.get("Table");
            String createTable = (String) map.get("Create Table");
            return new CreateTableSql(table, createTable);
        }
        return null;
    }

    @Override
    public void execute(String sql) {
        shardingJdbcContext.getActualJdbcTemplates().forEach(jdbcTemplate -> execute(sql));
    }
}
