package com.hqy.cloud.mq.rocket.lang;

/**
 * nameserver用;分割
 * 同步消息，如果两次
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/16 9:15
 */
public interface RocketmqConstants {

    // 延迟消息 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h (1-18)

    /**
     * 默认发送消息超时时间
     */
    long TIMEOUT = 3000;

    /**
     * rocketmq
     */
    String ROCKER_MQ = "rocketmq";

    /**
     * orderly hash
     */
    String ORDERLY_HASH = "hashkey";







}
