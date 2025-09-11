package com.hqy.cloud.util.concurrent.pc;

/**
 * @author hongqy
 * @date 2025/7/17
 */
@FunctionalInterface
public interface Consumer<T> {

    void accept(T t) throws Exception;

}
