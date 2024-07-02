package com.hqy.cloud.alarm.notification.common;

import lombok.RequiredArgsConstructor;

/**
 * 事件类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11
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
