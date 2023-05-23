package com.hqy.cloud.canal.core.parser.converter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:46
 */
public interface BinLogFieldConverter<SOURCE, TARGET> {

    /**
     * SOURCE convert to TARGET
     * @param source SOURCE
     * @return       TARGET
     */
    TARGET convert(SOURCE source);
}
