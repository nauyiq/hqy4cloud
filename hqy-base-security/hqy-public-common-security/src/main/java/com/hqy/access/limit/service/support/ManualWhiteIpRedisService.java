package com.hqy.access.limit.service.support;

import com.hqy.access.limit.service.DefaultManualWhiteIpAdaptor;
import com.hqy.util.spring.ProjectContextInfo;
import org.redisson.api.RedissonClient;

/**
 * ManualWhiteIpService.
 * @see com.hqy.access.limit.service.DefaultManualWhiteIpAdaptor
 * @author qy
 * @date 2021/9/14 23:30
 */
public class ManualWhiteIpRedisService extends DefaultManualWhiteIpAdaptor  {

    public ManualWhiteIpRedisService(RedissonClient redisson) {
        super(ProjectContextInfo.WHITE_IP_PROPERTIES_KEY, redisson);
        initializeWhiteIp(false);
    }


}
