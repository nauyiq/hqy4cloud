package com.hqy.cloud.stream.api;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/25
 */
public interface StreamService {

    /**
     * 获取发布订阅的类型
     * @return 类型
     */
    String getType();

    /**
     * 是否关闭
     * @return 是否关闭.
     */
    boolean isShutdown();

    /**
     * 关闭服务.
     */
    void shutdown();


}
