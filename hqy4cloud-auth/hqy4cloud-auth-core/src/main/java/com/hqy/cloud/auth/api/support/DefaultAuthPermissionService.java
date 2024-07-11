package com.hqy.cloud.auth.api.support;

import com.hqy.cloud.auth.api.AbstractAuthPermissionService;
import com.hqy.cloud.limiter.api.ManualWhiteIpService;
import org.springframework.core.env.Environment;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
public class DefaultAuthPermissionService extends AbstractAuthPermissionService {
    private final ManualWhiteIpService manualWhiteIpService;

    public DefaultAuthPermissionService(Environment environment, ManualWhiteIpService manualWhiteIpService) {
        super(environment);
        this.manualWhiteIpService = manualWhiteIpService;
    }

    @Override
    protected boolean isWhiteAccessIp(String requestIp) {
        return this.manualWhiteIpService.isWhiteIp(requestIp);
    }

}
