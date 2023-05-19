package com.hqy.cloud.canal.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:12
 */
public interface NamingPolicy {

    /**
     * source convert to target value.
     * @param source source
     * @return       target
     */
    String convert(String source);

}
