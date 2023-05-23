package com.hqy.cloud.canal.core;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:20
 */
public interface CanalGlue {

    /**
     * receive binlog data do something.
     * @param content binlog data.
     */
    void process(String content);
}
