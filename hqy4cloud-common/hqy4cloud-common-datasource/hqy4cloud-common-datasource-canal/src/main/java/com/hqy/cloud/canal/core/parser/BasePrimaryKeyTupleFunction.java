package com.hqy.cloud.canal.core.parser;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:50
 */
public abstract class BasePrimaryKeyTupleFunction implements TupleFunction<Map<String, String>, Map<String, String>, String, Long> {
    @Override
    public Long apply(Map<String, String> before, Map<String, String> after, String primaryKey) {
        throw new UnsupportedOperationException();
    }
}
