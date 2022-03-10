package com.hqy.security.access.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * @author qy
 * @date 2021-09-15 10:50
 */
@Data
@ConfigurationProperties(prefix = "manual")
public class ManualLimitListProperties {

    /**
     * 白名单列表
     */
    private Set<String> whiteIps = new HashSet<>();

    /**
     * 特定uri放行
     */
    private Set<String> whiteUris = new HashSet<>();

    /**
     * 指定黑名单
     */
    private Set<String> blockedIps = new HashSet<>();


}
