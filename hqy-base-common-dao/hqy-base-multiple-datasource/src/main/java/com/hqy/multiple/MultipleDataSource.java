package com.hqy.multiple;

import javax.sql.DataSource;

/**
 * 多数据源
 * @author qy
 * @date  2021-09-03 18:18
 */
public class MultipleDataSource {

    /**
     * 数据源名称
     */
    private DataSourceName dataSourceName;

    /**
     * 数据源实例
     */
    private DataSource dataSource;


    public MultipleDataSource(DataSourceName dataSourceName, DataSource dataSource) {
        this.dataSourceName = dataSourceName;
        this.dataSource = dataSource;
    }

    public DataSourceName getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(DataSourceName dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public enum DataSourceName {

        DEFAULT_DATA_SOURCE("default")

        ;

        public String name;

        DataSourceName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
