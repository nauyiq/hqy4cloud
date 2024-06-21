package com.hqy.cloud.auth.api;

import com.hqy.cloud.auth.common.AuthConstants;
import com.hqy.cloud.auth.common.AuthenticationModuleInfo;
import com.hqy.cloud.auth.utils.StaticEndpointAuthorizationManager;
import com.hqy.cloud.common.base.AuthenticationInfo;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.authentication.AuthenticationRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Set;

/**
 * 抽象权限校验模板类.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAuthPermissionService implements AuthPermissionService {

    private final Environment environment;
    private final AuthoritiesRoleService authoritiesRoleService;


    @Override
    public final boolean isPermitRequest(AuthenticationRequest request) {
        AssertUtil.notNull(request, "AuthenticationRequest should not be null.");
        // option请求放行.
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.method())) {
            return false;
        }
        // 判断是否是白名单请求
        if (isWhiteRequest(request)) {
            return true;
        }
        // 基于RBAC进行鉴权. 通过该请求对应的用户是什么角色, 那些角色能访问什么权限.
        List<String> authorities = request.authorities();
        if (CollectionUtils.isEmpty(authorities)) {
            return false;
        }
        return checkAuthoritiesRequest(authorities, request);
    }

    private boolean isWhiteRequest(AuthenticationRequest request) {
        String requestUri = request.requestUri();
        //是否是静态的端点访问uri || 是否是白名单IP || 是业务允许通过的uri
        return isWhiteStaticEndpoint(requestUri) || isWhiteAccessIp(request.requestIp()) || isBusinessWhiteAccessUri(requestUri);
    }

    @Override
    public boolean havePermissions(String... permissions) {
        AuthenticationInfo authentication = AuthenticationRequestContext.getAuthentication();
        List<String> roles = authentication.getRoles();
        Set<String> permissionsByAuthorities = authoritiesRoleService.loadAuthenticationPermissionsByAuthorities(roles);
        if (CollectionUtils.isEmpty(permissionsByAuthorities)) {
            return false;
        }
        return permissionsByAuthorities.containsAll(List.of(permissions));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getWhiteUris() {
        return this.environment.getProperty(AuthConstants.BUSINESS_WHITE_URIS_KEY, List.class, AuthConstants.DEFAULT_BUSINESS_WHITE_URIS);
    }

    protected boolean checkAuthoritiesRequest(List<String> authorities, AuthenticationRequest request) {
        // 权限角色信息.
        List<AuthenticationModuleInfo> moduleInfos = authoritiesRoleService.loadAuthenticationModulesByAuthorities(authorities);
        if (CollectionUtils.isEmpty(moduleInfos)) {
            return false;
        }
        String method = request.method();
        String requestUri = request.requestUri();
        AntPathMatcher antPathMatcher = StaticEndpointAuthorizationManager.getInstance().getAntPathMatcher();
        // 只有任意一个角色，权限可以访问该uri 则放行该请求。
        return moduleInfos.parallelStream().anyMatch(moduleInfo -> {
            List<AuthenticationModuleInfo.ModuleInfo> infos = moduleInfo.getModuleInfos();
            if (CollectionUtils.isEmpty(infos)) {
                return false;
            }
            return infos.parallelStream().anyMatch(info -> {
                if (StringUtils.isBlank(info.getMethod())) {
                    return antPathMatcher.match(info.getModuleExpression(), requestUri);
                } else {
                    // 如果配置了请求方式，则校验一下配置的方法请求方式是否包含了本次请求的方法请求方式
                    return info.getMethod().toLowerCase().contains(method.toLowerCase()) && antPathMatcher.match(info.getModuleExpression(), requestUri);
                }
            });
        });
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
            List<String> whiteUris = this.getWhiteUris();
            return StaticEndpointAuthorizationManager.getInstance().isMatch(whiteUris, requestUri);
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            return false;
        }
    }


}
