package com.hqy.access.limit.service.support;

import com.hqy.access.limit.service.DefaultRedisBlockedAdaptor;
import com.hqy.util.spring.ProjectContextInfo;

/**
 * RedisManualBlockedIpService
 * @see com.hqy.access.limit.service.DefaultRedisBlockedAdaptor
 * 基于redis ip人工 黑名单 服务
 * @author qy
 * @date 2021-08-02 10:51
 */
public class ManualBlockedIpService extends DefaultRedisBlockedAdaptor {

    public static String NAME = " ManualBlock";

    public ManualBlockedIpService(boolean startScheduled) {
        super(ProjectContextInfo.MANUAL_BLOCKED_IP_KEY, startScheduled);
    }
}
