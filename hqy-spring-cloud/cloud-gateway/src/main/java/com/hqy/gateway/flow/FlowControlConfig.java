package com.hqy.gateway.flow;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 笼统的针对每个ip的 get/post/put/delete 请求总数的超限判断
 * @author qy
 * @date 2021-08-04 14:25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FlowControlConfig {

    /**
     * 计量时间窗口内 get最大访问次数限制
     */
    private int maxGet = 90;

    /**
     * 计量时间窗口内 post最大访问次数限制
     */
    private int maxPost = 60;

    /**
     * 计数缓存有效期 (默认2分钟), 请注意不能小于window对应的分钟数
     */
    private int expireSeconds = 2 * 60;

    /**
     * 如果访问超限，阻塞ip多长时间（全网阻塞）
     */
    private int blockSeconds = 2 * 60;

    /**
     * 计量时间窗口 默认一分钟
     */
    private MeasurementMinutes window = MeasurementMinutes.ONE_MINUTE;


}
