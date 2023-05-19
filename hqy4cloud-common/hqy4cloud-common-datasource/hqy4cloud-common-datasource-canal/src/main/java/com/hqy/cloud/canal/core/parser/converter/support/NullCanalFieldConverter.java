package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.JDBCType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:59
 */
public class NullCanalFieldConverter extends BaseCanalFieldConverter<Void> {

    public static final BaseCanalFieldConverter<Void> X = new NullCanalFieldConverter();

    private NullCanalFieldConverter() {
        super(JDBCType.NULL, Void.class);
    }

    @Override
    protected Void convertInternal(String source) {
        return null;
    }

}
