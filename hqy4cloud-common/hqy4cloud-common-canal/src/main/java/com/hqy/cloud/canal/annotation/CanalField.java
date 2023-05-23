package com.hqy.cloud.canal.annotation;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;
import com.hqy.cloud.canal.core.parser.converter.support.NullCanalFieldConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.JDBCType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:05
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CanalField {

    /**
     * 行名称
     * @return columnName
     */
    String columnName() default "";

    /**
     * sql字段类型
     * @return JDBCType
     */
    JDBCType sqlType() default JDBCType.NULL;

    /**
     * 转换器类型
     * @return
     */
    Class<? extends BaseCanalFieldConverter<?>> converterKlass() default NullCanalFieldConverter.class;

}
