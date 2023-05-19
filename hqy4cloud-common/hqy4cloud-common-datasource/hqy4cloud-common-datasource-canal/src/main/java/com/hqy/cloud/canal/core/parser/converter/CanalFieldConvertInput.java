package com.hqy.cloud.canal.core.parser.converter;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.sql.SQLType;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:55
 */
@Builder
@Data
@SuppressWarnings("rawtypes")
public class CanalFieldConvertInput {

    private Class<?> fieldKlass;
    private Class<? extends BaseCanalFieldConverter> converterKlass;
    private SQLType sqlType;

    @Tolerate
    public CanalFieldConvertInput() {

    }

}
