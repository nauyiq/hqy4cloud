package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.JDBCType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:03
 */
public class TinyIntCanalFieldConverter extends BaseCanalFieldConverter<Integer> {

    public static final BaseCanalFieldConverter<Integer> X = new TinyIntCanalFieldConverter();

    private TinyIntCanalFieldConverter() {
        super(JDBCType.TINYINT, Integer.class);
    }

    @Override
    protected Integer convertInternal(String source) {
        return IntCanalFieldConverter.X.convert(source);
    }
}
