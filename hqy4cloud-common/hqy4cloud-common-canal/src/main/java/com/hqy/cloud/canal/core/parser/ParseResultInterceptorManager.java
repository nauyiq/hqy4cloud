package com.hqy.cloud.canal.core.parser;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:42
 */
public interface ParseResultInterceptorManager {

    /**
     * register BaseParseResultInterceptor.
     * @param parseResultInterceptor {@link BaseParseResultInterceptor}
     */
    <T> void registerParseResultInterceptor(BaseParseResultInterceptor<T> parseResultInterceptor);

    /**
     * get BaseParseResultInterceptor list by class
     * @param klass class type
     * @return      BaseParseResultInterceptor
     */
    <T> List<BaseParseResultInterceptor<T>> getParseResultInterceptors(Class<T> klass);

}
