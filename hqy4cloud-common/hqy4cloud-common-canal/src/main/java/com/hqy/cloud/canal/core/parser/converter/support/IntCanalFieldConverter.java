package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.JDBCType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:59
 */
public class IntCanalFieldConverter extends BaseCanalFieldConverter<Integer> {

    public static final BaseCanalFieldConverter<Integer> X = new IntCanalFieldConverter();

    private IntCanalFieldConverter() {
        super(JDBCType.INTEGER, Integer.class);
    }

    @Override
    protected Integer convertInternal(String source) {
        return Integer.valueOf(source);
    }

}
