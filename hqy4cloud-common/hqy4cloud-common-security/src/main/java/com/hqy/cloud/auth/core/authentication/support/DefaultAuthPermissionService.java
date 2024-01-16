package com.hqy.cloud.auth.core.authentication.support;

import com.hqy.cloud.auth.core.authentication.RoleAuthenticationService;
import com.hqy.cloud.auth.core.component.EndpointAuthorizationManager;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @see AbstractAuthPermissionService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 14:28
 */
public class DefaultAuthPermissionService extends AbstractAuthPermissionService {

    private final ManualWhiteIpService manualWhiteIpService;

    @Value("${oauth2.white.uri:''}")
    private List<String> whites;

    public DefaultAuthPermissionService(RoleAuthenticationService roleAuthenticationService, ManualWhiteIpService manualWhiteIpService) {
        super(roleAuthenticationService);
        this.manualWhiteIpService = manualWhiteIpService;
    }

    @Override
    protected boolean isWhiteAccessUri(String requestUri) {
        if (CollectionUtils.isEmpty(whites) || EndpointAuthorizationManager.getInstance().isAdminRequest(requestUri)) {
            return false;
        }
        return EndpointAuthorizationManager.getInstance().isMatch(whites, requestUri);
    }

    @Override
    protected boolean isWhiteAccessIp(String requestIp) {
        return manualWhiteIpService.isWhiteIp(requestIp);
    }

    @Override
    public List<String> getWhites() {
        return whites;
    }




}
