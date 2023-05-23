package com.hqy.cloud.canal.core.parser;

import java.util.Map;
import java.util.function.Function;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:51
 */
public abstract class BaseCommonEntryFunction<T> implements Function<Map<String, String>, T> {
    @Override
    public T apply(Map<String, String> entry) {
        throw new UnsupportedOperationException();
    }
}
