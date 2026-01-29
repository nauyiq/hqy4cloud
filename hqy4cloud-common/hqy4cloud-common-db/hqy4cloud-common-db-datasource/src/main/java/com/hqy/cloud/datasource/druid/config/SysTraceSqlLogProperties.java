package com.hqy.cloud.datasource.druid.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hongqy
 * @date 2026/1/28
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "sys.trace.sql")
public class SysTraceSqlLogProperties {

    /**
     * 是否打印sql
     */
    private boolean print = true;

    /**
     * sql脱敏
     */
    private boolean desensitized = false;


}
