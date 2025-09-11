package com.hqy.cloud.util.concurrent.pc;

import java.util.List;

/**
 * @author hongqy
 * @date 2025/7/17
 */
@FunctionalInterface
public interface Producer<T> {

    List<T> generate() throws Exception;


}
