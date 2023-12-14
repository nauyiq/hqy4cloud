package com.hqy.cloud.datasource.druid.filter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/14 9:55
 */
public interface StatFilterConfig {

    /**
     * 是否开启慢sql记录
     * @return result
     */
    boolean logSlowSql();

    /**
     * 获取慢sql的毫秒数
     * @return 慢sql毫秒数
     */
    long slowSqlMillis();

    /**
     * 是否合并sql
     * @return 是否合并sql
     */
    boolean mergeSql();



}
