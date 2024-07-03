package com.hqy.cloud.collection.common;

import lombok.RequiredArgsConstructor;

/**
 * 业务采集的类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11
 */
@RequiredArgsConstructor
public enum BusinessCollectionType {

    /**
     * 异常采集
     */
    EXCEPTION("exception"),

    /**
     * 节流封禁采集
     */
    THROTTLES("throttles"),

    /**
     * sql采集
     */
    SQL("sql")

    ;

    public final String name;

}
