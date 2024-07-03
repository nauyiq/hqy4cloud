package com.hqy.cloud.alarm.notification.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 业务通知配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/3
 */
@Getter
@Setter
public class BusinessNotificationProperties {

    /**
     * 是否开启通知功能
     */
    private boolean enabled = true;

    /**
     * 需要通知的人...
     */
    private List<String> targets;

    /**
     * 限流配置
     */
    private Limit limit = new Limit();



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Limit {

        /**
         * 是否启用限流.
         */
        private boolean enabled = true;

        /**
         * 限流的次数， 默认
         */
        private int count = 1;

        /**
         * 滑动窗口长度，默认1，单位为秒
         */
        private int windowSize = 1;

    }




}
