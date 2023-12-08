package com.hqy.cloud.core.dynamics.support;

import lombok.extern.slf4j.Slf4j;

/**
 * 多数据源线程上下文
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/30 11:39
 */
@Slf4j
public class DynamicMultipleDataSourceContextHolder {

    private static final ThreadLocal<String> CURRENT_DATASOURCE_NAME = new InheritableThreadLocal<>();

    public static void setDataSourceName(String dataSourceName){
        log.info("切换到{}数据源", dataSourceName);
        CURRENT_DATASOURCE_NAME.set(dataSourceName);
    }

    public static String getDataSourceName(){
        return CURRENT_DATASOURCE_NAME.get();
    }

    public static void clearDataSourceName(){
        CURRENT_DATASOURCE_NAME.remove();
    }


}
