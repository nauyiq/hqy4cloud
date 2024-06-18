package com.hqy.cloud.auth.support.base;

import com.hqy.cloud.auth.security.common.Oauth2EndpointUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义模式认证转换器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 14:58
 */
public abstract class Oauth2ResourceOwnerBaseAuthenticationConverter<T extends Oauth2ResourceOwnerBaseAuthenticationToken> implements AuthenticationConverter {

    /**
     * 是否支持此converter
     * @param grantType 授权类型
     * @return          result.
     */
    public abstract boolean support(String grantType);

    /**
     * 校验参数
     * @param request HttpServletRequest.
     */
    public void checkParams(HttpServletRequest request) {

    }

    /**
     * 构建具体类型的token
     * @param clientPrincipal      客户端身份认证对象
     * @param requestedScopes      请求范围
     * @param additionalParameters 其他参数.
     * @return                     token obj.
     */
    public abstract T buildToken(Authentication clientPrincipal, Set<String> requestedScopes,
                                 Map<String, Object> additionalParameters);


    @Override
    public Authentication convert(HttpServletRequest request) {
        // grant_type (REQUIRED)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!support(grantType)) {
            return null;
        }

        //request params.
        MultiValueMap<String, String> parameters = Oauth2EndpointUtils.getParameters(request);
        // scope (OPTIONAL)
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            Oauth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.SCOPE,
                    Oauth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }

        // 校验个性化参数
        checkParams(request);

        // 获取当前已经认证的客户端信息
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (clientPrincipal == null) {
            Oauth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ErrorCodes.INVALID_CLIENT,
                    Oauth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        // 扩展信息
        Map<String, Object> additionalParameters = parameters.entrySet().stream()
                .filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE)
                        && !e.getKey().equals(OAuth2ParameterNames.SCOPE))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

        // 创建token
        return buildToken(clientPrincipal, requestedScopes, additionalParameters);
    }
}
