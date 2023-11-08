package com.hqy.cloud.canal.annotation;

import com.hqy.cloud.canal.common.FieldNamingPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:07
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalModel {

    /**
     * 目标数据库
     */
    String database();

    /**
     * 目标表
     */
    String table();

    /**
     * 属性名 -> 列名命名转换策略
     */
    FieldNamingPolicy fieldNamingPolicy() default FieldNamingPolicy.DEFAULT;


}
