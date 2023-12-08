package com.hqy.cloud.actuator.core;

/**
 * actuator指示器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/17 17:34
 */
public interface Indicator<T> {

    /**
     * indication id.
     * @return actuator endpoint id...
     */
    String indicatorId();

}
