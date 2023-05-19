package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.Date;
import java.sql.JDBCType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:00
 */
public class SqlDateCanalFieldConverter1 extends BaseCanalFieldConverter<Date> {

    public static final BaseCanalFieldConverter<java.sql.Date> X = new SqlDateCanalFieldConverter1();

    private SqlDateCanalFieldConverter1() {
        super(JDBCType.DATE, java.sql.Date.class);
    }

    @Override
    protected java.sql.Date convertInternal(String source) {
        return java.sql.Date.valueOf(SqlDateCanalFieldConverter0.X.convert(source));
    }
}
