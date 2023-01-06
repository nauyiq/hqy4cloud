package com.hqy.fundation.collector;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/6 13:34
 */
public interface ExceptionCollector {

    /**
     * 采集异常
     * @param cause 异常
     */
    void collect(Throwable cause);


}
