package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.JDBCType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:03
 */
public class VarcharCanalFieldConverter extends BaseCanalFieldConverter<String> {

    public static final BaseCanalFieldConverter<String> X = new VarcharCanalFieldConverter();

    private VarcharCanalFieldConverter() {
        super(JDBCType.VARCHAR, String.class);
    }

    @Override
    protected String convertInternal(String source) {
        return source;
    }
}
