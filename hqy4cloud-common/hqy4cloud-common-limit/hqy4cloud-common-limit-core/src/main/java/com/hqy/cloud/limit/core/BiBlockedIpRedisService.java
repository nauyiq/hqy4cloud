package com.hqy.cloud.limit.core;

import com.hqy.cloud.common.base.project.ProjectContextInfo;
import com.hqy.cloud.limit.api.DefaultRedisBlockedAdaptor;
import org.redisson.api.RedissonClient;

/**
 * BiBlockedIpService
 * @see DefaultRedisBlockedAdaptor
 * @author qy
 * @date 2021-08-03 11:25
 */
public class BiBlockedIpRedisService extends DefaultRedisBlockedAdaptor {

    public static String NAME = "BiBlock";

    public BiBlockedIpRedisService(RedissonClient redissonClient) {
        super(ProjectContextInfo.BI_BLOCKED_IP_KEY, redissonClient);
    }
}
