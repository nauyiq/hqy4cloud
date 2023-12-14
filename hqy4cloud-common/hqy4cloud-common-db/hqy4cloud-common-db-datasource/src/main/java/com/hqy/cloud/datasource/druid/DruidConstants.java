package com.hqy.cloud.datasource.druid;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/13 10:48
 */
public interface DruidConstants {

    /**
     * 监控页面登录用户名 key
     */
    String MONITOR_VIEW_LOGIN_USER_KEY = "loginUsername";

    /**
     * 监控页面登录密码 key
     */
    String MONITOR_VIEW_PASSWORD_KEY = "loginPassword";

    /**
     * 监控页面允许哪些IP访问 key
     */
    String MONITOR_VIEW_ALLOW_IPS_KEY = "allow";


    /**
     * 监控页面禁止哪些IP访问 key
     */
    String MONITOR_VIEW_DENY_IPS_KEY = "deny";

    /**
     * druid url pattern
     */
    String DRUID_URL_PATTERN = "/druid/*";


    String FILTER_STAT_PREFIX = "spring.datasource.druid.filter.stat";



}
