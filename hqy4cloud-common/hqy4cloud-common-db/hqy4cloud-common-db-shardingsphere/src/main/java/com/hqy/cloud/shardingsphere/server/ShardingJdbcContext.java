package com.hqy.cloud.shardingsphere.server;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
public interface ShardingJdbcContext {

    /**
     * 根据配置的sharding jdbc数据库别名, 获取对应的jdbcTemplate, 其内置的原生的Datasource
     * 而不是 {@link org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource}
     * @param dbAlias 数据库别名, sharding配置的数据名 即spring.shardingsphere.datasource.names
     * @return        {@link JdbcTemplate}
     */
    JdbcTemplate getJdbcTemplate(String dbAlias);

    /**
     * 获取 {@link org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource}的 jdbcTemplate
     * @return {@link JdbcTemplate}
     */
    JdbcTemplate getShardingJdbcTemplate();

    /**
     * 获取数据库别名对应的数据库连接池
     * @param dbAlias 数据库别名, sharding配置的数据名 即spring.shardingsphere.datasource.names
     * @return        数据库连接池
     */
    DataSource getDataSource(String dbAlias);

    /**
     * 返回sharding jdbc数据库连接池
     * @return  shardingsphere数据库连接池
     */
    DataSource getShardingDateSource();

}
