package com.hqy.cloud.auth.support.core.sms;

import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.auth.common.SecurityConstants;
import com.hqy.cloud.auth.security.common.Oauth2EndpointUtils;
import com.hqy.cloud.auth.support.base.Oauth2ResourceOwnerBaseAuthenticationConverter;
import com.hqy.cloud.infrastructure.random.RandomCodeScene;
import com.hqy.cloud.infrastructure.random.RandomCodeService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Set;

/**
 * 手机验证认证转换器
 * @author qiyuan.hong
 * @date 2024/7/11
 */
public class Oauth2ResourceOwnerSmsAuthenticationConverter extends Oauth2ResourceOwnerBaseAuthenticationConverter<Oauth2ResourceOwnerSmsAuthenticationToken> {


    @Override
    public boolean support(String grantType) {
        return SecurityConstants.SMS.equalsIgnoreCase(grantType);
    }

    @Override
    public Oauth2ResourceOwnerSmsAuthenticationToken buildToken(Authentication clientPrincipal, Set<String> requestedScopes, Map<String, Object> additionalParameters) {
        return new Oauth2ResourceOwnerSmsAuthenticationToken(new AuthorizationGrantType(SecurityConstants.SMS), clientPrincipal, requestedScopes, additionalParameters);
    }

    @Override
    public void checkParams(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = Oauth2EndpointUtils.getParameters(request);
        // 手机号码
        String phone = parameters.getFirst(SecurityConstants.PHONE_PARAMETER_NAME);
        // 验证码
        String code = parameters.getFirst(SecurityConstants.CODE_PARAMETER_NAME);

        // 校验入参
        if (StringUtils.isAnyBlank(phone, code)) {
            Oauth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, SecurityConstants.PHONE_PARAMETER_NAME,
                    Oauth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        // 判断验证码是否存在
        RandomCodeService service = SpringUtil.getBean(RandomCodeService.class);
        if (!service.isExist(code, RandomCodeScene.SMS_AUTH.PARAMS)) {
            Oauth2EndpointUtils.throwError(SecurityConstants.INVALID_REQUEST_CODE, SecurityConstants.INVALID_REQUEST_CODE,
                    Oauth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
    }
}
