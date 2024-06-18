package com.hqy.cloud.auth.limit.support;

import com.hqy.cloud.auth.limit.DefaultManualWhiteIpAdaptor;
import com.hqy.cloud.common.base.project.ProjectContextInfo;
import org.redisson.api.RedissonClient;

/**
 * ManualWhiteIpService.
 * @see DefaultManualWhiteIpAdaptor
 * @author qy
 * @date 2021/9/14 23:30
 */
public class ManualWhiteIpRedisService extends DefaultManualWhiteIpAdaptor  {

    public ManualWhiteIpRedisService(RedissonClient redisson) {
        super(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY, redisson);
        initializeWhiteIp(false);
    }


}
