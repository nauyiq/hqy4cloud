package com.hqy.auth.server;

import com.hqy.auth.dto.SecurityUserDTO;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @date 2022-03-14 14:01
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {


    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        SecurityUserDTO securityUserDTO = (SecurityUserDTO)authentication.getPrincipal();
        Map<String, Object> data = new HashMap<>(4);
        data.put("id", securityUserDTO.getId());
        data.put("status", securityUserDTO.getStatus());
        ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(data);
        return accessToken;
    }
}
