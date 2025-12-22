package com.hqy.cloud.auth.api;

import com.alibaba.fastjson2.JSON;
import com.hqy.cloud.auth.core.AuthorizationResourceRepository;
import com.hqy.cloud.auth.utils.AuthUtils;
import com.hqy.cloud.auth.utils.StaticEndpointAuthorizationManager;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 抽象权限校验模板类.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAuthPermissionService implements AuthPermissionService {

    private final AuthorizationResourceRepository authorizationResourceRepository;

    @Override
    public final boolean isPermitRequest(AuthenticationRequest request) {
        AssertUtil.notNull(request, "AuthenticationRequest should not be null.");
        // option请求放行.
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.method())) {
            return true;
        }
        // 判断是否是白名单请求
        if (isWhiteRequest(request)) {
            return true;
        }

        // 获取用户允许访问的权限， 这里配置的是用户角色
        List<String> authorities = request.authorities();
        if (CollectionUtils.isEmpty(authorities)) {
            log.warn("AuthenticationRequest has no authorities, request:{}", JSON.toJSONString(request));
            return false;
        }

        return authorizationResourceRepository.authenticate(request);
    }

    private boolean isWhiteRequest(AuthenticationRequest request) {
        String requestUri = request.requestUri();
        //是否是静态的端点访问uri || 是否是白名单IP || 是业务允许通过的uri
        return isWhiteStaticEndpoint(requestUri) || isWhiteAccessIp(request.requestIp()) || isBusinessWhiteAccessUri(requestUri);
    }


    @Override
    public boolean hasAuthorities(String... authorities) {
        // 获取当前登录用户权限
        List<String> currentAuthorities = AuthUtils.getCurrentAuthorities();
        return new HashSet<>(currentAuthorities).containsAll(List.of(authorities));
    }

    @Override
    public List<String> getBusinessWhiteUris() {
        return new ArrayList<>(authorizationResourceRepository.getIgnoredAccessTokenUri());
    }


    protected boolean isWhiteStaticEndpoint(String requestUri) {
        if (StringUtils.isBlank(requestUri)) {
            return false;
        }
        return StaticEndpointAuthorizationManager.getInstance().isStaticWhiteEndpoint(requestUri);
    }


    /**
     * 是否是白名单ip
     * @param requestIp 请求ip
     * @return          result.
     */
    protected abstract boolean isWhiteAccessIp(String requestIp);

    /**
     * 是否是业务白名单uri
     * @param requestUri request uri
     * @return           result.
     */
    protected boolean isBusinessWhiteAccessUri(String requestUri) {
        try {
            List<String> whiteUris = this.getBusinessWhiteUris();
            return StaticEndpointAuthorizationManager.getInstance().isMatch(whiteUris, requestUri);
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }


}
