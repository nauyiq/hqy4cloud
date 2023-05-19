package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.math.BigDecimal;
import java.sql.JDBCType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:58
 */
public class DecimalCanalFieldConverter extends BaseCanalFieldConverter<BigDecimal> {

    public static final BaseCanalFieldConverter<BigDecimal> X = new DecimalCanalFieldConverter();

    private DecimalCanalFieldConverter() {
        super(JDBCType.DECIMAL, BigDecimal.class);
    }

    @Override
    protected BigDecimal convertInternal(String source) {
        return new BigDecimal(source);
    }

}
