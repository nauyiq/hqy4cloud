package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.JDBCType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:57
 */
public class BigIntCanalFieldConverter extends BaseCanalFieldConverter<Long> {

    public static final BaseCanalFieldConverter<Long> X = new BigIntCanalFieldConverter();

    private BigIntCanalFieldConverter() {
        super(JDBCType.BIGINT, Long.class);
    }

    @Override
    protected Long convertInternal(String source) {
        return Long.valueOf(source);
    }


}
