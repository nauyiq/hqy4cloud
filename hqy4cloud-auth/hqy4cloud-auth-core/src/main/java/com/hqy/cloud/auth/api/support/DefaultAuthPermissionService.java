package com.hqy.cloud.auth.api.support;

import com.hqy.cloud.auth.api.AbstractAuthPermissionService;
import com.hqy.cloud.auth.core.AuthorizationResourceRepository;
import com.hqy.cloud.limiter.api.ManualWhiteIpService;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/21
 */
public class DefaultAuthPermissionService extends AbstractAuthPermissionService {
    private final ManualWhiteIpService manualWhiteIpService;

    public DefaultAuthPermissionService(AuthorizationResourceRepository authorizationResourceRepository, ManualWhiteIpService manualWhiteIpService) {
        super(authorizationResourceRepository);
        this.manualWhiteIpService = manualWhiteIpService;
    }

    @Override
    protected boolean isWhiteAccessIp(String requestIp) {
        return this.manualWhiteIpService.isWhiteIp(requestIp);
    }

}
