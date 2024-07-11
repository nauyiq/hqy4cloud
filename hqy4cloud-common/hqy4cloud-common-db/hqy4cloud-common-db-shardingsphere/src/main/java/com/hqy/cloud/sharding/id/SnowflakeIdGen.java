package com.hqy.cloud.sharding.id;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/30
 */
public interface SnowflakeIdGen {

    /**
     * 获取下一个雪花id
     * @return    雪花id
     */
    long nextId();


}
