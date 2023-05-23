package com.hqy.cloud.canal.core.parser.converter.support;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

import java.sql.JDBCType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:02
 */
public class TimestampCanalFieldConverter1 extends BaseCanalFieldConverter<Date> {

    public static final BaseCanalFieldConverter<java.util.Date> X = new TimestampCanalFieldConverter1();

    private TimestampCanalFieldConverter1() {
        super(JDBCType.TIMESTAMP, java.util.Date.class);
    }

    @Override
    protected java.util.Date convertInternal(String source) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(source);
        } catch (ParseException e) {
            throw new IllegalArgumentException(String.format("转换日期时间类型java.util.Date失败,原始字符串:%s", source), e);
        }
    }
}
