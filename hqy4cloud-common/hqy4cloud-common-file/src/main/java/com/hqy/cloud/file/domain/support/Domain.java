package com.hqy.cloud.file.domain.support;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/8
 */
public enum Domain {

    /**
     * 头像domain
     */
    AVATAR("avatar", DomainConstants.DEFAULT_FILES_DOMAIN),

    /**
     * 文件服务
     */
    FILES("file", DomainConstants.DEFAULT_FILES_DOMAIN),

    /**
     * api网关
     */
    API_GATEWAY("api", DomainConstants.DEFAULT_GATEWAY_DOMAIN),


    ;
    public final String scene;
    public final String value;

    Domain(String scene, String value) {
        this.scene = scene;
        this.value = value;
    }

    private static final Map<String, String> DOMAIN_MAP = Maps.newHashMapWithExpectedSize(Domain.values().length);

    static {
        for (Domain domain : Domain.values()) {
            DOMAIN_MAP.put(domain.scene, domain.value);
        }
    }

    public static String getDefaultDomain(String scene) {
        return DOMAIN_MAP.get(scene);
    }

    public static String getDefaultDomain(Domain domain) {
        return DOMAIN_MAP.get(domain.scene);
    }





}
