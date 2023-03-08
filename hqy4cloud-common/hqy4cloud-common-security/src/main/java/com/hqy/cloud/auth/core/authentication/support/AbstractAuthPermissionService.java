package com.hqy.cloud.auth.core.authentication.support;

import com.hqy.cloud.auth.base.dto.ResourceConfigDTO;
import com.hqy.cloud.auth.base.dto.RoleAuthenticationDTO;
import com.hqy.cloud.auth.core.authentication.RoleAuthenticationService;
import com.hqy.cloud.auth.core.authentication.AuthPermissionService;
import com.hqy.cloud.auth.core.authentication.AuthenticationRequest;
import com.hqy.cloud.auth.core.component.EndpointAuthorizationManager;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.foundation.common.authentication.AuthenticationRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * AbstractOath2Access.
 * @see AuthPermissionService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 14:02
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAuthPermissionService implements AuthPermissionService {
    private final RoleAuthenticationService roleAuthenticationService;

    @Override
    public final boolean isPermitRequest(List<String> authorities, AuthenticationRequest request) {
        if (CollectionUtils.isEmpty(authorities) || Objects.isNull(request)) {
            return false;
        }
        String requestUri = request.requestUri();

        //是否是静态的端点访问uri || 是否是白名单IP || 是业务允许通过的uri
        if (isWhiteStaticEndpoint(requestUri) || isWhiteAccessIp(request.requestIp()) || isWhiteAccessUri(requestUri)) {
            return true;
        }

        //如果是admin服务的请求，将请求权限校验下沉到服务的aop去
        if (EndpointAuthorizationManager.getInstance().isAdminRequest(request.requestUri())) {
            return true;
        }

        //校验是否权限访问.
        return checkAuthoritiesRequest(authorities, request);
    }


    @Override
    public boolean havePermissions(String... permissions) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication();
        List<String> roles = authentication.getRoles();
        List<RoleAuthenticationDTO> authenticationByRoles = roleAuthenticationService.getAuthenticationByRoles(roles);
        if (CollectionUtils.isEmpty(authenticationByRoles)) {
            return false;
        }
        return authenticationByRoles.stream().map(RoleAuthenticationDTO::getPermissions).anyMatch(permissionsConfig -> permissionsConfig.containsAll(Arrays.asList(permissions)));
    }

    protected boolean isWhiteStaticEndpoint(String requestUri) {
        if (StringUtils.isBlank(requestUri)) {
            return false;
        }
        return EndpointAuthorizationManager.getInstance().isStaticWhiteEndpoint(requestUri);
    }

    protected boolean checkAuthoritiesRequest(List<String> authorities, AuthenticationRequest request) {
        List<RoleAuthenticationDTO> authentications = roleAuthenticationService.getAuthenticationByRoles(authorities);
        if (CollectionUtils.isEmpty(authentications)) {
            log.warn("Not found authentications:{}.", authentications);
            return false;
        }
        return authentications.stream().map(RoleAuthenticationDTO::getResourceConfigs).anyMatch(resource -> checkMatch(request, resource));
    }

    private boolean checkMatch(AuthenticationRequest request, List<ResourceConfigDTO> resources) {
        if (CollectionUtils.isEmpty(resources)) {
            return false;
        }

        String uri = request.requestUri();
        AntPathMatcher antPathMatcher = EndpointAuthorizationManager.getInstance().getAntPathMatcher();
        return resources.stream().anyMatch(resource -> {
            String method = resource.getMethod();
            if (StringUtils.isBlank(method)) {
                return antPathMatcher.match(resource.getPath(), uri);
            }
            return request.method().equals(method) && antPathMatcher.match(resource.getPath(), uri);
        });
    }


    /**
     * 是否是白名单ip
     * @param requestIp 请求ip
     * @return          result.
     */
    protected abstract boolean isWhiteAccessIp(String requestIp);

    /**
     * 是否是白名单uri
     * @param requestUri request uri
     * @return           result.
     */
    protected abstract boolean isWhiteAccessUri(String requestUri);


}
