package com.hqy.global.flow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * redis流量限流器的检测结果
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-04 15:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisFlowDTO {

    /**
     * redis的key
     */
    private String redisInnerKey;

    /**
     * 是否超限
     */
    private Boolean overLimit = false;

    /**
     * 当前计数值
     */
    private Long counter;

    /**
     * 最大值
     */
    private Integer max;

    /**
     * 封禁时间
     */
    private Integer blockSeconds;


    public RedisFlowDTO(String redisInnerKey, Boolean overLimit, Long counter, Integer max) {
        this.redisInnerKey = redisInnerKey;
        this.overLimit = overLimit;
        this.counter = counter;
        this.max = max;
    }
}
