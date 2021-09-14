package com.hqy.multiple;

import javax.sql.DataSource;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-03 18:18
 */
public class MultipleDataSource {

    private DataSourceName dataSourceName;

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
