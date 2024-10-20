package com.hqy.cloud.limiter.core;

import com.hqy.cloud.common.base.project.ProjectContextInfo;
import com.hqy.cloud.limiter.api.DefaultRedisBlockedAdaptor;
import org.redisson.api.RedissonClient;

/**
 * RedisManualBlockedIpService
 * @see DefaultRedisBlockedAdaptor
 * 基于redis ip人工 黑名单 服务
 * @author qy
 * @date 2021-08-02 10:51
 */
public class ManualBlockedIpService extends DefaultRedisBlockedAdaptor {
    public static String NAME = " ManualBlock";

    public ManualBlockedIpService(RedissonClient redissonClient) {
        super(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY, redissonClient);
    }
}
