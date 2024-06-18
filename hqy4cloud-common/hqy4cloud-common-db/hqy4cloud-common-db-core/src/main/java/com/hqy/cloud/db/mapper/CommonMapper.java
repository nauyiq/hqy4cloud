package com.hqy.cloud.db.mapper;

import com.hqy.cloud.db.common.CreateTableSql;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/31
 */
@Mapper
public interface CommonMapper {

    /**
     * 获取库中所有的表明
     * @param schema 库名
     * @return       返回对应库中所有的表名
     */
    List<String> getAllTableNameBySchema(@Param("schema") String schema);

    /**
     * 查询建表语句
     * @param tableName 表名
     * @return          建表语句
     */
    CreateTableSql selectTableCreateSql(@Param("tableName") String tableName);


    /**
     * 执行SQL
     * @param sql 需要执行的sql
     */
    void executeSql(@Param("sql") String sql);

}
