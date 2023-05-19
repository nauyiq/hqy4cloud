package com.hqy.cloud.canal.core.parser;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:42
 */
public interface TupleFunction<BEFORE, AFTER, KEY, R> {
    R apply(BEFORE before, AFTER after, KEY key);
}
