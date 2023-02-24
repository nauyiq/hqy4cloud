package com.hqy.gateway.server;

import com.hqy.access.auth.support.EndpointAuthorizationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;

/**
 * AbstractCodeAuthorizationChecker.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/21 16:55
 */
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public abstract class AbstractCodeAuthorizationChecker implements CodeAuthorizationChecker {

    private final AbstractCodeServer codeServer;
    @Value("${code.check.uris:'/admin/oauth/token'}")
    private List<String> needCheckCodeUris;

    @Override
    public boolean checkCode(String resource, String key, String code) {
        if (resourceNeedCheck(resource)) {
            String serverCode = codeServer.getCode(key);
            return StringUtils.isNotBlank(serverCode) && serverCode.equalsIgnoreCase(code);
        }
        return false;
    }

    public boolean resourceNeedCheck(String resource) {
        if (CollectionUtils.isEmpty(needCheckCodeUris)) {
            return false;
        }
        return EndpointAuthorizationManager.getInstance().isMatch(needCheckCodeUris, resource);
    }
}
