package com.hqy.foundation.common;

import lombok.RequiredArgsConstructor;

/**
 * 时间类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 16:50
 */
@RequiredArgsConstructor
public enum EventType {

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
