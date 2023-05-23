package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:02
 */
public class TimestampCanalFieldConverter0 extends BaseCanalFieldConverter<LocalDateTime> {

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final BaseCanalFieldConverter<LocalDateTime> X = new TimestampCanalFieldConverter0();

    private TimestampCanalFieldConverter0() {
        super(JDBCType.TIMESTAMP, LocalDateTime.class);
    }

    @Override
    protected LocalDateTime convertInternal(String source) {
        return LocalDateTime.parse(source, F);
    }

}
