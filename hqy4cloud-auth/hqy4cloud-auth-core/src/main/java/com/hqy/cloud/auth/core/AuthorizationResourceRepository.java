package com.hqy.cloud.auth.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.hqy.cloud.auth.api.AuthenticationRequest;
import com.hqy.cloud.auth.common.AuthorizationResourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 权限认证资源仓库
 * <pre>
 *     基于redis实现
 * </pre>
 * @author hongqy
 * @date 2025/12/15
 */
@Slf4j
@Component
public class AuthorizationResourceRepository {
    private final RMap<String, Set<String>> authorizationResourceMap;
    private final RSet<String> ignoredAccessTokenUriList;
    private final RSet<String> identifierTokenUriList;

    public AuthorizationResourceRepository(RedissonClient redissonClient) {
        authorizationResourceMap = redissonClient.getMap("GLOBAL_AUTHORIZATION_RESOURCE");
        ignoredAccessTokenUriList = redissonClient.getSet("GLOBAL_IGNORED_ACCESS_TOKEN_API");
        identifierTokenUriList = redissonClient.getSet("GLOBAL_IDENTIFIER_TOKEN_API");
    }

    /**
     * 注册权限认证资源
     * @param authorizationResourceDTO
     */
    public void registerAuthorizationResource(AuthorizationResourceDTO authorizationResourceDTO) {
        Assert.notNull(authorizationResourceDTO, "authorizationResource must not be null");
        log.info("register authorization resource: {}", JSON.toJSONString(authorizationResourceDTO));
        Set<String> oldResources = authorizationResourceMap.get(authorizationResourceDTO.getId());
        Set<String> resources = CollectionUtils.isEmpty(oldResources) ? authorizationResourceDTO.getAuthorities() : CollUtil.unionDistinct(oldResources, authorizationResourceDTO.getAuthorities());
        authorizationResourceMap.put(authorizationResourceDTO.getId(), resources);
    }

    /**
     * 注册忽略token的uri
     * @param uri
     */
    public void registerIgnoredAccessTokenUri(String uri) {
        ignoredAccessTokenUriList.add(uri);
    }

    /**
     * 判断当前请求的uri是否被忽略token
     * @param uri
     * @return
     */
    public boolean isIgnoredAccessTokenApi(String uri) {
        return ignoredAccessTokenUriList.contains(uri);
    }

    public Set<String> getIgnoredAccessTokenUri() {
        return ignoredAccessTokenUriList;
    }


    public Set<String> getIdentifierTokenUri() {
        return identifierTokenUriList;
    }

    /**
     * 注册需要校验全局幂等的uri
     * @param uri
     */
    public void registerIdentifierTokenUri(String uri) {
        identifierTokenUriList.add(uri);
    }

    /**
     * 判断当前请求的uri是否需要校验全局幂等
     * @param uri
     * @return
     */
    public boolean isIdentifierTokenApi(String uri) {
        return identifierTokenUriList.contains(uri);
    }


    /**
     * 判断当前请求是否可以被访问
     * @param request
     * @return
     */
    public boolean authenticate(AuthenticationRequest request) {
        return authenticate(request.requestUri(), request.method(), request.authorities());
    }

    /**
     * 判断当前请求是否可以被访问
     * @param uri    请求URI
     * @param method 请求方法
     * @param authorities 拥有的权限列表
     * @return
     */
    public boolean authenticate(String uri, String method, List<String> authorities) {
        String resourceId = method + StrUtil.UNDERLINE + uri;
        Set<String> needAuthorities = authorizationResourceMap.get(resourceId);
        if (CollectionUtils.isEmpty(needAuthorities)) {
            return false;
        }
        return CollUtil.containsAny(authorities, needAuthorities);
    }


}
