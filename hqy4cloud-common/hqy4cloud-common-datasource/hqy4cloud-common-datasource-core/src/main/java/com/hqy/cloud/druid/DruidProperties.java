package com.hqy.cloud.druid;

import cn.hutool.core.map.MapUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.hqy.cloud.util.AssertUtil;
import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.pool.DruidDataSourceFactory.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/30 10:15
 */
@Data
public class DruidProperties {

    private int initialSize = 0;
    private int minIdle = 10;
    private int maxActive = 50;
    private int maxWait = 2000;
    private int slowSqlMillis = 2500;
    private int timeBetweenEvictionRunsMillis = 30000;
    private int minEvictableIdleTimeMillis = 30000;
    private int maxEvictableIdleTimeMillis = 30000;
    private String validationQuery = "select 1 from dual";
    private boolean testOnBorrow = false;
    private boolean testOnReturn = true;
    private boolean testWhileIdle = true;
    public static final String PROP_MAXEVICTABLEIDLETIMEMILLIS =  "maxEvictableIdleTimeMillis";

    public Map<String, String> toMap() {
        HashMap<String, String> map = MapUtil.newHashMap(16);
        map.put(PROP_INITIALSIZE, initialSize + "");
        map.put(PROP_MINIDLE, minIdle + "");
        map.put(PROP_MAXACTIVE, maxActive + "");
        map.put(PROP_MAXWAIT, maxWait + "");
        map.put(PROP_TIMEBETWEENEVICTIONRUNSMILLIS, timeBetweenEvictionRunsMillis + "");
        map.put(PROP_MINEVICTABLEIDLETIMEMILLIS, minEvictableIdleTimeMillis + "");
        map.put(PROP_MAXEVICTABLEIDLETIMEMILLIS, maxEvictableIdleTimeMillis + "");
        map.put(PROP_VALIDATIONQUERY, validationQuery);
        map.put(PROP_TESTONBORROW, testOnBorrow + "");
        map.put(PROP_TESTONRETURN, testOnReturn + "");
        map.put(PROP_TESTWHILEIDLE, testWhileIdle + "");
        return map;
    }

    public void config(DruidDataSource druidDataSource) {
        // 初始连接数
        druidDataSource.setInitialSize(initialSize);
        // 最小连接池数量
        druidDataSource.setMinIdle(minIdle);
        // 最大连接池数量
        druidDataSource.setMaxActive(maxActive);
        // 获取连接等待超时的时间
        druidDataSource.setMaxWait(maxWait);
        // 检测间隔时间，检测需要关闭的空闲连接，单位毫秒
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        // 一个连接在连接池中最小的生存时间，单位毫秒
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        // 一个连接在连接池中最大的生存时间，单位毫秒
        druidDataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        // 配置检测连接是否有效
        druidDataSource.setValidationQuery(validationQuery);
        // 如果为true（默认为false），当应用向连接池申请连接时，连接池会判断这条连接是否是可用的
        druidDataSource.setTestOnBorrow(testOnBorrow);
        // 连接返回检测
        druidDataSource.setTestOnReturn(testOnReturn);
        // 失效连接检测
        druidDataSource.setTestWhileIdle(testWhileIdle);
    }

    public Map<String, String> setDbProperties(DataSourceProperties dataSourceProperties) {
        AssertUtil.notNull(dataSourceProperties, "DataSourceProperties should not be null.");
        Map<String, String> map = this.toMap();
        map.put(PROP_URL, dataSourceProperties.getUrl());
        map.put(PROP_USERNAME, dataSourceProperties.getUsername());
        map.put(PROP_PASSWORD, dataSourceProperties.getPassword());
        map.put(PROP_DRIVERCLASSNAME, dataSourceProperties.getDriverClassName());
        return map;
    }
}
