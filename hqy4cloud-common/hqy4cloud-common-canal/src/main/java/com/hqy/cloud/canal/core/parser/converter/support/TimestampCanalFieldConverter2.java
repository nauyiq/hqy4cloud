package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:02
 */
public class TimestampCanalFieldConverter2 extends BaseCanalFieldConverter<OffsetDateTime> {

    public static final BaseCanalFieldConverter<OffsetDateTime> X = new TimestampCanalFieldConverter2();

    /**
     * 东八区
     */
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("+08:00");

    private TimestampCanalFieldConverter2() {
        super(JDBCType.TIMESTAMP, OffsetDateTime.class);
    }

    @Override
    protected OffsetDateTime convertInternal(String source) {
        LocalDateTime localDateTime = TimestampCanalFieldConverter0.X.convert(source);
        return OffsetDateTime.of(localDateTime, ZONE_OFFSET);
    }
}
