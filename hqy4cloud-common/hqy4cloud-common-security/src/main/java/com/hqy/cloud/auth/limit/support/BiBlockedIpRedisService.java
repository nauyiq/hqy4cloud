package com.hqy.cloud.auth.limit.support;

import com.hqy.cloud.auth.limit.DefaultRedisBlockedAdaptor;
import com.hqy.cloud.util.spring.ProjectContextInfo;

/**
 * BiBlockedIpService
 * @see DefaultRedisBlockedAdaptor
 * @author qy
 * @date 2021-08-03 11:25
 */
public class BiBlockedIpRedisService extends DefaultRedisBlockedAdaptor {

    public static String NAME = "BiBlock";

    public BiBlockedIpRedisService(boolean startScheduled) {
        super(ProjectContextInfo.BI_BLOCKED_IP_KEY, startScheduled);
    }
}
