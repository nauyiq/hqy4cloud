package com.hqy.cloud.auth.server.support;

import com.hqy.cloud.auth.server.AbstractOauth2Access;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;

/**
 * NacosReactRequestOath2Access.
 * @see AbstractOauth2Access
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 14:28
 */
@RefreshScope
@RequiredArgsConstructor
public class NacosOauth2Access extends AbstractOauth2Access {

    private final ManualWhiteIpService manualWhiteIpService;

    @Value("${oauth2.white.uri:''}")
    private List<String> oauth2WhiteAccessUriList;


    @Override
    protected boolean isWhiteAccessUri(String requestUri) {
        if (CollectionUtils.isEmpty(oauth2WhiteAccessUriList)) {
            return false;
        }
        if (EndpointAuthorizationManager.getInstance().isAdminRequest(requestUri)) {
            return false;
        }

        return EndpointAuthorizationManager.getInstance().isMatch(oauth2WhiteAccessUriList, requestUri);
    }

    @Override
    protected boolean isWhiteAccessIp(String requestIp) {
        return manualWhiteIpService.isWhiteIp(requestIp);
    }




}
