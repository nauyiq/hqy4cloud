package com.hqy.cloud.auth.core.authentication.support;

import com.hqy.cloud.auth.core.authentication.RoleAuthenticationService;
import com.hqy.cloud.auth.core.component.EndpointAuthorizationManager;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;

/**
 * @see AbstractAuthPermissionService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 14:28
 */
@RefreshScope
public class DefaultAuthPermissionService extends AbstractAuthPermissionService {

    private final ManualWhiteIpService manualWhiteIpService;

    @Value("${oauth2.white.uri:''}")
    private List<String> oauth2WhiteAccessUriList;

    public DefaultAuthPermissionService(RoleAuthenticationService roleAuthenticationService, ManualWhiteIpService manualWhiteIpService) {
        super(roleAuthenticationService);
        this.manualWhiteIpService = manualWhiteIpService;
    }

    @Override
    protected boolean isWhiteAccessUri(String requestUri) {
        if (CollectionUtils.isEmpty(oauth2WhiteAccessUriList) || EndpointAuthorizationManager.getInstance().isAdminRequest(requestUri)) {
            return false;
        }
        return EndpointAuthorizationManager.getInstance().isMatch(oauth2WhiteAccessUriList, requestUri);
    }

    @Override
    protected boolean isWhiteAccessIp(String requestIp) {
        return manualWhiteIpService.isWhiteIp(requestIp);
    }




}
