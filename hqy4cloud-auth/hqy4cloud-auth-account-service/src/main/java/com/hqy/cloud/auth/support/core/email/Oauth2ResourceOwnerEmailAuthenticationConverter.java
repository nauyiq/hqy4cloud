package com.hqy.cloud.auth.support.core.email;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.auth.common.SecurityConstants;
import com.hqy.cloud.auth.security.common.Oauth2EndpointUtils;
import com.hqy.cloud.auth.support.base.Oauth2ResourceOwnerBaseAuthenticationConverter;
import com.hqy.cloud.infrastructure.random.RandomCodeService;
import com.hqy.cloud.infrastructure.random.RedisRandomCodeService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Set;

/**
 * 邮箱认证转换器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/17 17:32
 */
public class Oauth2ResourceOwnerEmailAuthenticationConverter extends Oauth2ResourceOwnerBaseAuthenticationConverter<Oauth2ResourceOwnerEmailAuthenticationToken> {
    RandomCodeService randomCodeService = new RedisRandomCodeService();

    @Override
    public boolean support(String grantType) {
        return SecurityConstants.EMAIL.equals(grantType);
    }

    @Override
    public Oauth2ResourceOwnerEmailAuthenticationToken buildToken(Authentication clientPrincipal, Set<String> requestedScopes, Map<String, Object> additionalParameters) {
        return new Oauth2ResourceOwnerEmailAuthenticationToken(new AuthorizationGrantType(SecurityConstants.EMAIL),
                clientPrincipal, requestedScopes, additionalParameters);
    }

    @Override
    public void checkParams(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = Oauth2EndpointUtils.getParameters(request);
        String email = parameters.getFirst(SecurityConstants.EMAIL_PARAMETER_NAME);
        String code = parameters.getFirst(SecurityConstants.CODE_PARAMETER_NAME);
        if (StringUtils.isAnyBlank(email, code) || parameters.get(SecurityConstants.EMAIL_PARAMETER_NAME).size() != 1 ||
                parameters.get(SecurityConstants.CODE_PARAMETER_NAME).size() != 1) {
            Oauth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, SecurityConstants.EMAIL_PARAMETER_NAME,
                    Oauth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        if (!randomCodeService.isExist(StrUtil.EMPTY, email, code)) {
            Oauth2EndpointUtils.throwError(SecurityConstants.INVALID_REQUEST_CODE, SecurityConstants.INVALID_REQUEST_CODE,
                    Oauth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }


    }
}
