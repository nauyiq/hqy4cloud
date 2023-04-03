package com.hqy.multiple.support;

import lombok.RequiredArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/30 14:40
 */
@RequiredArgsConstructor
public enum MultipleDataSourceType {

    /**
     * 默认数据源
     */
    DEFAULT("default"),

    /**
     * hqy数据源
     */
    HQY("hqy"),

    /**
     * app数据源
     */
    APP("app"),

    /**
     * 博客数据源
     */
    APPS_BLOG("apps-blog")

    ;

    public final String name;



}
