package com.hqy.cloud.canal.core.parser;

import lombok.Data;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:45
 */
@Data
public class ColumnMetadata {

    private String columnName;
    private BaseCanalFieldConverter<?> converter;
}
