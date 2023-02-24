package com.hqy.access.limit.service.support;

import com.hqy.access.limit.service.DefaultRedisBlockedAdaptor;
import com.hqy.util.spring.ProjectContextInfo;

/**
 * BiBlockedIpService
 * @see com.hqy.access.limit.service.DefaultRedisBlockedAdaptor
 * @author qy
 * @date 2021-08-03 11:25
 */
public class BiBlockedIpRedisService extends DefaultRedisBlockedAdaptor {

    public static String NAME = "BiBlock";

    public BiBlockedIpRedisService(boolean startScheduled) {
        super(ProjectContextInfo.BI_BLOCKED_IP_KEY, startScheduled);
    }
}
