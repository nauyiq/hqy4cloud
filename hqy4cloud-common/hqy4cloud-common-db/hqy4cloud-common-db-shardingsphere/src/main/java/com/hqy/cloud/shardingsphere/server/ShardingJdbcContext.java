package com.hqy.cloud.shardingsphere.server;

import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
public interface ShardingJdbcContext {


    /**
     * 获取某个数据库的所有表名
     * @param datasource 数据库名字，别名
     * @return           所有表名
     */
    Set<String> getTables(String datasource);

    /**
     * 往所有的数据库添加表名
     * @param table 添加的表名
     */
    void addTableName(String table);

    /**
     * 往指定的数据库中添加一个表
     * @param dbAlias 数据库名字，别名
     * @param table   添加的表名
     */
    void addTableName(String dbAlias, String table);

    /**
     * 获取所有数据库中的表名
     * @return 所有表集合
     */
    Map<String, Set<String>> getAllTables();

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
    ShardingDataSource getShardingDateSource();

}
