package com.hqy.cloud.db.dynamics.annotation;

import com.hqy.cloud.db.dynamics.config.MultipleDataSourceProvider;

import java.lang.annotation.*;

/**
 * 自定义数据源类型注解, 标志当前接口使用的数据源类型
 * 可作用于类名或方法名上
 * @author qy
 * @date 2021-09-03 17:31
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceType {

    String value() default MultipleDataSourceProvider.DEFAULT_DATASOURCE;

}
