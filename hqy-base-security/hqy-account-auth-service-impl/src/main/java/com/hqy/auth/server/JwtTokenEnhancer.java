package com.hqy.auth.server;

import cn.hutool.core.bean.BeanUtil;
import com.hqy.auth.dto.SecurityUserDTO;
import com.hqy.auth.dto.UserJwtPayloadDTO;
import com.hqy.util.AssertUtil;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * jwt token增强器
 * @author qiyuan.hong
 * @date 2022-03-14 14:01
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {


    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        SecurityUserDTO securityUserDTO = (SecurityUserDTO)authentication.getPrincipal();
        AssertUtil.notNull(securityUserDTO, "JwtTokenEnhancer enhance failure. principal convert securityUserDTO is null.");
        ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(userConvertToMap(securityUserDTO));
        return accessToken;
    }

    private Map<String, Object> userConvertToMap(SecurityUserDTO securityUser) {
        UserJwtPayloadDTO userJwtPayloadDTO = new UserJwtPayloadDTO(securityUser.getId(), securityUser.getPassword(), securityUser.getEmail(), securityUser.getUsername(), securityUser.getAuthorities());
        return BeanUtil.beanToMap(userJwtPayloadDTO);
    }
}
