package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:00
 */
public class SqlDateCanalFieldConverter0 extends BaseCanalFieldConverter<LocalDate> {

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final BaseCanalFieldConverter<LocalDate> X = new SqlDateCanalFieldConverter0();

    public SqlDateCanalFieldConverter0() {
        super(JDBCType.DATE, LocalDate.class);
    }

    @Override
    protected LocalDate convertInternal(String source) {
        return LocalDate.parse(source, F);
    }

}
