package com.hqy.cloud.alarm.notification.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/20
 */
public interface NotificationContent {

    /**
     * 场景， 唯一标识 可用于区分幂等、或超限判断等.
     * @return 场景
     */
    @JsonIgnore
    String scene();

}
