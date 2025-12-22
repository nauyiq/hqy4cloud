package com.hqy.cloud.gateway.route;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态路由配置
 * @author hongqy
 * @date 2025/12/16
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.cloud.gateway.dynamic-route")
public class DynamicRouteConfigProperties {

    /**
     * 是否可用
     */
    private Boolean enabled;

    /**
     * 动态路由配置类型
     */
    private String type;

    /**
     * 动态路由配置数据ID
     */
    private String dataId;

    /**
     * 所属空间
     */
    private String namespace;

    /**
     * 所属组
     */
    private String group;

}

