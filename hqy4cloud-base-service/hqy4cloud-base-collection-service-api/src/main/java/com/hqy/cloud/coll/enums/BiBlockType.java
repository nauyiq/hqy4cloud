package com.hqy.cloud.coll.enums;

import lombok.AllArgsConstructor;

/**
 * Bi分析封禁类型.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 10:18
 */
@AllArgsConstructor
public enum BiBlockType {

    /**
     * 黑客攻击-参数
     */
    HACK_ACCESS_PARAM("黑客攻击-参数脚本","HackAccessParam"),

    /**
     * 黑客攻击-uri
     */
    HACK_ACCESS_URI("黑客攻击-uri脚本", "HackAccessURI"),

    /**
     * redis限流控制
     */
    REDIS_FLOW("redis限流控制", "RedisLimitFlowControl"),


    ;

    public final String name;

    public final String value;


}
